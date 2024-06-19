package com.skyhorsemanpower.auction.application.impl;

import com.skyhorsemanpower.auction.application.AuctionService;
import com.skyhorsemanpower.auction.common.exception.CustomException;
import com.skyhorsemanpower.auction.domain.RoundInfo;
import com.skyhorsemanpower.auction.repository.AuctionHistoryRepository;
import com.skyhorsemanpower.auction.common.exception.ResponseStatus;
import com.skyhorsemanpower.auction.data.dto.*;
import com.skyhorsemanpower.auction.domain.AuctionHistory;
import com.skyhorsemanpower.auction.repository.AuctionHistoryReactiveRepository;
import com.skyhorsemanpower.auction.repository.RoundInfoReactiveRepository;
import com.skyhorsemanpower.auction.repository.RoundInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    @Transactional
    public void offerBiddingPrice(OfferBiddingPriceDto offerBiddingPriceDto) {

        // 현재 경매의 라운드 정보 추출
        RoundInfo roundInfo = roundInfoRepository.
                findFirstByAuctionUuidOrderByCreatedAtDesc(offerBiddingPriceDto.getAuctionUuid()).orElseThrow(
                        () -> new CustomException(ResponseStatus.NO_DATA));

        // 입찰 가능 확인
        // 입찰이 안되면 아래 메서드 내에서 예외를 던진다.
        // isUpdateRoundInfo boolean 데이터는 round_info 도큐먼트를 갱신 트리거
        isBiddingPossible(offerBiddingPriceDto, roundInfo);

        // 입찰 정보 저장
        AuctionHistory auctionHistory = AuctionHistory.converter(offerBiddingPriceDto);
        log.info("Saved Auction History Information >>> {}", auctionHistory.toString());

        try {
            auctionHistoryRepository.save(auctionHistory);
        } catch (Exception e) {
            throw new CustomException(ResponseStatus.MONGODB_ERROR);
        }

        // 입찰 후, round_info 도큐먼트 갱신
        updateRoundInfo(roundInfo);
    }

    private void updateRoundInfo(RoundInfo roundInfo) {
        RoundInfo updatedRoundInfo;

        // 다음 라운드로 round_info 도큐먼트 갱신
        if(roundInfo.getLeftNumberOfParticipants().equals(1L)) {
            updatedRoundInfo = RoundInfo.nextRoundUpdate(roundInfo);
        }

        // 동일 라운드에서 round_info 도큐먼트 갱신
        else {
            updatedRoundInfo = RoundInfo.currentRoundUpdate(roundInfo);
        }

        log.info("Updated round_info Document >>> {}", updatedRoundInfo.toString());

        try {
            roundInfoRepository.save(updatedRoundInfo);
        } catch (Exception e) {
            throw new CustomException(ResponseStatus.MONGODB_ERROR);
        }
    }

    private void isBiddingPossible(OfferBiddingPriceDto offerBiddingPriceDto, RoundInfo roundInfo) {
        // 조건1. 입찰 시간 확인
        checkBiddingTime(roundInfo.getRoundStartTime(), roundInfo.getRoundEndTime());
        log.info("입찰 시간 통과");

        // 조건2. 남은 인원이 1 이상
        checkLeftNumberOfParticipant(roundInfo.getLeftNumberOfParticipants());
        log.info("남은 인원 통과");

        // 조건3. round 입찰가와 입력한 입찰가 확인
        checkRoundAndBiddingPrice(offerBiddingPriceDto, roundInfo);
        log.info("라운드 및 입찰가 통과");
    }

    private void checkLeftNumberOfParticipant(Long leftNumberOfParticipants) {
        log.info("leftNumberOfParticipants >>> {}", leftNumberOfParticipants);
        if (leftNumberOfParticipants < 1L) throw new CustomException(ResponseStatus.FULL_PARTICIPANTS);
    }

    private void checkRoundAndBiddingPrice(OfferBiddingPriceDto offerBiddingPriceDto, RoundInfo roundInfo) {
        log.info("input round >>> {}, document round >>> {}, input price >>> {}, document price >>> {}",
                offerBiddingPriceDto.getRound(), roundInfo.getRound(),
                offerBiddingPriceDto.getBiddingPrice(), roundInfo.getPrice());

        log.info("inputRound == documentRound >>> {}", offerBiddingPriceDto.getRound() == roundInfo.getRound());
        log.info("inputPrice.compareTo(documentPrice) == 0 >>> {}",
                offerBiddingPriceDto.getBiddingPrice().compareTo(roundInfo.getPrice()) == 0);

        if (!(offerBiddingPriceDto.getBiddingPrice().compareTo(roundInfo.getPrice()) == 0) ||
                !(offerBiddingPriceDto.getRound() == roundInfo.getRound())) {
            throw new CustomException(ResponseStatus.NOT_EQUAL_ROUND_INFORMATION);
        }
    }

    private void checkBiddingTime(LocalDateTime roundStartTime, LocalDateTime roundEndTime) {
        log.info("roundStartTime >>> {}, now >>> {}, roundEndTime >>> {}",
                roundStartTime, LocalDateTime.now(), roundEndTime);
        log.info("roundStartTime.isBefore(LocalDateTime.now()) >>> {}, roundEndTime.isAfter(LocalDateTime.now()) >>> {}"
                , roundStartTime.isBefore(LocalDateTime.now()), roundEndTime.isAfter(LocalDateTime.now()));
        // roundStartTime <= 입찰 시간 <= roundEndTime
        if (!(roundStartTime.isBefore(LocalDateTime.now()) && roundEndTime.isAfter(LocalDateTime.now()))) {
            throw new CustomException(ResponseStatus.NOT_BIDDING_TIME);
        }
    }

}
