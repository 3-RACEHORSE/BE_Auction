package com.skyhorsemanpower.auction.repository;

import com.skyhorsemanpower.auction.data.vo.RoundInfoResponseVo;
import com.skyhorsemanpower.auction.data.vo.domain.RoundInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoundInfoRepository extends MongoRepository<RoundInfo, String> {
    Optional<RoundInfoResponseVo> findFirstByAuctionUuidOrderByCreatedAtDesc(String auctionUuid);
}
