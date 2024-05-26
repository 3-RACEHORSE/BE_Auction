package com.skyhorsemanpower.auction.repository.cqrs.read;


import com.skyhorsemanpower.auction.domain.cqrs.read.ReadOnlyAuction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReadOnlyAuctionRepository extends MongoRepository<ReadOnlyAuction, String> {

    Page<ReadOnlyAuction> findAllByTitleLike(String keyword, Pageable pageable);

    Optional<ReadOnlyAuction> findByAuctionUuid(String auctionUuid);

    Page<ReadOnlyAuction> findAllByCategory(String category, Pageable pageable);

    Page<ReadOnlyAuction> findAllByTitleLikeAndCategory(String keyword, String category, Pageable pageable);

    List<ReadOnlyAuction> findAllBySellerUuidOrderByCreatedAtDesc(String sellerUuid);

}
