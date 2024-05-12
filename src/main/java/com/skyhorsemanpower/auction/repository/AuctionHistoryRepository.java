package com.skyhorsemanpower.auction.repository;

import com.skyhorsemanpower.auction.domain.AuctionHistory;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface AuctionHistoryRepository extends ReactiveMongoRepository<AuctionHistory, String> {
}
