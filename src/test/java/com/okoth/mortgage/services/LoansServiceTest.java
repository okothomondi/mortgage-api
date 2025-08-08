package com.okoth.mortgage.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.okoth.mortgage.exceptions.LoanException;
import com.okoth.mortgage.models.custom.ApplicationEvent;
import com.okoth.mortgage.models.db.Application;
import com.okoth.mortgage.models.db.User;
import com.okoth.mortgage.models.dto.ApplicationDto;
import com.okoth.mortgage.models.dto.ApplicationResponse;
import com.okoth.mortgage.models.enums.EventType;
import com.okoth.mortgage.repositories.ApplicationRepository;
import com.okoth.mortgage.repositories.LoanRepaymentRepository;
import com.okoth.mortgage.repositories.UserRepository;
import com.okoth.mortgage.services.externals.ScoringEngineService;
import com.okoth.mortgage.services.kafka.QueueProducer;
import com.okoth.mortgage.spring.security.SecurityContext;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import java.util.Optional;
import java.util.UUID;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

class LoansServiceTest {

  @Mock private QueueProducer queueProducer;
  @Mock private UserRepository userRepository;
  @Mock private DocumentService documentService;
  @Mock private SecurityContext securityContext;
  @Mock private ApplicationRepository applicationRepository;
  @Mock private LoanRepaymentRepository loanRepaymentRepository;
  @Mock private ScoringEngineService scoringEngineService;

  @InjectMocks private LoansService loansService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testCreateApplication_WithNationalId_Success() {
    // Given
    String nationalId = "12345678";
    String mockTraceId = "00-1234567890abcdef1234567890abcd-1234567890abcdef-01";

    ApplicationDto dto = new ApplicationDto();
    User user = new User();
    Application application = new Application(dto, user);
    ApplicationDto applicationDto = new ApplicationDto(application);

    try (MockedStatic<Span> mockedSpan = mockStatic(Span.class)) {
      Span span = mock(Span.class);
      SpanContext spanContext = mock(SpanContext.class);
      when(Span.current()).thenReturn(span);
      when(span.getSpanContext()).thenReturn(spanContext);
      when(spanContext.getTraceId()).thenReturn(mockTraceId);

      when(userRepository.findByNationalId(nationalId)).thenReturn(Optional.of(user));
      when(applicationRepository.save(any(Application.class))).thenReturn(application);

      // When
      ApplicationResponse response = loansService.createApplication(nationalId, dto);

      // Then
      assertNotNull(response);
      verify(applicationRepository).save(any(Application.class));

      // Verify the correct event was published
      ArgumentCaptor<ApplicationEvent.Created> eventCaptor =
          ArgumentCaptor.forClass(ApplicationEvent.Created.class);
      verify(queueProducer).publishApplicationEvent(eventCaptor.capture());

      ApplicationEvent.Created capturedEvent = eventCaptor.getValue();
      assertEquals(EventType.CREATE, capturedEvent.getEventType());
      assertEquals(applicationDto, capturedEvent.getApplication());
      assertEquals(mockTraceId, capturedEvent.getTraceId());
      assertEquals("1.0", capturedEvent.getVersion());
    }
  }

  @Test
  void testCreateApplication_WithNationalId_UserNotFound() {
    when(userRepository.findByNationalId("invalid")).thenReturn(Optional.empty());
    assertThrows(
        ResourceNotFoundException.class,
        () -> loansService.createApplication("invalid", new ApplicationDto()));
  }

  @Test
  void testGetStatus_CustomerNotFound() {
    when(userRepository.findByNationalId("nonexistent")).thenReturn(Optional.empty());
    assertThrows(LoanException.class, () -> loansService.getStatus("nonexistent"));
  }

  @Test
  void testGetStatus_NoActiveLoan() {
    User user = new User();
    when(userRepository.findByNationalId("123")).thenReturn(Optional.of(user));
    when(applicationRepository.findActiveByCustomerNumber(user)).thenReturn(Optional.empty());
    assertThrows(LoanException.class, () -> loansService.getStatus("123"));
  }

  @Test
  void testGetStatus_Success() {
    User user = new User();
    Application application = new Application();
    when(userRepository.findByNationalId("123")).thenReturn(Optional.of(user));
    when(applicationRepository.findActiveByCustomerNumber(user))
        .thenReturn(Optional.of(application));
    Application result = loansService.getStatus("123");
    assertEquals(application, result);
  }

  @Test
  void testMakePaymentNotFound() {
    UUID loanId = UUID.randomUUID();
    when(applicationRepository.findById(loanId)).thenReturn(Optional.empty());
    assertThrows(LoanException.class, () -> loansService.makePayment(loanId, 1000.0));
  }

  @Test
  void testMakePayment_Success() {
    UUID loanId = UUID.randomUUID();
    Application application = new Application();
    when(applicationRepository.findById(loanId)).thenReturn(Optional.of(application));
    loansService.makePayment(loanId, 1500.0);
    verify(loanRepaymentRepository).save(any());
  }
}
