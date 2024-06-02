package com.skyhorsemanpower.auction.repository.cqrs.read;


import com.skyhorsemanpower.auction.domain.cqrs.read.ReadOnlyAuction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReadOnlyAuctionRepository extends MongoRepository<ReadOnlyAuction, String> {
    
    Optional<ReadOnlyAuction> findByAuctionUuid(String auctionUuid);

    List<ReadOnlyAuction> findAllBySellerUuidOrderByCreatedAtDesc(String sellerUuid);

    Page<ReadOnlyAuction> findAllByTitleLikeOrderByCreatedAtDesc(String keyword, PageRequest of);

    Page<ReadOnlyAuction> findAllByCategoryOrderByCreatedAtDesc(String category, PageRequest of);

    Page<ReadOnlyAuction> findAllByTitleLikeAndCategoryOrderByCreatedAtDesc(String keyword, String category, PageRequest of);
}
