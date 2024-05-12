package com.skyhorsemanpower.auction.domain.read;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@AllArgsConstructor
@Document(collection = "auction")
public class ReadOnlyAuction {

    @Id
    private String auctionPostId;

    private String auctionUuid;
    private String uuid;
    private String handle;
    private String title;
    private String content;
    private int minimumBiddingPrice;
    private LocalDateTime createdAt;
    private LocalDateTime endedAt;


    @Builder
    public ReadOnlyAuction(String auctionUuid, String uuid, String handle, String title, String content, int minimumBiddingPrice, LocalDateTime createdAt, LocalDateTime endedAt) {
        this.auctionUuid = auctionUuid;
        this.uuid = uuid;
        this.handle = handle;
        this.title = title;
        this.content = content;
        this.minimumBiddingPrice = minimumBiddingPrice;
        this.createdAt = LocalDateTime.now(); // 현재 시간으로 설정
        this.endedAt = this.createdAt.plusDays(1);
    }
}
