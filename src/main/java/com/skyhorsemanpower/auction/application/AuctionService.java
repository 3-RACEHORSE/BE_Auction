package com.skyhorsemanpower.auction.application;

import com.skyhorsemanpower.auction.data.dto.CreateAuctionDto;
import com.skyhorsemanpower.auction.data.dto.SearchAllAuctionDto;
import com.skyhorsemanpower.auction.data.dto.SearchAuctionDto;
import com.skyhorsemanpower.auction.data.vo.SearchAllAuctionResponseVo;
import com.skyhorsemanpower.auction.data.vo.SearchAuctionResponseVo;

import java.util.List;

public interface AuctionService {
    void createAuction(CreateAuctionDto createAuctionDto);

    List<SearchAllAuctionResponseVo> searchAllAuctionResponseVo(SearchAllAuctionDto searchAuctionDto);

    SearchAuctionResponseVo searchAuction(SearchAuctionDto searchAuctionDto);
}
