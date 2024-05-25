package com.skyhorsemanpower.auction.data.projection;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class ParticipatedAuctionHistoryProjection {
    private String auctionUuid;

    @Builder
    public ParticipatedAuctionHistoryProjection(String auctionUuid) {
        this.auctionUuid = auctionUuid;
    }
}
