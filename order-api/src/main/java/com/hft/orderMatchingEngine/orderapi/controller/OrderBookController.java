package com.hft.orderMatchingEngine.orderapi.controller;

import com.hft.orderMatchingEngine.common.dto.OrderBookDTO;
import com.hft.orderMatchingEngine.orderapi.service.OrderBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orderbook")
@RequiredArgsConstructor
public class OrderBookController {
    private final OrderBookService orderBookService;

    @GetMapping("/{symbol}")
    public ResponseEntity<OrderBookDTO> getOrderBook(@PathVariable String symbol) {
        return ResponseEntity.ok(orderBookService.getOrderBook(symbol));
    }

    @GetMapping("/{symbol}/depth")
    public ResponseEntity<OrderBookDTO> getOrderBookDepth(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "10") int depth) {
        return ResponseEntity.ok(orderBookService.getOrderBookDepth(symbol, depth));
    }
} 