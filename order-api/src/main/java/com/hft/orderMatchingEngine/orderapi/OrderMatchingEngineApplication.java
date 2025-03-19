package com.hft.orderMatchingEngine.orderapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
    "com.hft.orderMatchingEngine.orderapi",
    "com.hft.orderMatchingEngine.common",
    "com.hft.orderMatchingEngine.matching.engine",
    "com.hft.orderMatchingEngine.storage"
})
public class OrderMatchingEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderMatchingEngineApplication.class, args);
    }
} 