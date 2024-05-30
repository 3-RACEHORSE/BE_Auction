package com.skyhorsemanpower.auction.data.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class AuctionAndIsSubscribedDto {
    private String auctionUuid;
    private String handle;
    private String sellerUuid;
    private String title;
    private String content;
    private String category;
    private int minimumBiddingPrice;
    private String thumbnail;
    private LocalDateTime createdAt;
    private LocalDateTime endedAt;

    private boolean isSubscribed;

    @Builder
    public AuctionAndIsSubscribedDto(String auctionUuid, String handle, String sellerUuid, String title, String content, String category, int minimumBiddingPrice, String thumbnail, LocalDateTime createdAt, LocalDateTime endedAt, boolean isSubscribed) {
        this.auctionUuid = auctionUuid;
        this.handle = handle;
        this.sellerUuid = sellerUuid;
        this.title = title;
        this.content = content;
        this.category = category;
        this.minimumBiddingPrice = minimumBiddingPrice;
        this.thumbnail = thumbnail;
        this.createdAt = createdAt;
        this.endedAt = endedAt;
        this.isSubscribed = isSubscribed;
    }
}
