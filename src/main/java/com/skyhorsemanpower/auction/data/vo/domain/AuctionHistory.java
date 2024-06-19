package com.skyhorsemanpower.auction.data.vo.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@ToString
@Document(collection = "auction_history")
public class AuctionHistory {
    @Id
    private String auctionHistoryId;

    private String auctionUuid;
    private String biddingUuid;
    private int biddingPrice;
    private LocalDateTime biddingTime;
    private Integer round;

    @Builder
    public AuctionHistory(String auctionUuid, String biddingUuid, int biddingPrice,
                          LocalDateTime biddingTime, Integer round) {
        this.auctionUuid = auctionUuid;
        this.biddingUuid = biddingUuid;
        this.biddingPrice = biddingPrice;
        this.biddingTime = biddingTime;
        this.round = round;
    }
}
