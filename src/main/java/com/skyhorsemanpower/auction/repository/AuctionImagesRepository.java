package com.skyhorsemanpower.auction.repository;

import com.skyhorsemanpower.auction.domain.AuctionImages;
import com.skyhorsemanpower.auction.repository.querydsl.AuctionImagesRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionImagesRepository extends JpaRepository<AuctionImages, Long>, AuctionImagesRepositoryCustom {
}
