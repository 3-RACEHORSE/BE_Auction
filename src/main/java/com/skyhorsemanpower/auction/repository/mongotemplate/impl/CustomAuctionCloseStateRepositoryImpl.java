package com.skyhorsemanpower.auction.repository.mongotemplate.impl;

import com.skyhorsemanpower.auction.domain.AuctionCloseState;
import com.skyhorsemanpower.auction.repository.mongotemplate.CustomAuctionCloseStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomAuctionCloseStateRepositoryImpl implements CustomAuctionCloseStateRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public boolean setAuctionClosedInNotExists(String auctionUuid) {
        Query query = new Query(Criteria.where("auctionUuid").is(auctionUuid)
                .and("auctionCloseState").is(false));
        Update update = new Update().set("auctionCloseState", true);
        AuctionCloseState updatedDocument = mongoTemplate.findAndModify(query, update, AuctionCloseState.class);
        return updatedDocument != null;
    }
}
