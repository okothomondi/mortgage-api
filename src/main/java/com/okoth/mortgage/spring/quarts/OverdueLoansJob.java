package com.okoth.mortgage.spring.quarts;

import static com.okoth.mortgage.models.enums.ApplicationStatus.OVERDUE;

import com.okoth.mortgage.repositories.ApplicationRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@Slf4j
@RequiredArgsConstructor
public class OverdueLoansJob implements Job {
  private final ApplicationRepository ApplicationRepository;

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    try {
      ApplicationRepository.findLoansOlderThan(LocalDateTime.now().minusDays(30)).parallelStream()
          .forEach(
              loan -> {
                loan.setStatus(OVERDUE);
                ApplicationRepository.save(loan);
              });
    } catch (Exception e) {
      log.error("Failed to execute OverdueLoansJob {}", e.getMessage());
      throw new JobExecutionException(e);
    }
  }
}
