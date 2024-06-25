package com.skyhorsemanpower.auction.repository;

import com.skyhorsemanpower.auction.domain.AuctionResult;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuctionResultRepository extends MongoRepository<AuctionResult, String> {
}
