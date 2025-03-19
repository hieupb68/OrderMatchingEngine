package com.hft.orderMatchingEngine.storage.repository;

import com.hft.orderMatchingEngine.storage.entity.TradeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TradeRepository extends JpaRepository<TradeEntity, UUID>, JpaSpecificationExecutor<TradeEntity> {
} 