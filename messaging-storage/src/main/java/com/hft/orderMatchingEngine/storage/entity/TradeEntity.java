package com.hft.orderMatchingEngine.storage.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Table(name = "trades")
public class TradeEntity {
    @Id
    @Column(name = "id")
    private UUID id;
    
    @Column(name = "buy_order_id")
    private UUID buyOrderId;
    
    @Column(name = "sell_order_id")
    private UUID sellOrderId;
    
    @Column(name = "symbol")
    private String symbol;
    
    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(name = "quantity")
    private Long quantity;
    
    @Column(name = "timestamp")
    private Instant timestamp;
} 