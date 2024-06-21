package com.skyhorsemanpower.auction.kafka.dto;

import com.skyhorsemanpower.auction.status.AuctionStateEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class AuctionCloseDto {
    private String auctionUuid;
    private AuctionStateEnum auctionState;

    @Builder
    public AuctionCloseDto(String auctionUuid, AuctionStateEnum auctionState) {
        this.auctionUuid = auctionUuid;
        this.auctionState = auctionState;
    }
}
