package com.poc.carddelivery.application;

import com.poc.carddelivery.domain.delivery.JobHistory;
import com.poc.carddelivery.domain.delivery.JobHistoryRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 배치 이력 기록.
 * REQUIRES_NEW: 배치 트랜잭션이 롤백돼도 "실패했다"는 이력은 반드시 남아야 한다.
 */
@Component
@RequiredArgsConstructor
public class JobHistoryRecorder {

    private final JobHistoryRepository jobHistoryRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(String batchId, String courierCode, int targetCount, int sentCount,
                       JobHistory.Result result, String errorMessage, LocalDateTime startedAt) {
        jobHistoryRepository.save(JobHistory.builder()
                .batchId(batchId)
                .courierCode(courierCode)
                .targetCount(targetCount)
                .sentCount(sentCount)
                .result(result)
                .errorMessage(errorMessage)
                .startedAt(startedAt)
                .finishedAt(LocalDateTime.now())
                .build());
    }
}
