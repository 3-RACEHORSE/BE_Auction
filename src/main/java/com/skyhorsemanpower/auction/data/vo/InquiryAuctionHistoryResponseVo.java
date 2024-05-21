package com.skyhorsemanpower.auction.data.vo;

import com.skyhorsemanpower.auction.domain.cqrs.read.ReadOnlyAuction;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class InquiryAuctionHistoryResponseVo {
    private ReadOnlyAuction readOnlyAuction;

    @Builder
    public InquiryAuctionHistoryResponseVo(ReadOnlyAuction readOnlyAuction) {
        this.readOnlyAuction = readOnlyAuction;
    }
}
