package com.skyhorsemanpower.auction.application;

import com.skyhorsemanpower.auction.common.SuccessResponse;
import com.skyhorsemanpower.auction.data.dto.*;
import com.skyhorsemanpower.auction.data.vo.*;
import com.skyhorsemanpower.auction.domain.AuctionHistory;
import reactor.core.publisher.Flux;

import java.util.List;

public interface AuctionService {
    void offerBiddingPrice(OfferBiddingPriceDto offerBiddingPriceDto);

    Flux<AuctionHistory> searchBiddingPrice(SearchBiddingPriceDto searchBiddingPriceDto);

    List<ParticipatedAuctionHistoryResponseVo> participatedAuctionHistory(ParticipatedAuctionHistoryDto participatedAuctionHistoryDto);
}
