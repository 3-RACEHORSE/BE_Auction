package com.skyhorsemanpower.auction.application;

import com.skyhorsemanpower.auction.data.dto.CreateAuctionDto;

public interface AuctionService {
    void createAuction(CreateAuctionDto createAuctionDto);
}
