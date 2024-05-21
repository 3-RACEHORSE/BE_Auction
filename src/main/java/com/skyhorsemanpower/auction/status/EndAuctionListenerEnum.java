package com.skyhorsemanpower.auction.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EndAuctionListenerEnum {
    RETRY_THREE_DELAY_5000(3, 5000);

    private final int MAX_RETRY_ATTEMPTS;
    private final long RETRY_DELAY_MILLIS;
}
