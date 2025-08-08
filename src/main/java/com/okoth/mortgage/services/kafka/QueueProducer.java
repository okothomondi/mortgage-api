package com.okoth.mortgage.services.kafka;

import com.okoth.mortgage.models.custom.ApplicationEvent;
import com.okoth.mortgage.models.db.Application;
import com.okoth.mortgage.models.dto.ApplicationDto;
import com.okoth.mortgage.models.enums.EventType;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class QueueProducer {
  private final KafkaTemplate<String, Object> kafkaTemplate;

  public void publishApplicationEvent(EventType eventType, Application application) {
    try {
      ApplicationEvent event =
          ApplicationEvent.builder()
              .eventType(eventType)
              .application(new ApplicationDto(application))
              .timestamp(LocalDateTime.now().toString())
              .traceId(UUID.randomUUID().toString())
              .build();
      kafkaTemplate.send("loan.applications", application.getId().toString(), event);
      log.info("Published {} event for application ID: {}", eventType, application.getId());
    } catch (Exception e) {
      log.error("Failed to publishApplicationEvent({}, {})", eventType, application, e);
    }
  }

  public void publishApplicationEvent(ApplicationEvent event) {
    kafkaTemplate.send("loan.applications", event.getApplication().getId().toString(), event);
  }
}
