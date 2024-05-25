package com.skyhorsemanpower.auction.data.vo;

import com.skyhorsemanpower.auction.domain.cqrs.read.ReadOnlyAuction;
import com.skyhorsemanpower.auction.status.AuctionStateEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
public class CreatedAuctionHistoryResponseVo {
    private String auctionUuid;
    private String title;
    private String category;
    private int minimumBiddingPrice;
    private LocalDateTime createdAt;
    private LocalDateTime endedAt;
    private int bidPrice;
    private AuctionStateEnum state;
    private String handle;
    private String thumbnail;
    private String content;

    @Builder
    public CreatedAuctionHistoryResponseVo(String auctionUuid, String title, String category, int minimumBiddingPrice, LocalDateTime createdAt, LocalDateTime endedAt, int bidPrice, AuctionStateEnum state, String handle, String thumbnail, String content) {
        this.auctionUuid = auctionUuid;
        this.title = title;
        this.category = category;
        this.minimumBiddingPrice = minimumBiddingPrice;
        this.createdAt = createdAt;
        this.endedAt = endedAt;
        this.bidPrice = bidPrice;
        this.state = state;
        this.handle = handle;
        this.thumbnail = thumbnail;
        this.content = content;
    }

    // auction 엔티티와 thumbnail, handle 로 InquiryAuctionHistoryResponseVo 생성
    public CreatedAuctionHistoryResponseVo toVo(ReadOnlyAuction readOnlyAuction, String thumbnail, String handle) {
        return CreatedAuctionHistoryResponseVo.builder()
                .auctionUuid(readOnlyAuction.getAuctionUuid())
                .title(readOnlyAuction.getTitle())
                .category(readOnlyAuction.getCategory())
                .minimumBiddingPrice(readOnlyAuction.getMinimumBiddingPrice())
                .createdAt(readOnlyAuction.getCreatedAt())
                .endedAt(readOnlyAuction.getEndedAt())
                .bidPrice(readOnlyAuction.getBidPrice())
                .state(readOnlyAuction.getState())
                .thumbnail(thumbnail)
                .handle(handle)
                .content(readOnlyAuction.getContent())
                .build();
    }
}
