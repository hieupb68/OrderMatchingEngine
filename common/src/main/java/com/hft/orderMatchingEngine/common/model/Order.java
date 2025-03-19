package com.hft.orderMatchingEngine.common.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class Order {
    private UUID id;
    
    @NotBlank(message = "Symbol is required")
    private String symbol;
    
    @NotNull(message = "Order type is required")
    private OrderType orderType;
    
    @NotNull(message = "Side is required")
    private OrderSide side;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Long quantity;
    
    private Instant timestamp;
    private OrderStatus status;
    
    public enum OrderType {
        LIMIT, MARKET
    }
    
    public enum OrderSide {
        BUY, SELL
    }
    
    public enum OrderStatus {
        NEW, FILLED, PARTIALLY_FILLED, CANCELLED, REJECTED
    }
} 