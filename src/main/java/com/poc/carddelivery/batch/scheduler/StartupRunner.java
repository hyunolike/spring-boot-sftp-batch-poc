package com.poc.carddelivery.batch.scheduler;

import com.poc.carddelivery.batch.job.DeliveryJobRunner;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/** delivery.batch.run-on-startup=true 이면 기동 직후 1회 실행 (데모/검증용) */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "delivery.batch.run-on-startup", havingValue = "true")
public class StartupRunner implements CommandLineRunner {

    private final DeliveryJobRunner jobRunner;

    @Override
    public void run(String... args) {
        jobRunner.run();
    }
}
