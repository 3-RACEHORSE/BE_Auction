package com.skyhorsemanpower.auction.repository;

import com.skyhorsemanpower.auction.domain.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
}
