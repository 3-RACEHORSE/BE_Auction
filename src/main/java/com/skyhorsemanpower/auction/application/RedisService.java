package com.skyhorsemanpower.auction.application;

import com.skyhorsemanpower.auction.data.vo.AuctionPageResponseVo;
import com.skyhorsemanpower.auction.data.vo.StandbyResponseVo;

public interface RedisService {
    AuctionPageResponseVo getAuctionPage(String auctionUuid);

    StandbyResponseVo getStandbyPage(String auctionUuid);
}
