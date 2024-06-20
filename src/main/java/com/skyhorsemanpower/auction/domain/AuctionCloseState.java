package com.skyhorsemanpower.auction.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@NoArgsConstructor
@ToString
@Document(collection = "auction_close_state")
public class AuctionCloseState {
    @Id
    private String auctionCloseStateId;

    private String auctionUuid;
    private boolean auctionCloseState;

    @Builder
    public AuctionCloseState(String auctionUuid, boolean auctionCloseState) {
        this.auctionUuid = auctionUuid;
        this.auctionCloseState = auctionCloseState;
    }
}
