package com.skyhorsemanpower.auction.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuctionStatusEnum {
    // 경매 진행 중
    AUCTION_IS_IN_PROGRESS(1),
    // 경매 정상 마감
    AUCTION_NORMAL_CLOSING(2),
    // 경매 참여자 없음
    AUCTION_NO_PARTICIPANTS(3),
    // 경매 비정상 마감
    AUCTION_ABNORMAL_CLOSING(4);

    private final int status;
}
