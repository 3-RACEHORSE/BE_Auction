package com.skyhorsemanpower.auction.domain.cqrs.read;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@ToString
@Document(collection = "auction")
public class ReadOnlyAuction {

    @Id
    private String auctionPostId;

    private String auctionUuid;
    private String sellerUuid;
    private String handle;
    private String title;
    private String content;
    private String category;
    private int minimumBiddingPrice;
    private LocalDateTime createdAt;
    private LocalDateTime endedAt;
    private String bidderUuid;
    private int bidPrice;


    @Builder
    public ReadOnlyAuction(String auctionPostId, String auctionUuid, String sellerUuid, String handle, String title, String content, String category, int minimumBiddingPrice, LocalDateTime createdAt, LocalDateTime endedAt, String bidderUuid, int bidPrice) {
        this.auctionPostId = auctionPostId;
        this.auctionUuid = auctionUuid;
        this.sellerUuid = sellerUuid;
        this.handle = handle;
        this.title = title;
        this.content = content;
        this.category = category;
        this.minimumBiddingPrice = minimumBiddingPrice;
        this.createdAt = createdAt;
        this.endedAt = endedAt;
        this.bidderUuid = bidderUuid;
        this.bidPrice = bidPrice;
    }
}
