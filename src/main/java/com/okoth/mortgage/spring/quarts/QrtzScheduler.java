package com.okoth.mortgage.spring.quarts;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class QrtzScheduler {
  private final Scheduler scheduler;
  private final JobDetail overdueLoansJd;
  private final Trigger overdueLoansTrigger;

  @PostConstruct
  public void scheduleOverdueLoansJob() {
    try {
      scheduler.scheduleJob(overdueLoansJd, overdueLoansTrigger);
    } catch (Exception e) {
      log.error("Failed to schedule jobs: {}", e.getMessage());
    }
  }
}
