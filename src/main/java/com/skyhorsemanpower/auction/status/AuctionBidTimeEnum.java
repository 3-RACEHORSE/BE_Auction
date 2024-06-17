package com.skyhorsemanpower.auction.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuctionBidTimeEnum {
    SECONDS_90(90),
    SECONDS_60(60),
    SECONDS_30(30);
    private final int second;
}
