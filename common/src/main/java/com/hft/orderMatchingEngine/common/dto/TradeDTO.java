package com.hft.orderMatchingEngine.common.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
public class TradeDTO {
    private UUID id;
    private UUID buyOrderId;
    private UUID sellOrderId;
    private String symbol;
    private BigDecimal price;
    private Long quantity;
    private Instant timestamp;
} 