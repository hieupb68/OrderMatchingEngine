package com.hft.orderMatchingEngine.storage.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hft.orderMatchingEngine.common.dto.TradeDTO;
import com.hft.orderMatchingEngine.common.model.Trade;
import com.hft.orderMatchingEngine.storage.entity.TradeEntity;
import com.hft.orderMatchingEngine.storage.repository.TradeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.Predicate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradeService {
    private final ObjectMapper objectMapper;
    private final TradeRepository tradeRepository;

    @KafkaListener(topics = "trades", groupId = "storage-service")
    public void handleTrade(String tradeJson) {
        try {
            Trade trade = objectMapper.readValue(tradeJson, Trade.class);
            TradeEntity entity = new TradeEntity();
            entity.setId(trade.getId());
            entity.setBuyOrderId(trade.getBuyOrderId());
            entity.setSellOrderId(trade.getSellOrderId());
            entity.setSymbol(trade.getSymbol());
            entity.setPrice(trade.getPrice());
            entity.setQuantity(trade.getQuantity());
            entity.setTimestamp(trade.getTimestamp());
            
            tradeRepository.save(entity);
        } catch (Exception e) {
            log.error("Error handling trade: {}", tradeJson, e);
        }
    }

    public List<TradeDTO> getTrades(String symbol, Instant startTime, Instant endTime) {
        Specification<TradeEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (symbol != null) {
                predicates.add(cb.equal(root.get("symbol"), symbol));
            }
            if (startTime != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("timestamp"), startTime));
            }
            if (endTime != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("timestamp"), endTime));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return tradeRepository.findAll(spec).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public TradeDTO getTrade(UUID id) {
        return tradeRepository.findById(id)
            .map(this::convertToDTO)
            .orElse(null);
    }

    public List<TradeDTO> getTradesBySymbol(String symbol) {
        return tradeRepository.findAll((root, query, cb) -> 
            cb.equal(root.get("symbol"), symbol))
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<TradeDTO> getTradesByOrder(UUID orderId) {
        return tradeRepository.findAll((root, query, cb) -> 
            cb.or(
                cb.equal(root.get("buyOrderId"), orderId),
                cb.equal(root.get("sellOrderId"), orderId)
            ))
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    private TradeDTO convertToDTO(TradeEntity entity) {
        TradeDTO dto = new TradeDTO();
        dto.setId(entity.getId());
        dto.setBuyOrderId(entity.getBuyOrderId());
        dto.setSellOrderId(entity.getSellOrderId());
        dto.setSymbol(entity.getSymbol());
        dto.setPrice(entity.getPrice());
        dto.setQuantity(entity.getQuantity());
        dto.setTimestamp(entity.getTimestamp());
        return dto;
    }
} 