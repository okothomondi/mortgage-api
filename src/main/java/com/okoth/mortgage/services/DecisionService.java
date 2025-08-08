package com.okoth.mortgage.services;

import com.okoth.mortgage.models.db.Application;
import com.okoth.mortgage.models.db.Decision;
import com.okoth.mortgage.models.dto.DecisionDto;
import com.okoth.mortgage.repositories.DecisionRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DecisionService {

  private final DecisionRepository decisionRepository;

  @Transactional
  public Decision createDecision(
      DecisionDto decisionDto, Application application, String approverId) {
    Decision decision = new Decision(decisionDto);
    decision.setApproverId(approverId);
    decision.setApplication(application);
    return decisionRepository.save(decision);
  }

  public DecisionDto getDecisionByApplicationId(String applicationId) {
    Decision decision =
        decisionRepository
            .findByApplicationId(UUID.fromString(applicationId))
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Decision not found for application: " + applicationId));

    return DecisionDto.builder()
        .id(decision.getId().toString())
        .status(decision.getStatus())
        .comments(decision.getComments())
        .approverId(decision.getApproverId())
        .decisionDate(decision.getDecisionDate().toString())
        .build();
  }
}
