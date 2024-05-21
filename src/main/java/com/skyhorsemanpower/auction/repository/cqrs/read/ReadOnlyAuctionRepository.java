package com.skyhorsemanpower.auction.repository.cqrs.read;


import com.skyhorsemanpower.auction.data.vo.InquiryAuctionHistoryResponseVo;
import com.skyhorsemanpower.auction.domain.cqrs.read.ReadOnlyAuction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReadOnlyAuctionRepository extends MongoRepository<ReadOnlyAuction, String> {

    List<ReadOnlyAuction> findAllByTitleLike(String keyword);

    Optional<ReadOnlyAuction> findByAuctionUuid(String auctionUuid);

    List<ReadOnlyAuction> findAllByCategory(String category);

    List<ReadOnlyAuction> findAllByTitleLikeAndCategory(String keyword, String category);

    List<ReadOnlyAuction> findAllBySellerUuidOrderByCreatedAtDesc(String sellerUuid);

}
