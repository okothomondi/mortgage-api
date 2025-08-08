package com.okoth.mortgage.spring.quarts;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class QrtzBeans {
  @Bean
  public JobDetail overdueLoansJd() {
    return JobBuilder.newJob(OverdueLoansJob.class)
        .storeDurably()
        .withIdentity("OverdueLoansJob", "OverdueLoansGroup")
        .build();
  }

  @Bean
  public Trigger overdueLoansTrigger() {
    return TriggerBuilder.newTrigger()
        .startNow()
        .forJob("OverdueLoansJob", "OverdueLoansGroup")
        .withPriority(1)
        .withIdentity("OverdueLoansTrigger", "OverdueLoansGroup")
        .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(0, 0))
        .build();
  }
}
