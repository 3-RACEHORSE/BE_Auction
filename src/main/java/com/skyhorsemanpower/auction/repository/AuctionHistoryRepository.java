package com.skyhorsemanpower.auction.repository;

import com.skyhorsemanpower.auction.domain.AuctionHistory;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface AuctionHistoryRepository extends ReactiveMongoRepository<AuctionHistory, String> {

    @Tailable
    @Query("{'auctionUuid' : ?0}")
    Flux<AuctionHistory> searchBiddingPrice(String auctionUuid);
}
