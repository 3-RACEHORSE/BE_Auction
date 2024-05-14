package com.skyhorsemanpower.auction.repository;

import com.skyhorsemanpower.auction.domain.AuctionCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionCategoryRepository extends JpaRepository<AuctionCategory, Long> {
}
