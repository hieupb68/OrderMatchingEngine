package com.hft.orderMatchingEngine.matching.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hft.orderMatchingEngine.common.model.Trade;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TradeProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendTrade(Trade trade) {
        try {
            String tradeJson = objectMapper.writeValueAsString(trade);
            kafkaTemplate.send("trades", tradeJson);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send trade to Kafka", e);
        }
    }
} 