package com.skyhorsemanpower.auction.application;

import com.skyhorsemanpower.auction.data.vo.AuctionPageResponseVo;

public interface RedisService {
    AuctionPageResponseVo getAuctionPage(String auctionUuid);
}
