package com.skyhorsemanpower.auction.repository.read;


import com.skyhorsemanpower.auction.domain.read.ReadOnlyAuction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReadOnlyAuctionRepository extends MongoRepository<ReadOnlyAuction, String> {

    List<ReadOnlyAuction> findAllByTitleLike(String keyword);

    Optional<ReadOnlyAuction> findByAuctionUuid(String auctionUuid);
}
