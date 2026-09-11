package com.poc.carddelivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CardDeliveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(CardDeliveryApplication.class, args);
    }
}
