package com.skyhorsemanpower.auction.application;

import com.skyhorsemanpower.auction.data.dto.*;
import com.skyhorsemanpower.auction.data.vo.SearchAllAuctionResponseVo;
import com.skyhorsemanpower.auction.data.vo.SearchAuctionResponseVo;
import com.skyhorsemanpower.auction.domain.AuctionHistory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AuctionService {
    void createAuction(CreateAuctionDto createAuctionDto);

    List<SearchAllAuctionResponseVo> searchAllAuctionResponseVo(SearchAllAuctionDto searchAuctionDto);

    SearchAuctionResponseVo searchAuction(SearchAuctionDto searchAuctionDto);

    void offerBiddingPrice(OfferBiddingPriceDto offerBiddingPriceDto);

    Flux<AuctionHistory> searchBiddingPrice(SearchBiddingPriceDto searchBiddingPriceDto);
}
