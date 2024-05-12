package com.skyhorsemanpower.auction.repository.read;


import com.skyhorsemanpower.auction.domain.read.ReadOnlyAuction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReadOnlyAuctionRepository extends MongoRepository<ReadOnlyAuction, String> {

    List<ReadOnlyAuction> findAllByTitle(String keyword);
}
