package com.okoth.mortgage.models.custom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryResponse {

  /**
   * SUCCESSFUL: message delivered successfully THROTTLE_RETRY: The endpoint returned a throttle
   * error; retry again later ERROR_RETRY: An internal error of some kind we should be able to
   * recover and resend the message FAILED: Message could be submitted due to some unrecoverable
   * error TEMP_ERROR_RETRY: A minor error in Toshi eg Sender obj not ready to send message. quickly
   * resend a gain a fresh
   */
  public enum DeliveryStates {
    SYNC_SENT,
    ASYNC_SENT,
    SUCCESSFULL,
    SENDER_NOT_READY,
    THROTTLE_RETRY,
    ERROR_RETRY,
    SYNC_SENT_FAILED,
    FAILED,
    UNSUPPORTED,
    RETRY,
    MISSING_FROM,
    ERROR;

    public boolean isSuccessful() {
      return this.equals(ASYNC_SENT) || this.equals(SYNC_SENT) || this.equals(SUCCESSFULL);
    }

    public boolean isRetry() {
      return this.equals(RETRY);
    }
  }

  private String deliverResult;
  private DeliveryStates deliveryState;
}
