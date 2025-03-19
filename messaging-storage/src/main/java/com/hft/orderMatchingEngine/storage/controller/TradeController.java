package com.hft.orderMatchingEngine.storage.controller;

import com.hft.orderMatchingEngine.common.dto.TradeDTO;
import com.hft.orderMatchingEngine.storage.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/trades")
@RequiredArgsConstructor
public class TradeController {
    private final TradeService tradeService;

    @GetMapping
    public ResponseEntity<List<TradeDTO>> getTrades(
            @RequestParam(required = false) String symbol,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endTime) {
        return ResponseEntity.ok(tradeService.getTrades(symbol, startTime, endTime));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TradeDTO> getTrade(@PathVariable UUID id) {
        return ResponseEntity.ok(tradeService.getTrade(id));
    }

    @GetMapping("/symbol/{symbol}")
    public ResponseEntity<List<TradeDTO>> getTradesBySymbol(@PathVariable String symbol) {
        return ResponseEntity.ok(tradeService.getTradesBySymbol(symbol));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<TradeDTO>> getTradesByOrder(@PathVariable UUID orderId) {
        return ResponseEntity.ok(tradeService.getTradesByOrder(orderId));
    }
} 