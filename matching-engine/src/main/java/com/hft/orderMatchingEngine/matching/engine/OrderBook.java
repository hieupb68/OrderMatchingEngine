package com.hft.orderMatchingEngine.matching.engine;

import com.hft.orderMatchingEngine.common.model.Order;
import com.hft.orderMatchingEngine.common.model.Trade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

@Slf4j
@Component
public class OrderBook {
    private final Map<String, ConcurrentSkipListMap<BigDecimal, Set<Order>>> buyOrders = new ConcurrentHashMap<>();
    private final Map<String, ConcurrentSkipListMap<BigDecimal, Set<Order>>> sellOrders = new ConcurrentHashMap<>();
    private final Map<UUID, Order> ordersById = new ConcurrentHashMap<>();
    private final Map<String, BigDecimal> lastPrices = new ConcurrentHashMap<>();

    public void addOrder(Order order) {
        ordersById.put(order.getId(), order);
        Map<String, ConcurrentSkipListMap<BigDecimal, Set<Order>>> orderSide = 
            order.getSide() == Order.OrderSide.BUY ? buyOrders : sellOrders;
        
        orderSide.computeIfAbsent(order.getSymbol(), k -> new ConcurrentSkipListMap<>(Collections.reverseOrder()))
                .computeIfAbsent(order.getPrice(), k -> new HashSet<>())
                .add(order);
    }

    public void removeOrder(Order order) {
        ordersById.remove(order.getId());
        Map<String, ConcurrentSkipListMap<BigDecimal, Set<Order>>> orderSide = 
            order.getSide() == Order.OrderSide.BUY ? buyOrders : sellOrders;
        
        ConcurrentSkipListMap<BigDecimal, Set<Order>> priceLevels = orderSide.get(order.getSymbol());
        if (priceLevels != null) {
            Set<Order> orders = priceLevels.get(order.getPrice());
            if (orders != null) {
                orders.remove(order);
                if (orders.isEmpty()) {
                    priceLevels.remove(order.getPrice());
                }
            }
            if (priceLevels.isEmpty()) {
                orderSide.remove(order.getSymbol());
            }
        }
    }

    public List<Trade> matchOrder(Order order) {
        List<Trade> trades = new ArrayList<>();
        Map<String, ConcurrentSkipListMap<BigDecimal, Set<Order>>> oppositeSide = 
            order.getSide() == Order.OrderSide.BUY ? sellOrders : buyOrders;
        
        ConcurrentSkipListMap<BigDecimal, Set<Order>> priceLevels = oppositeSide.get(order.getSymbol());
        if (priceLevels == null) {
            return trades;
        }

        long remainingQuantity = order.getQuantity();
        for (Map.Entry<BigDecimal, Set<Order>> entry : priceLevels.entrySet()) {
            if (remainingQuantity <= 0) break;
            
            BigDecimal price = entry.getKey();
            if (order.getSide() == Order.OrderSide.BUY && price.compareTo(order.getPrice()) > 0) break;
            if (order.getSide() == Order.OrderSide.SELL && price.compareTo(order.getPrice()) < 0) break;

            Set<Order> orders = entry.getValue();
            for (Order matchingOrder : orders) {
                if (remainingQuantity <= 0) break;
                
                long matchQuantity = Math.min(remainingQuantity, matchingOrder.getQuantity());
                Trade trade = Trade.builder()
                    .id(UUID.randomUUID())
                    .buyOrderId(order.getSide() == Order.OrderSide.BUY ? order.getId() : matchingOrder.getId())
                    .sellOrderId(order.getSide() == Order.OrderSide.SELL ? order.getId() : matchingOrder.getId())
                    .symbol(order.getSymbol())
                    .price(price)
                    .quantity(matchQuantity)
                    .timestamp(Instant.now())
                    .build();
                
                trades.add(trade);
                remainingQuantity -= matchQuantity;
                matchingOrder.setQuantity(matchingOrder.getQuantity() - matchQuantity);
                
                if (matchingOrder.getQuantity() <= 0) {
                    removeOrder(matchingOrder);
                }
            }
        }

        if (remainingQuantity > 0) {
            order.setQuantity(remainingQuantity);
            addOrder(order);
        }

        // Update last price
        if (!trades.isEmpty()) {
            lastPrices.put(order.getSymbol(), trades.get(trades.size() - 1).getPrice());
        }

        return trades;
    }

    public Order getOrder(UUID orderId) {
        return ordersById.get(orderId);
    }

    public BigDecimal getLastPrice(String symbol) {
        return lastPrices.get(symbol);
    }

    public Map<BigDecimal, Long> getOrderBookDepth(String symbol, int depth, Order.OrderSide side) {
        Map<String, ConcurrentSkipListMap<BigDecimal, Set<Order>>> orderSide = 
            side == Order.OrderSide.BUY ? buyOrders : sellOrders;
        
        ConcurrentSkipListMap<BigDecimal, Set<Order>> priceLevels = orderSide.get(symbol);
        if (priceLevels == null) {
            return Collections.emptyMap();
        }

        return priceLevels.entrySet().stream()
            .limit(depth)
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().stream()
                    .mapToLong(Order::getQuantity)
                    .sum(),
                (v1, v2) -> v1,
                () -> new TreeMap<>(side == Order.OrderSide.BUY ? Collections.reverseOrder() : null)
            ));
    }

    public Map<BigDecimal, Long> getBuyOrderBookDepth(String symbol, int depth) {
        return getOrderBookDepth(symbol, depth, Order.OrderSide.BUY);
    }

    public Map<BigDecimal, Long> getSellOrderBookDepth(String symbol, int depth) {
        return getOrderBookDepth(symbol, depth, Order.OrderSide.SELL);
    }
} 