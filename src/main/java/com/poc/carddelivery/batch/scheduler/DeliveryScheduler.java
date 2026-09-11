package com.poc.carddelivery.batch.scheduler;

import com.poc.carddelivery.batch.job.DeliveryJobRunner;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeliveryScheduler {

    private final DeliveryJobRunner jobRunner;

    @Scheduled(cron = "${delivery.batch.cron}")
    public void runDeliveryBatch() {
        jobRunner.run();
    }
}
