package com.skyhorsemanpower.auction.repository.querydsl.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.skyhorsemanpower.auction.repository.querydsl.AuctionImagesRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.skyhorsemanpower.auction.domain.QAuctionImages.auctionImages;

@Repository
@RequiredArgsConstructor
public class AuctionImagesRepositoryImpl implements AuctionImagesRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public String getThumbnailUrl(String auctionUuid) {
        return queryFactory
                .select(auctionImages.imageUrl)
                .from(auctionImages)
                .where(
                        auctionImages.auctionUuid.eq(auctionUuid)
                                .and(auctionImages.isThumbnail.eq(true))
                )
                .fetchFirst();
    }

    @Override
    public List<String> getImagesUrl(String auctionUuid) {
        return queryFactory
                .select(auctionImages.imageUrl)
                .from(auctionImages)
                .where(
                        auctionImages.auctionUuid.eq(auctionUuid)
                                .and(auctionImages.isThumbnail.eq(false))
                )
                .fetch();
    }
}
