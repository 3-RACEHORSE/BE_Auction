package com.skyhorsemanpower.auction.data.vo;

import com.skyhorsemanpower.auction.domain.cqrs.read.ReadOnlyAuction;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
public class MainCategoryHotAuctionResponseVo {
    private String auctionUuid;
    private String title;
    private String category;
    private int minimumBiddingPrice;
    private LocalDateTime createdAt;
    private LocalDateTime endedAt;
    private String handle;
    private String thumbnail;
    private String content;

    @Builder
    public MainCategoryHotAuctionResponseVo(String auctionUuid, String title, String category, int minimumBiddingPrice, LocalDateTime createdAt, LocalDateTime endedAt, String handle, String thumbnail, String content) {
        this.auctionUuid = auctionUuid;
        this.title = title;
        this.category = category;
        this.minimumBiddingPrice = minimumBiddingPrice;
        this.createdAt = createdAt;
        this.endedAt = endedAt;
        this.handle = handle;
        this.thumbnail = thumbnail;
        this.content = content;
    }

    public MainCategoryHotAuctionResponseVo toVo(ReadOnlyAuction readOnlyAuction, String thumbnail, String handle) {
        return MainCategoryHotAuctionResponseVo.builder()
                .auctionUuid(readOnlyAuction.getAuctionUuid())
                .title(readOnlyAuction.getTitle())
                .category(readOnlyAuction.getCategory())
                .minimumBiddingPrice(readOnlyAuction.getMinimumBiddingPrice())
                .createdAt(readOnlyAuction.getCreatedAt())
                .endedAt(readOnlyAuction.getEndedAt())
                .thumbnail(thumbnail)
                .handle(handle)
                .content(readOnlyAuction.getContent())
                .build();
    }
}
