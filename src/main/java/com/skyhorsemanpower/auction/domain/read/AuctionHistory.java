package com.skyhorsemanpower.auction.domain.read;

import com.skyhorsemanpower.auction.common.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@NoArgsConstructor
@Document(collection = "auction_history")
public class AuctionHistory extends BaseTimeEntity {
    @Id
    private String auctionHistoryId;

    private String auctionUuid;
    private String biddingUuid;
    private int biddingPrice;

    @Builder
    public AuctionHistory(String auctionHistoryId, String auctionUuid, String biddingUuid, int biddingPrice) {
        this.auctionHistoryId = auctionHistoryId;
        this.auctionUuid = auctionUuid;
        this.biddingUuid = biddingUuid;
        this.biddingPrice = biddingPrice;
    }
}
