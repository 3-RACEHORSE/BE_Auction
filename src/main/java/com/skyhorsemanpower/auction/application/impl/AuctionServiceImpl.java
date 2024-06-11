package com.skyhorsemanpower.auction.application.impl;

import com.skyhorsemanpower.auction.application.AuctionService;
import com.skyhorsemanpower.auction.common.CustomException;
import com.skyhorsemanpower.auction.data.projection.CheckBiddingPriceProjection;
import com.skyhorsemanpower.auction.data.projection.ParticipatedAuctionHistoryProjection;
import com.skyhorsemanpower.auction.data.vo.*;
import com.skyhorsemanpower.auction.repository.AuctionHistoryRepository;
import com.skyhorsemanpower.auction.status.ResponseStatus;
import com.skyhorsemanpower.auction.data.dto.*;
import com.skyhorsemanpower.auction.domain.AuctionHistory;
import com.skyhorsemanpower.auction.domain.cqrs.read.ReadOnlyAuction;
import com.skyhorsemanpower.auction.repository.AuctionHistoryReactiveRepository;
import com.skyhorsemanpower.auction.common.ServerPathEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuctionServiceImpl implements AuctionService {

    private final AuctionHistoryRepository auctionHistoryRepository;
    private final AuctionHistoryReactiveRepository auctionHistoryReactiveRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public void offerBiddingPrice(OfferBiddingPriceDto offerBiddingPriceDto) {
        // 우선순위가 있는 입찰 조건
        // 조건1. 마감 시간이 현재 시간보다 미래면 입찰 제시 가능
        // 조건2. 경매 작성자는 경매에 참여할 수 없음
        // 조건3. 입찰 제시가가 최고 입찰가보다 커야한다.
        if (isAuctionActive(offerBiddingPriceDto.getAuctionUuid()) &&
                !isAuctionSeller(offerBiddingPriceDto.getAuctionUuid(), offerBiddingPriceDto.getBiddingUuid()) &&
                checkBiddingPrice(offerBiddingPriceDto.getBiddingUuid(), offerBiddingPriceDto.getAuctionUuid(),
                        offerBiddingPriceDto.getBiddingPrice())) {
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
        } else throw new CustomException(ResponseStatus.CAN_NOT_BIDDING);
    }


    private boolean checkBiddingPrice(String biddingUuid, String auctionUuid, int biddingPrice) {
        Optional<CheckBiddingPriceProjection> optionalMaxBiddingPrice = auctionHistoryRepository
                .findMaxBiddingPriceByAuctionUuid(auctionUuid);
        int minimumBiddingPrice = readOnlyAuctionRepository.findByAuctionUuid(auctionUuid).orElseThrow(
                () -> new CustomException(ResponseStatus.NO_DATA)
        ).getMinimumBiddingPrice();

        // 최초 입찰인 경우 바로 입찰되도록 true 반환
        if (optionalMaxBiddingPrice.isEmpty() && biddingPrice >= minimumBiddingPrice) return true;

        if (optionalMaxBiddingPrice.isPresent()) {
            int maxBiddingPrice = optionalMaxBiddingPrice.get().getBiddingPrice();
            if (biddingPrice > maxBiddingPrice && biddingPrice >= minimumBiddingPrice) return true;
        }
        throw new CustomException(ResponseStatus.UNSATISFING_BIDDING_PRICE);
    }

    private boolean isAuctionActive(String auctionUuid) {
        ReadOnlyAuction readOnlyAuction = readOnlyAuctionRepository.findByAuctionUuid(auctionUuid).orElseThrow(
                () -> new CustomException(ResponseStatus.NO_DATA)
        );

        // 마감 시간이 현재 시간보다 미래면 true 반환
        if (readOnlyAuction.getEndedAt().isAfter(LocalDateTime.now())) return true;
        else throw new CustomException(ResponseStatus.NOT_BIDDING_TIME);
    }

    @Override
    public Flux<AuctionHistory> searchBiddingPrice(SearchBiddingPriceDto searchBiddingPriceDto) {

        return auctionHistoryReactiveRepository.searchBiddingPrice(searchBiddingPriceDto.getAuctionUuid());
    }

    @Override
    public List<ParticipatedAuctionHistoryResponseVo> participatedAuctionHistory(
            ParticipatedAuctionHistoryDto participatedAuctionHistoryDto) {
        List<ParticipatedAuctionHistoryResponseVo> participatedAuctionHistoryResponseVos = new ArrayList<>();
        ParticipatedAuctionHistoryResponseVo participatedAuctionHistoryResponseVo =
                new ParticipatedAuctionHistoryResponseVo();

        // 참여한 경매의 중복 제거한 auctionUuid 리스트 조회
        List<ParticipatedAuctionHistoryProjection> participatedAuctionHistoryProjections =
                getAuctionUuidList(participatedAuctionHistoryDto.getParticipateUuid());

        // auctionHistoryProjection 객체를 통해 auctionUuid를 반환
        for (ParticipatedAuctionHistoryProjection participatedAuctionHistoryProjection :
                participatedAuctionHistoryProjections) {
            // thumbnail 호출
            String thumbnail = auctionImagesRepository
                    .getThumbnailUrl(participatedAuctionHistoryProjection.getAuctionUuid());

            // auction 엔티티 조회
            ReadOnlyAuction auction = readOnlyAuctionRepository
                    .findByAuctionUuid(participatedAuctionHistoryProjection.getAuctionUuid())
                    .orElseThrow(
                            () -> new CustomException(ResponseStatus.NO_DATA)
                    );

            // Todo 배포환경 테스트 필요
            // 배포 환경에서 데이터 받아오는 지 확인 필요
            String handle = getHandleByWebClientBlocking(auction.getSellerUuid());
            participatedAuctionHistoryResponseVos.
                    add(participatedAuctionHistoryResponseVo.toVo(auction, thumbnail, handle));
        }
        return participatedAuctionHistoryResponseVos;
    }


    private List<ParticipatedAuctionHistoryProjection> getAuctionUuidList(String participateUuid) {
        List<ParticipatedAuctionHistoryProjection> distinctAuctionUuids =
                mongoTemplate.findDistinct(
                        "auctionUuid", AuctionHistory.class, ParticipatedAuctionHistoryProjection.class);

        // 조회 결과가 없는 경우
        if (distinctAuctionUuids.isEmpty()) throw new CustomException(ResponseStatus.NO_DATA);

        // 조회 결과가 있는 경우, 입찰 결과를 내림차순 진행
        Collections.reverse(distinctAuctionUuids);
        return distinctAuctionUuids;
    }


    // webClient-blocking 통신으로 회원 서비스에 uuid를 이용해 handle 데이터 요청
    private String getHandleByWebClientBlocking(String uuid) {
        WebClient webClient = WebClient.create(ServerPathEnum.MEMBER_SERVER.getServer());
        ResponseEntity<MemberInfoResponseVo> responseEntity = webClient.get()
                .uri(uriBuilder -> uriBuilder.path(ServerPathEnum.GET_HANDLE.getServer() + "/{uuid}")
                        .build(uuid))
                .retrieve().toEntity(MemberInfoResponseVo.class).block();
        log.info("handle >>> {}", responseEntity.getBody().getHandle());
        return responseEntity.getBody().getHandle();
    }
}
