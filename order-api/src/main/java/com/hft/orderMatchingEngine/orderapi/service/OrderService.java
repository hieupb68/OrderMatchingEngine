package com.hft.orderMatchingEngine.orderapi.service;

import com.hft.orderMatchingEngine.common.model.Order;
import com.hft.orderMatchingEngine.orderapi.kafka.OrderProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderProducer orderProducer;
    private final Map<UUID, Order> orders = new ConcurrentHashMap<>();

    public Order createOrder(Order order) {
        order.setId(UUID.randomUUID());
        order.setTimestamp(Instant.now());
        order.setStatus(Order.OrderStatus.NEW);
        
        orders.put(order.getId(), order);
        orderProducer.sendOrder(order);
        
        return order;
    }

    public Order getOrder(UUID orderId) {
        return orders.get(orderId);
    }

    public List<Order> getOrders() {
        return List.copyOf(orders.values());
    }

    public List<Order> getOrdersBySymbol(String symbol) {
        return orders.values().stream()
            .filter(order -> order.getSymbol().equals(symbol))
            .collect(Collectors.toList());
    }

    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        return orders.values().stream()
            .filter(order -> order.getStatus() == status)
            .collect(Collectors.toList());
    }

    public void cancelOrder(UUID orderId) {
        Order order = orders.get(orderId);
        if (order != null) {
            order.setStatus(Order.OrderStatus.CANCELLED);
            orderProducer.sendOrder(order);
        }
    }

    public void updateOrderStatus(UUID orderId, Order.OrderStatus status) {
        Order order = orders.get(orderId);
        if (order != null) {
            order.setStatus(status);
            orderProducer.sendOrder(order);
        }
    }
} 