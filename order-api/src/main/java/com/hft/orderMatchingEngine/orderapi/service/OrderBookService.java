package com.hft.orderMatchingEngine.orderapi.service;

import com.hft.orderMatchingEngine.common.dto.OrderBookDTO;
import com.hft.orderMatchingEngine.common.dto.OrderDTO;
import com.hft.orderMatchingEngine.common.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderBookService {
    private final OrderService orderService;

    public OrderBookDTO getOrderBook(String symbol) {
        OrderBookDTO orderBook = new OrderBookDTO(symbol);
        List<Order> orders = orderService.getOrdersBySymbol(symbol);
        
        Map<Order.OrderSide, Map<Double, List<Order>>> ordersBySide = orders.stream()
            .collect(Collectors.groupingBy(
                Order::getSide,
                Collectors.groupingBy(
                    order -> order.getPrice().doubleValue(),
                    Collectors.toList()
                )
            ));

        // Convert orders to DTOs and group by price
        ordersBySide.getOrDefault(Order.OrderSide.BUY, Map.of()).forEach((price, ordersList) -> 
            orderBook.getBuyOrders().put(
                BigDecimal.valueOf(price),
                ordersList.stream().map(this::convertToDTO).collect(Collectors.toList())
            )
        );

        ordersBySide.getOrDefault(Order.OrderSide.SELL, Map.of()).forEach((price, ordersList) -> 
            orderBook.getSellOrders().put(
                BigDecimal.valueOf(price),
                ordersList.stream().map(this::convertToDTO).collect(Collectors.toList())
            )
        );

        orderBook.updateBestPrices();
        return orderBook;
    }

    public OrderBookDTO getOrderBookDepth(String symbol, int depth) {
        OrderBookDTO orderBook = getOrderBook(symbol);
        
        // Limit the depth of orders
        orderBook.setBuyOrders(
            orderBook.getBuyOrders().entrySet().stream()
                .limit(depth)
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (v1, v2) -> v1,
                    () -> new TreeMap<>(java.util.Collections.reverseOrder())
                ))
        );

        orderBook.setSellOrders(
            orderBook.getSellOrders().entrySet().stream()
                .limit(depth)
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (v1, v2) -> v1,
                    TreeMap::new
                ))
        );

        return orderBook;
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setSymbol(order.getSymbol());
        dto.setOrderType(order.getOrderType());
        dto.setSide(order.getSide());
        dto.setPrice(order.getPrice());
        dto.setQuantity(order.getQuantity());
        dto.setTimestamp(order.getTimestamp());
        dto.setStatus(order.getStatus());
        return dto;
    }
} 