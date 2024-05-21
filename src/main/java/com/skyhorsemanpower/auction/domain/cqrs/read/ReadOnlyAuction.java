package com.skyhorsemanpower.auction.domain.cqrs.read;

import com.skyhorsemanpower.auction.status.AuctionStateEnum;
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
    private String title;
    private String content;
    private String category;
    private int minimumBiddingPrice;
    private LocalDateTime createdAt;
    private LocalDateTime endedAt;
    private String bidderUuid;
    private int bidPrice;
    private AuctionStateEnum state;


    @Builder
    public ReadOnlyAuction(String auctionPostId, String auctionUuid, String sellerUuid, String title, String content, String category, int minimumBiddingPrice, LocalDateTime createdAt, LocalDateTime endedAt, String bidderUuid, int bidPrice, AuctionStateEnum state) {
        this.auctionPostId = auctionPostId;
        this.auctionUuid = auctionUuid;
        this.sellerUuid = sellerUuid;
        this.title = title;
        this.content = content;
        this.category = category;
        this.minimumBiddingPrice = minimumBiddingPrice;
        this.createdAt = createdAt;
        this.endedAt = endedAt;
        this.bidderUuid = bidderUuid;
        this.bidPrice = bidPrice;
        this.state = state;
    }
}
