package com.okoth.mortgage.models.custom;

import static com.okoth.mortgage.models.enums.EventType.CREATE;
import static com.okoth.mortgage.models.enums.EventType.DELETE;
import static com.okoth.mortgage.models.enums.EventType.UPDATE;

import com.okoth.mortgage.models.dto.ApplicationDto;
import com.okoth.mortgage.models.enums.EventType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationEvent {
  private EventType eventType;
  private ApplicationDto application;
  private String timestamp;
  private String traceId;
  @Builder.Default private String version = "1.0";

  protected ApplicationEvent(
      EventType eventType, ApplicationDto application, String timestamp, String traceId) {
    this.eventType = eventType;
    this.application = application;
    this.timestamp = timestamp;
    this.traceId = traceId;
    this.version = "1.0";
  }

  public static class Created extends ApplicationEvent {
    public Created(ApplicationDto application, String traceId) {
      super(CREATE, application, LocalDateTime.now().toString(), traceId);
    }
  }

  public static class Updated extends ApplicationEvent {
    public Updated(ApplicationDto application, String traceId) {
      super(UPDATE, application, LocalDateTime.now().toString(), traceId);
    }
  }

  public static class Deleted extends ApplicationEvent {
    public Deleted(ApplicationDto application, String traceId) {
      super(DELETE, application, LocalDateTime.now().toString(), traceId);
    }
  }
}
