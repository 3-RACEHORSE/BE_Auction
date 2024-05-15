package com.skyhorsemanpower.auction.repository;

import com.skyhorsemanpower.auction.domain.AuctionImages;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuctionImagesRepository extends JpaRepository<AuctionImages, Long>, AuctionImagesRepositoryCustom {
    Optional<AuctionImages> findByAuctionUuidAndIsThumbnail(String auctionUuid, boolean isThumbnail);
}
