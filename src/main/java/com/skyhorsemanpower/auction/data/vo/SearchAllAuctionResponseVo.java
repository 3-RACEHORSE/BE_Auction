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
public class SearchAllAuctionResponseVo {
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

    @Builder
    public SearchAllAuctionResponseVo(String auctionUuid, String handle, String sellerUuid, String title, String content, String category, int minimumBiddingPrice, String thumbnail, LocalDateTime createdAt, LocalDateTime endedAt) {
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
    }

    public static SearchAllAuctionResponseVo readOnlyAuctionToSearchAllAuctionResponseVo(ReadOnlyAuction readOnlyAuction, String thumbnail, String handle) {
        return SearchAllAuctionResponseVo.builder().
                auctionUuid(readOnlyAuction.getAuctionUuid())
                .handle(handle)
                .sellerUuid(readOnlyAuction.getSellerUuid())
                .title(readOnlyAuction.getTitle())
                .content(readOnlyAuction.getContent())
                .category(readOnlyAuction.getCategory())
                .minimumBiddingPrice(readOnlyAuction.getMinimumBiddingPrice())
                .thumbnail(thumbnail)
                .createdAt(readOnlyAuction.getCreatedAt())
                .endedAt(readOnlyAuction.getEndedAt())
                .build();
    }
}
