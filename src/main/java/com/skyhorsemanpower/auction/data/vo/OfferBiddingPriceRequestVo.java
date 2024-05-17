package com.skyhorsemanpower.auction.data.vo;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class OfferBiddingPriceRequestVo {
    private String auctionUuid;
    private int biddingPrice;
}
