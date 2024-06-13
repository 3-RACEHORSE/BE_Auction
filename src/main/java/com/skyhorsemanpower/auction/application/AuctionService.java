package com.skyhorsemanpower.auction.application;

import com.skyhorsemanpower.auction.data.dto.*;
import com.skyhorsemanpower.auction.domain.AuctionHistory;
import reactor.core.publisher.Flux;

public interface AuctionService {
    void offerBiddingPrice(OfferBiddingPriceDto offerBiddingPriceDto);

    Flux<AuctionHistory> searchBiddingPrice(SearchBiddingPriceDto searchBiddingPriceDto);
}
