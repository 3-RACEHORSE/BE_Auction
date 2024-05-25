package com.skyhorsemanpower.auction.data.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class MainStatisticResponseVo {
    private String totalAuctionCount;
    private String weeklyAddedAuctionCount;
    private String dailyTotalAuctionCount;
    private String currentTimeAddedAuctionCount;
    private String biddingRate;
    private String closedAuctionCount;
    private String progressingAuctionCount;

    @Builder
    public MainStatisticResponseVo(String totalAuctionCount, String weeklyAddedAuctionCount, String dailyTotalAuctionCount, String currentTimeAddedAuctionCount, String biddingRate, String closedAuctionCount, String progressingAuctionCount) {
        this.totalAuctionCount = totalAuctionCount;
        this.weeklyAddedAuctionCount = weeklyAddedAuctionCount;
        this.dailyTotalAuctionCount = dailyTotalAuctionCount;
        this.currentTimeAddedAuctionCount = currentTimeAddedAuctionCount;
        this.biddingRate = biddingRate;
        this.closedAuctionCount = closedAuctionCount;
        this.progressingAuctionCount = progressingAuctionCount;
    }
}
