package com.skyhorsemanpower.auction.repository;

import com.skyhorsemanpower.auction.domain.RoundInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoundInfoRepository extends MongoRepository<RoundInfo, String> {
}
