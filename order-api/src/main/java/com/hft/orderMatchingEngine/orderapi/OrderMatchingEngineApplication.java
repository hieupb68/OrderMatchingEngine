package com.hft.orderMatchingEngine.orderapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = "com.hft.orderMatchingEngine")
public class OrderMatchingEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderMatchingEngineApplication.class, args);
    }
} 