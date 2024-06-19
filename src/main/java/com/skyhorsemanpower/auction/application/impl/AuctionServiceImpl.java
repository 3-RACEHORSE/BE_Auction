package com.skyhorsemanpower.auction.application.impl;

import com.skyhorsemanpower.auction.application.AuctionService;
import com.skyhorsemanpower.auction.common.exception.CustomException;
import com.skyhorsemanpower.auction.repository.AuctionHistoryRepository;
import com.skyhorsemanpower.auction.common.exception.ResponseStatus;
import com.skyhorsemanpower.auction.data.dto.*;
import com.skyhorsemanpower.auction.data.vo.domain.AuctionHistory;
import com.skyhorsemanpower.auction.repository.AuctionHistoryReactiveRepository;
import com.skyhorsemanpower.auction.repository.RoundInfoReactiveRepository;
import com.skyhorsemanpower.auction.repository.RoundInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuctionServiceImpl implements AuctionService {

    private final AuctionHistoryRepository auctionHistoryRepository;
    private final AuctionHistoryReactiveRepository auctionHistoryReactiveRepository;
    private final MongoTemplate mongoTemplate;
    private final RoundInfoReactiveRepository roundInfoReactiveRepository;
    private final RoundInfoRepository roundInfoRepository;

    @Override
    public void offerBiddingPrice(OfferBiddingPriceDto offerBiddingPriceDto) {
        // 우선순위가 있는 입찰 조건
        // 조건1. 마감 시간이 현재 시간보다 미래면 입찰 제시 가능
        // 조건2. 경매 작성자는 경매에 참여할 수 없음
        // 조건3. 입찰 제시가가 최고 입찰가보다 커야한다.
//        if (isAuctionActive(offerBiddingPriceDto.getAuctionUuid()) &&
//                !isAuctionSeller(offerBiddingPriceDto.getAuctionUuid(), offerBiddingPriceDto.getBiddingUuid()) &&
//                checkBiddingPrice(offerBiddingPriceDto.getBiddingUuid(), offerBiddingPriceDto.getAuctionUuid(),
//                        offerBiddingPriceDto.getBiddingPrice())) {
            AuctionHistory auctionHistory = AuctionHistory.builder()
                    .auctionUuid(offerBiddingPriceDto.getAuctionUuid())
                    .biddingUuid(offerBiddingPriceDto.getBiddingUuid())
                    .biddingPrice(offerBiddingPriceDto.getBiddingPrice())
                    .biddingTime(LocalDateTime.now())
                    .build();
            try {
                auctionHistoryReactiveRepository.save(auctionHistory).subscribe();
            } catch (Exception e) {
                throw new CustomException(ResponseStatus.MONGODB_ERROR);
            }
        }
//        else throw new CustomException(ResponseStatus.CAN_NOT_BIDDING);
//    }

    private boolean isAuctionSeller(String auctionUuid, String biddingUuid) {
        return true;
    }

    private boolean checkBiddingPrice(String biddingUuid, String auctionUuid, int biddingPrice) {
        return true;
    }

    private boolean isAuctionActive(String auctionUuid) {
        return true;
    }

}
