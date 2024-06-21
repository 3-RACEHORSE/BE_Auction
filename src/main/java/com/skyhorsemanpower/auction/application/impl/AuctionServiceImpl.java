package com.skyhorsemanpower.auction.application.impl;

import com.skyhorsemanpower.auction.application.AuctionService;
import com.skyhorsemanpower.auction.common.exception.CustomException;
import com.skyhorsemanpower.auction.domain.AuctionCloseState;
import com.skyhorsemanpower.auction.domain.RoundInfo;
import com.skyhorsemanpower.auction.kafka.KafkaProducerCluster;
import com.skyhorsemanpower.auction.kafka.Topics;
import com.skyhorsemanpower.auction.kafka.dto.AuctionCloseDto;
import com.skyhorsemanpower.auction.kafka.dto.SuccessfulBidDto;
import com.skyhorsemanpower.auction.repository.*;
import com.skyhorsemanpower.auction.common.exception.ResponseStatus;
import com.skyhorsemanpower.auction.data.dto.*;
import com.skyhorsemanpower.auction.domain.AuctionHistory;
import com.skyhorsemanpower.auction.status.AuctionStateEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuctionServiceImpl implements AuctionService {

    private final AuctionHistoryRepository auctionHistoryRepository;
    private final AuctionHistoryReactiveRepository auctionHistoryReactiveRepository;
    private final MongoTemplate mongoTemplate;
    private final RoundInfoReactiveRepository roundInfoReactiveRepository;
    private final RoundInfoRepository roundInfoRepository;
    private final AuctionCloseStateRepository auctionCloseStateRepository;
    private final KafkaProducerCluster producer;

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

    @Override
    public void auctionClose(String auctionUuid) {
        // auction_close_state 도큐먼트에 acutionUuid 데이터가 있으면(마감됐으면) 바로 return
        if(auctionCloseStateRepository.findByAuctionUuid(auctionUuid).isPresent()) {
            log.info("Auction already close");
            return;
        }

        log.info("Auction Close Start");

        // auction_history 도큐먼트를 조회하여 경매 상태를 변경
        if(auctionHistoryRepository.findFirstByAuctionUuidOrderByBiddingTimeDesc(auctionUuid).isEmpty()) {
            log.info("auction_history is not exist! No one bid at auction!");
            producer.sendMessage(Topics.AUCTION_CLOSE_STATE.getTopic(), AuctionStateEnum.AUCTION_NO_PARTICIPANTS);
            return;
        };

        log.info("auction_history is exist!");

        // 경매 마감 로직
        // 마지막 라운드 수, 낙찰 가능 인원 수 조회
        RoundInfo lastRoundInfo = roundInfoRepository.findFirstByAuctionUuidOrderByCreatedAtDesc(auctionUuid)
                .orElseThrow(() -> new CustomException(ResponseStatus.NO_DATA)
        );
        log.info("Last Round Info >>> {}", lastRoundInfo.toString());

        int round = lastRoundInfo.getRound();
        long numberOfParticipants = lastRoundInfo.getNumberOfParticipants();

        // 마감 로직
        // 마지막 라운드 입찰 이력
        List<AuctionHistory> lastRoundAuctionHistory = auctionHistoryRepository.
                findByAuctionUuidAndRoundOrderByBiddingTime(auctionUuid, round);
        log.info("Last Round Auction History >>> {}", lastRoundAuctionHistory.toString());

        // 마지막 - 1 라운드 입찰 이력
        List<AuctionHistory> lastMinusOneRoundAuctionHistory = auctionHistoryRepository.
                findByAuctionUuidAndRoundOrderByBiddingTime(auctionUuid, round - 1);
        log.info("Before Last Round Auction History >>> {}", lastMinusOneRoundAuctionHistory.toString());

        // 마지막 라운드 입찰자를 낙찰자로 고정
        Set<String> memberUuids = new HashSet<>();
        for(AuctionHistory auctionHistory : lastRoundAuctionHistory) {
            memberUuids.add(auctionHistory.getBiddingUuid());
        }

        // 마지막 직전 라운드 입찰자 중 낙찰자 추가
        for(AuctionHistory auctionHistory : lastMinusOneRoundAuctionHistory) {
            // 동일 입찰자 제외하고 추가
            memberUuids.add(auctionHistory.getBiddingUuid());

            // 낙찰 가능 인원 수 만큼 리스트 추가
            if (memberUuids.size() == numberOfParticipants) break;
        }

        log.info("memberUuids >>> {}", memberUuids.toString());

        // 낙찰가는 마지막 이전 라운드에서 biddingPrice로 결정
        BigDecimal price = lastMinusOneRoundAuctionHistory.get(0).getBiddingPrice();
        log.info("price >>> {}", price);

        // 마감 후 auction_close_state = true 처리
        auctionCloseStateRepository.save(AuctionCloseState.builder()
                .auctionUuid(auctionUuid).auctionCloseState(true).build());

        // 카프카로 경매 서비스 메시지 전달
        SuccessfulBidDto successfulBidDto = SuccessfulBidDto.builder()
                .auctionUuid(auctionUuid)
                .memberUuids(memberUuids.stream().toList())
                .price(price)
                .auctionState(AuctionStateEnum.AUCTION_NORMAL_CLOSING)
                .build();
        log.info("Kafka Message To Payment Service >>> {}", successfulBidDto.toString());

        producer.sendMessage(Topics.Constant.SUCCESSFUL_BID, successfulBidDto);

        AuctionCloseDto auctionCloseDto = AuctionCloseDto.builder()
                .auctionUuid(auctionUuid)
                .auctionState(AuctionStateEnum.AUCTION_NORMAL_CLOSING)
                .build();
        log.info("auctionCloseDto >>> {}", auctionCloseDto.toString());

        producer.sendMessage(Topics.Constant.AUCTION_CLOSE_STATE, auctionCloseDto);
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

        // 조건2. 해당 라운드에 참여 여부
        checkBiddingRound(offerBiddingPriceDto.getBiddingUuid(), offerBiddingPriceDto.getRound());
        log.info("현재 라운드에 참여한 적 없음");

        // 조건3. 남은 인원이 1 이상
        checkLeftNumberOfParticipant(roundInfo.getLeftNumberOfParticipants());
        log.info("남은 인원 통과");

        // 조건4. round 입찰가와 입력한 입찰가 확인
        checkRoundAndBiddingPrice(offerBiddingPriceDto, roundInfo);
        log.info("라운드 및 입찰가 통과");
    }

    private void checkBiddingRound(String biddingUuid, int round) {
        if(auctionHistoryRepository.findByBiddingUuidAndRound(biddingUuid, round).isPresent()) {
            throw new CustomException(ResponseStatus.ALREADY_BID_IN_ROUND);
        };
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
