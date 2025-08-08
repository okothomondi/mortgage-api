package com.okoth.mortgage.services;

import static com.okoth.mortgage.models.enums.ApplicationStatus.PENDING;
import static com.okoth.mortgage.models.enums.ApplicationStatus.REJECTED;
import static com.okoth.mortgage.models.enums.Role.APPLICANT;
import static com.okoth.mortgage.models.enums.Role.OFFICER;

import com.okoth.mortgage.exceptions.LoanException;
import com.okoth.mortgage.exceptions.UnauthorizedException;
import com.okoth.mortgage.models.custom.AppResponseBody;
import com.okoth.mortgage.models.custom.ApplicationEvent;
import com.okoth.mortgage.models.custom.CustomerScore;
import com.okoth.mortgage.models.custom.LoanRequest;
import com.okoth.mortgage.models.db.Application;
import com.okoth.mortgage.models.db.Decision;
import com.okoth.mortgage.models.db.LoanRepayment;
import com.okoth.mortgage.models.db.User;
import com.okoth.mortgage.models.dto.ApplicationDto;
import com.okoth.mortgage.models.dto.ApplicationListResponse;
import com.okoth.mortgage.models.dto.ApplicationResponse;
import com.okoth.mortgage.models.dto.DecisionDto;
import com.okoth.mortgage.models.enums.ApplicationStatus;
import com.okoth.mortgage.models.enums.DecisionStatus;
import com.okoth.mortgage.repositories.ApplicationRepository;
import com.okoth.mortgage.repositories.LoanRepaymentRepository;
import com.okoth.mortgage.repositories.UserRepository;
import com.okoth.mortgage.services.externals.ScoringEngineService;
import com.okoth.mortgage.services.kafka.QueueProducer;
import com.okoth.mortgage.spring.security.SecurityContext;
import io.opentelemetry.api.trace.Span;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoansService {
  private @Value("${loan.application.limit}") Double limit;

  private final QueueProducer queueProducer;
  private final UserRepository userRepository;
  private final DocumentService documentService;
  private final SecurityContext securityContext;
  private final DecisionService decisionService;
  private final ScoringEngineService scoringEngineService;
  private final ApplicationRepository applicationRepository;
  private final LoanRepaymentRepository loanRepaymentRepository;
  private final ExecutorService executorService = Executors.newCachedThreadPool();
  protected final BlockingQueue<LoanRequest> blockingQueue = new LinkedBlockingQueue<>();

  @PostConstruct
  public void init() {
    IntStream.range(0, Runtime.getRuntime().availableProcessors() * 2)
        .forEach(i -> executorService.submit(this::consume));
  }

  public void apply(LoanRequest request) {
    try {
      blockingQueue.put(request);
    } catch (Exception e) {
      log.error("Failed to apply({}) {}", request, e.getMessage());
    }
  }

  @Transactional
  public ApplicationResponse createApplication(String id, ApplicationDto applicationDto) {
    User user =
        userRepository
            .findByNationalId(id)
            .orElseThrow(() -> new ResourceNotFoundException("Member ID did not match"));
    return submitApplication(applicationDto, user);
  }

  @Transactional
  public ApplicationResponse createApplication(ApplicationDto applicationDto) {
    return submitApplication(applicationDto, securityContext.getCurrentUser());
  }

  public AppResponseBody getStatus() {
    AppResponseBody response = new AppResponseBody();
    User user = securityContext.getCurrentUser();
    Application application = getStatus(user.getUsername());
    response.setApplication(application);
    return response;
  }

  public Application getStatus(String customerNumber) {
    Optional<User> user = userRepository.findByNationalId(customerNumber);
    if (user.isEmpty()) throw new LoanException("USER_NOT_FOUND");
    Optional<Application> loan = applicationRepository.findActiveByCustomerNumber(user.get());
    if (loan.isEmpty()) throw new LoanException("LOAN_NOT_FOUND");
    return loan.get();
  }

  public ApplicationResponse getApplicationById(UUID id) {
    Application application =
        applicationRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("APPLICATION_NOT_FOUND"));
    User user = securityContext.getCurrentUser();
    if (user.getRole().equals(APPLICANT)
        && !application.getAssigneeId().getUsername().equals(user.getUsername()))
      throw new UnauthorizedException("ACTION_NOT_PERMITTED");
    return ApplicationResponse.builder().application(new ApplicationDto(application)).build();
  }

  public ApplicationListResponse getAllApplications(
      Integer page,
      Integer size,
      String sortBy,
      String sortDir,
      ApplicationStatus status,
      LocalDateTime createdFrom,
      LocalDateTime createdTo) {
    User user = securityContext.getCurrentUser();
    return getAllApplications(
        page, size, sortBy, sortDir, status, user.getNationalId(), createdFrom, createdTo);
  }

  public ApplicationListResponse getAllApplications(
      Integer page,
      Integer size,
      String sortBy,
      String sortDir,
      ApplicationStatus status,
      String nationalId,
      LocalDateTime createdFrom,
      LocalDateTime createdTo) {

    Pageable pageable =
        PageRequest.of(
            page != null ? page : 0,
            size != null ? size : 20,
            Sort.by(
                sortDir != null && sortDir.equalsIgnoreCase("asc")
                    ? Sort.Direction.ASC
                    : Sort.Direction.DESC,
                sortBy != null ? sortBy : "createdAt"));

    Specification<Application> spec =
        (root, query, cb) -> {
          Predicate predicate = cb.conjunction();
          User user = securityContext.getCurrentUser();
          Join<Application, User> userJoin = root.join("assigneeId", JoinType.INNER);
          if (user.getRole().equals(APPLICANT))
            predicate = cb.and(predicate, cb.equal(userJoin.get("id"), user.getId()));
          if (nationalId != null && user.getRole().equals(OFFICER))
            predicate = cb.and(predicate, cb.equal(userJoin.get("nationalId"), nationalId));
          if (status != null) predicate = cb.and(predicate, cb.equal(root.get("status"), status));
          if (createdFrom != null && createdTo != null)
            predicate =
                cb.and(predicate, cb.between(root.get("createdAt"), createdFrom, createdTo));
          return predicate;
        };

    Page<Application> applicationPage = applicationRepository.findAll(spec, pageable);

    List<ApplicationDto> applicationDtos =
        applicationPage.getContent().stream().map(ApplicationDto::new).collect(Collectors.toList());

    return ApplicationListResponse.builder()
        .applications(applicationDtos)
        .page(applicationPage.getNumber())
        .size(applicationPage.getSize())
        .totalElements(applicationPage.getTotalElements())
        .totalPages(applicationPage.getTotalPages())
        .build();
  }

  @Transactional
  public ApplicationResponse updateApplicationDecision(UUID id, DecisionDto decisionDto) {
    Application application =
        applicationRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("APPLICATION_NOT_FOUND"));

    Decision decision =
        decisionService.createDecision(decisionDto, application, securityContext.getUsername());
    application.setDecision(decision);

    application.setStatus(
        decisionDto.getStatus() == DecisionStatus.APPROVED
            ? ApplicationStatus.APPROVED
            : ApplicationStatus.REJECTED);

    application.setUpdatedAt(LocalDateTime.now());
    Application updatedApplication = applicationRepository.save(application);

    String traceId = Span.current().getSpanContext().getTraceId();
    ApplicationEvent.Updated event =
        new ApplicationEvent.Updated(new ApplicationDto(updatedApplication), traceId);
    queueProducer.publishApplicationEvent(event);

    return ApplicationResponse.builder()
        .application(new ApplicationDto(updatedApplication))
        .build();
  }

  public AppResponseBody makePayment(UUID id, Double amount) {
    Optional<Application> loan = applicationRepository.findById(id);
    if (loan.isEmpty()) throw new LoanException("LOAN_NOT_FOUND");
    loanRepaymentRepository.save(new LoanRepayment(loan.get(), amount));
    return new AppResponseBody("Payment successful!");
  }

  private ApplicationResponse submitApplication(ApplicationDto applicationDto, User user) {
    Application application = applicationRepository.save(new Application(applicationDto, user));
    if (applicationDto.getDocuments() != null && !applicationDto.getDocuments().isEmpty())
      application.setDocuments(
          applicationDto.getDocuments().stream()
              .map(docDto -> documentService.createDocument(docDto, application))
              .collect(Collectors.toList()));
    String traceId = Span.current().getSpanContext().getTraceId();
    ApplicationEvent.Created event =
        new ApplicationEvent.Created(new ApplicationDto(application), traceId);
    queueProducer.publishApplicationEvent(event);
    return ApplicationResponse.builder().application(new ApplicationDto(application)).build();
  }

  private void consume() {
    try {
      while (!Thread.currentThread().isInterrupted()) {
        List<LoanRequest> batch = new ArrayList<>();
        blockingQueue.drainTo(batch, 50);
        if (batch.isEmpty()) {
          LoanRequest request = blockingQueue.poll(500, TimeUnit.MILLISECONDS);
          if (request != null) batch.add(request);
        } else batch.parallelStream().forEach(this::process);
      }
    } catch (InterruptedException e) {
      log.warn("Failed to consume() because: {}", e.getMessage());
      Thread.currentThread().interrupt();
    }
  }

  private void process(LoanRequest request) {
    userRepository
        .findByNationalId(request.getCustomerNumber())
        .ifPresent(
            customer -> {
              Application application = new Application(customer, request.getAmount(), PENDING);
              String id = customer.getNationalId();
              if (applicationRepository.findActiveByCustomerNumber(customer).isEmpty()) {
                scoringEngineService
                    .getScore(customer.getNationalId())
                    .publishOn(Schedulers.boundedElastic())
                    .doOnSuccess(
                        customerScore -> {
                          application.setScore(customerScore.getScore());
                          application.setLimitAmount(customerScore.getLimitAmount());
                          if (customerScore.getScore() < limit) {
                            application.setStatus(REJECTED);
                            application.setExclusionReason(
                                "Score is below ".concat(limit.toString()));
                          } else application.setExclusion(customerScore.getExclusion());
                          applicationRepository.save(application);
                        })
                    .onErrorResume(
                        throwable -> {
                          log.error(
                              "Error in scoringEngineService.getScore({}) : {}",
                              id,
                              throwable.getMessage());
                          return Mono.just(new CustomerScore());
                        })
                    .subscribe();
              } else {
                application.setStatus(REJECTED);
                applicationRepository.save(application);
              }
            });
  }
}
