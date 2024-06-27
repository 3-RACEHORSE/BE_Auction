package com.skyhorsemanpower.auction.repository;

import com.skyhorsemanpower.auction.domain.AuctionCloseState;
import com.skyhorsemanpower.auction.repository.mongotemplate.CustomAuctionCloseStateRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuctionCloseStateRepository extends MongoRepository<AuctionCloseState, String>, CustomAuctionCloseStateRepository {
    Optional<AuctionCloseState> findByAuctionUuid(String auctionUuid);
}
