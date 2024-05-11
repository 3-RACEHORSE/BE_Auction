package com.skyhorsemanpower.auction.domain;

import com.skyhorsemanpower.auction.common.BaseTimeEntity;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document(collection = "auction_history")
public class AuctionHistory extends BaseTimeEntity {
    @Id
    private String auctionHistoryId;

    private String auctionUuid;
    private String biddingUuid;
    private int biddingPrice;
}
