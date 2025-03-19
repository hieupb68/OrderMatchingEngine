package com.hft.orderMatchingEngine.common.dto;

import com.hft.orderMatchingEngine.common.model.Order;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
public class OrderDTO {
    private UUID id;
    
    @NotBlank(message = "Symbol is required")
    private String symbol;
    
    @NotNull(message = "Order type is required")
    private Order.OrderType orderType;
    
    @NotNull(message = "Side is required")
    private Order.OrderSide side;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Long quantity;
    
    private Instant timestamp;
    private Order.OrderStatus status;
} 