package com.hft.orderMatchingEngine.matching.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hft.orderMatchingEngine.common.model.Order;
import com.hft.orderMatchingEngine.matching.engine.MatchingEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderConsumer {
    private final ObjectMapper objectMapper;
    private final MatchingEngine matchingEngine;

    @KafkaListener(topics = "orders", groupId = "matching-engine")
    public void consumeOrder(String orderJson) {
        try {
            Order order = objectMapper.readValue(orderJson, Order.class);
            matchingEngine.submitOrder(order);
        } catch (Exception e) {
            log.error("Error consuming order: {}", orderJson, e);
        }
    }
} 