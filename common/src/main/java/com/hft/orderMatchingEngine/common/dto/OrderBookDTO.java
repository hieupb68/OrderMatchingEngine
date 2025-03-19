package com.hft.orderMatchingEngine.common.dto;

import com.hft.orderMatchingEngine.common.model.Order;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Data
public class OrderBookDTO {
    private String symbol;
    private Map<BigDecimal, List<OrderDTO>> buyOrders;
    private Map<BigDecimal, List<OrderDTO>> sellOrders;
    private BigDecimal bestBid;
    private BigDecimal bestAsk;
    private BigDecimal spread;

    public OrderBookDTO(String symbol) {
        this.symbol = symbol;
        this.buyOrders = new TreeMap<>(java.util.Collections.reverseOrder());
        this.sellOrders = new TreeMap<>();
    }

    public void updateBestPrices() {
        this.bestBid = buyOrders.isEmpty() ? null : buyOrders.keySet().iterator().next();
        this.bestAsk = sellOrders.isEmpty() ? null : sellOrders.keySet().iterator().next();
        if (bestBid != null && bestAsk != null) {
            this.spread = bestAsk.subtract(bestBid);
        }
    }
} 