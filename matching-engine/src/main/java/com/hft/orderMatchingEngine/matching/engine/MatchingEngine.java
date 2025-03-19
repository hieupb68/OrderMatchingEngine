package com.hft.orderMatchingEngine.matching.engine;

import com.hft.orderMatchingEngine.common.model.Order;
import com.hft.orderMatchingEngine.common.model.Trade;
import com.hft.orderMatchingEngine.matching.kafka.TradeProducer;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class MatchingEngine {
    private final OrderBook orderBook;
    private final TradeProducer tradeProducer;
    private Disruptor<OrderEvent> disruptor;
    private RingBuffer<OrderEvent> ringBuffer;
    private static final int BUFFER_SIZE = 1024;
    private static final long SHUTDOWN_TIMEOUT = 5;

    public MatchingEngine(OrderBook orderBook, TradeProducer tradeProducer) {
        this.orderBook = orderBook;
        this.tradeProducer = tradeProducer;
    }

    @PostConstruct
    public void init() {
        disruptor = new Disruptor<>(
            OrderEvent::new,
            BUFFER_SIZE,
            Executors.newSingleThreadExecutor()
        );

        disruptor.handleEventsWith(this::processOrder);
        ringBuffer = disruptor.start();
    }

    @PreDestroy
    public void shutdown() {
        if (disruptor != null) {
            disruptor.shutdown(SHUTDOWN_TIMEOUT, TimeUnit.SECONDS);
        }
    }

    public void submitOrder(Order order) {
        long sequence = ringBuffer.next();
        try {
            OrderEvent event = ringBuffer.get(sequence);
            event.setOrder(order);
        } finally {
            ringBuffer.publish(sequence);
        }
    }

    private void processOrder(OrderEvent event, long sequence, boolean endOfBatch) {
        Order order = event.getOrder();
        try {
            if (order.getStatus() == Order.OrderStatus.CANCELLED) {
                orderBook.removeOrder(order);
                return;
            }

            List<Trade> trades = orderBook.matchOrder(order);
            for (Trade trade : trades) {
                tradeProducer.sendTrade(trade);
            }
        } catch (Exception e) {
            log.error("Error processing order: {}", order.getId(), e);
        }
    }

    private static class OrderEvent {
        private Order order;

        public Order getOrder() {
            return order;
        }

        public void setOrder(Order order) {
            this.order = order;
        }
    }
} 