package com.hft.orderMatchingEngine.common.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class Trade {
    private UUID id;
    private UUID buyOrderId;
    private UUID sellOrderId;
    private String symbol;
    private BigDecimal price;
    private Long quantity;
    private Instant timestamp;
} 