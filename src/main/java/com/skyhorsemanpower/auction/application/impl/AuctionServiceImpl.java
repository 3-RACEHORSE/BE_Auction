package com.skyhorsemanpower.auction.application.impl;

import com.skyhorsemanpower.auction.application.AuctionService;
import com.skyhorsemanpower.auction.common.CustomException;
import com.skyhorsemanpower.auction.data.projection.ParticipatedAuctionHistoryProjection;
import com.skyhorsemanpower.auction.data.vo.CreatedAuctionHistoryResponseVo;
import com.skyhorsemanpower.auction.data.vo.ParticipatedAuctionHistoryResponseVo;
import com.skyhorsemanpower.auction.repository.AuctionHistoryRepository;
import com.skyhorsemanpower.auction.status.AuctionStateEnum;
import com.skyhorsemanpower.auction.status.ResponseStatus;
import com.skyhorsemanpower.auction.config.QuartzConfig;
import com.skyhorsemanpower.auction.data.dto.*;
import com.skyhorsemanpower.auction.data.vo.SearchAllAuctionResponseVo;
import com.skyhorsemanpower.auction.data.vo.SearchAuctionResponseVo;
import com.skyhorsemanpower.auction.domain.AuctionHistory;
import com.skyhorsemanpower.auction.domain.AuctionImages;
import com.skyhorsemanpower.auction.domain.cqrs.command.CommandOnlyAuction;
import com.skyhorsemanpower.auction.domain.cqrs.read.ReadOnlyAuction;
import com.skyhorsemanpower.auction.repository.AuctionHistoryReactiveRepository;
import com.skyhorsemanpower.auction.repository.AuctionImagesRepository;
import com.skyhorsemanpower.auction.repository.cqrs.command.CommandOnlyAuctionRepository;
import com.skyhorsemanpower.auction.repository.cqrs.read.ReadOnlyAuctionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuctionServiceImpl implements AuctionService {

    private final CommandOnlyAuctionRepository commandOnlyAuctionRepository;
    private final ReadOnlyAuctionRepository readOnlyAuctionRepository;
    private final AuctionImagesRepository auctionImagesRepository;
    private final AuctionHistoryReactiveRepository auctionHistoryReactiveRepository;
    private final AuctionHistoryRepository auctionHistoryRepository;
    private final MongoTemplate mongoTemplate;
    private final QuartzConfig quartzConfig;

    @Override
    @Transactional
    public void createAuction(CreateAuctionDto createAuctionDto) {
        // auctionUuid 제작
        String auctionUuid = generateAuctionUuid();
        createAuctionDto.setAuctionUuid(auctionUuid);

        // PostgreSQL 저장
        createCommandOnlyAution(createAuctionDto);
        createAuctionImages(createAuctionDto);

        // MongoDB 저장
        createReadOnlyAuction(createAuctionDto);

        // 스케줄러에 경매 마감 등록
        try {
            quartzConfig.schedulerEndAuctionJob(auctionUuid);
        } catch (SchedulerException e) {
            throw new CustomException(ResponseStatus.SCHEDULER_ERROR);
        }
    }

    private String generateAuctionUuid() {
        // 현재 날짜와 시간을 "yyyyMMddHHmm" 형식으로 포맷
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        String dateTime = LocalDateTime.now().format(formatter);

        // UUID 생성 후 앞부분의 10자리 문자열 추출
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 10);

        // 날짜 시간과 UUID의 앞부분을 합쳐 UUID 생성
        return dateTime + "-" + uuid;
    }

    private void createAuctionImages(CreateAuctionDto createAuctionDto) {

        try {
            // 썸네일 저장
            AuctionImages auctionThumbnailImage = AuctionImages.builder()
                    .auctionUuid(createAuctionDto.getAuctionUuid())
                    .imageUrl(createAuctionDto.getThumbnail())
                    .isThumbnail(true)
                    .build();

            auctionImagesRepository.save(auctionThumbnailImage);

            // 일반 이미지 저장
            List<String> images = createAuctionDto.getImages();

            for (String image : images) {
                AuctionImages auctionImages = AuctionImages.builder()
                        .auctionUuid(createAuctionDto.getAuctionUuid())
                        .imageUrl(image)
                        .isThumbnail(false)
                        .build();

                auctionImagesRepository.save(auctionImages);
            }
        } catch (Exception e) {
            throw new CustomException(ResponseStatus.POSTGRESQL_ERROR);
        }
    }

    @Override
    public List<SearchAllAuctionResponseVo> searchAllAuctionResponseVo(SearchAllAuctionDto searchAuctionDto) {
        List<SearchAllAuctionResponseVo> searchAllAuctionResponseVos = new ArrayList<>();
        SearchAllAuctionResponseVo searchAllAuctionResponseVo;
        List<ReadOnlyAuction> readOnlyAuctions = new ArrayList<>();
        String thumbnail;

        // keyword와 category 없으면 전체 검색
        if (searchAuctionDto.getKeyword() == null && searchAuctionDto.getCategory() == null) {
            readOnlyAuctions = searchAllAuction();
        }

        // keyword 검색
        else if (searchAuctionDto.getKeyword() != null && searchAuctionDto.getCategory() == null) {
            readOnlyAuctions = searchAuctionByKeyword(searchAuctionDto.getKeyword());
        }

        // category 검색
        else if (searchAuctionDto.getKeyword() == null && searchAuctionDto.getCategory() != null) {
            readOnlyAuctions = searchAuctionByCategory(searchAuctionDto.getCategory());
        }

        // keyword, category 혼합 검색
        else {
            readOnlyAuctions = searchAuctionByKeywordAndCategory(searchAuctionDto.getKeyword(), searchAuctionDto.getCategory());
        }

        for (ReadOnlyAuction readOnlyAuction : readOnlyAuctions) {
            // 각 경매의 auctionUuid를 통해 thumbnail 가져오기
            // thumbnail은 null이 가능하다.
            thumbnail = auctionImagesRepository.getThumbnailUrl(readOnlyAuction.getAuctionUuid());

            //Todo handle을 회원 서비스에서 받아와야 한다.
            String handle = "handle";

            searchAllAuctionResponseVo = SearchAllAuctionResponseVo.readOnlyAuctionToSearchAllAuctionResponseVo(readOnlyAuction, thumbnail, handle);
            searchAllAuctionResponseVos.add(searchAllAuctionResponseVo);
        }
        return searchAllAuctionResponseVos;
    }


    // keyword, category 혼합 검색
    private List<ReadOnlyAuction> searchAuctionByKeywordAndCategory(String keyword, String category) {
        try {
            return readOnlyAuctionRepository.findAllByTitleLikeAndCategory(keyword, category);
        } catch (Exception e) {
            throw new CustomException(ResponseStatus.MONGODB_ERROR);
        }
    }


    // category 검색
    private List<ReadOnlyAuction> searchAuctionByCategory(String category) {
        try {
            return readOnlyAuctionRepository.findAllByCategory(category);
        } catch (Exception e) {
            throw new CustomException(ResponseStatus.MONGODB_ERROR);
        }
    }


    // keyword 검색
    private List<ReadOnlyAuction> searchAuctionByKeyword(String keyword) {
        try {
            return readOnlyAuctionRepository.findAllByTitleLike(keyword);
        } catch (Exception e) {
            throw new CustomException(ResponseStatus.MONGODB_ERROR);
        }
    }


    // 현재 진행되는 전체 경매글 검색
    private List<ReadOnlyAuction> searchAllAuction() {
        Criteria criteria = new Criteria().andOperator(
                Criteria.where("createdAt").lte(LocalDateTime.now()),
                Criteria.where("endedAt").gte(LocalDateTime.now())
        );

        Query query = new Query(criteria);

        try {
            return mongoTemplate.find(query, ReadOnlyAuction.class);
        } catch (Exception e) {
            throw new CustomException(ResponseStatus.MONGODB_ERROR);
        }
    }

    @Override
    public SearchAuctionResponseVo searchAuction(SearchAuctionDto searchAuctionDto) {
        ReadOnlyAuction auction = readOnlyAuctionRepository.findByAuctionUuid(searchAuctionDto.getAuctionUuid()).orElseThrow(
                () -> new CustomException(ResponseStatus.NO_DATA)
        );

        //Todo handle을 회원 서비스에서 받아와야 한다.
        String handle = "handle";

        return SearchAuctionResponseVo.builder()
                .readOnlyAuction(auction)
                .handle(handle)
                .thumbnail(auctionImagesRepository.getThumbnailUrl(searchAuctionDto.getAuctionUuid()))
                .images(auctionImagesRepository.getImagesUrl(searchAuctionDto.getAuctionUuid()))
                .build();
    }

    @Override
    public void offerBiddingPrice(OfferBiddingPriceDto offerBiddingPriceDto) {
        // 마감 시간이 현재 시간보다 미래면 입찰 제시 가능
        if (isAuctionActive(offerBiddingPriceDto.getAuctionUuid())) {
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
        } else throw new CustomException(ResponseStatus.NOT_BIDDING_TIME);
    }

    private boolean isAuctionActive(String auctionUuid) {
        ReadOnlyAuction readOnlyAuction = readOnlyAuctionRepository.findByAuctionUuid(auctionUuid).orElseThrow(
                () -> new CustomException(ResponseStatus.MONGODB_ERROR)
        );

        // 마감 시간이 현재 시간보다 미래면 true 반환
        return readOnlyAuction.getEndedAt().isAfter(LocalDateTime.now());
    }

    @Override
    public Flux<AuctionHistory> searchBiddingPrice(SearchBiddingPriceDto searchBiddingPriceDto) {

        return auctionHistoryReactiveRepository.searchBiddingPrice(searchBiddingPriceDto.getAuctionUuid());
    }

    @Override
    public List<CreatedAuctionHistoryResponseVo> createdAuctionHistory(CreatedAuctionHistoryDto createdAuctionHistoryDto) {
        List<CreatedAuctionHistoryResponseVo> createdAuctionHistoryResponseVos = new ArrayList<>();
        CreatedAuctionHistoryResponseVo createdAuctionHistoryResponseVo = new CreatedAuctionHistoryResponseVo();

        // 최신순으로 자신의 경매 내역 조회
        List<ReadOnlyAuction> auctions = readOnlyAuctionRepository.findAllBySellerUuidOrderByCreatedAtDesc(createdAuctionHistoryDto.getSellerUuid());

        // 경매에 따른 thumbnail과 낙찰자 handle 조회
        for (ReadOnlyAuction auction : auctions) {
            // thumbnail 호출
            String thumbnail = auctionImagesRepository.getThumbnailUrl(auction.getAuctionUuid());

            //Todo handle을 회원 서비스에서 받아와야 한다.
            String handle = "handle";

            createdAuctionHistoryResponseVos.add(createdAuctionHistoryResponseVo.toVo(auction, thumbnail, handle));
        }
        return createdAuctionHistoryResponseVos;
    }

    @Override
    public List<ParticipatedAuctionHistoryResponseVo> participatedAuctionHistory(ParticipatedAuctionHistoryDto participatedAuctionHistoryDto) {
        List<ParticipatedAuctionHistoryResponseVo> participatedAuctionHistoryResponseVos = new ArrayList<>();
        ParticipatedAuctionHistoryResponseVo participatedAuctionHistoryResponseVo = new ParticipatedAuctionHistoryResponseVo();

        // 참여한 경매의 중복 제거한 auctionUuid 리스트 조회
        List<ParticipatedAuctionHistoryProjection> participatedAuctionHistoryProjections = getAuctionUuidList(participatedAuctionHistoryDto.getSellerUuid());

        // auctionHistoryProjection 객체를 통해 auctionUuid를 반환
        for (ParticipatedAuctionHistoryProjection participatedAuctionHistoryProjection : participatedAuctionHistoryProjections) {
            // thumbnail 호출
            String thumbnail = auctionImagesRepository.getThumbnailUrl(participatedAuctionHistoryProjection.getAuctionUuid());

            //Todo handle을 회원 서비스에서 받아와야 한다.
            String handle = "handle";

            // auction 엔티티 조회
            ReadOnlyAuction auction = readOnlyAuctionRepository.findByAuctionUuid(participatedAuctionHistoryProjection.getAuctionUuid())
                    .orElseThrow(
                            () -> new CustomException(ResponseStatus.NO_DATA)
                    );
            participatedAuctionHistoryResponseVos.add(participatedAuctionHistoryResponseVo.toVo(auction, thumbnail, handle));
        }
        return participatedAuctionHistoryResponseVos;
    }

    private List<ParticipatedAuctionHistoryProjection> getAuctionUuidList(String sellerUuid) {
        Query query = new Query(Criteria.where("biddingUuid").is(sellerUuid));

        List<String> distinctAuctionUuids = mongoTemplate.query(AuctionHistory.class)
                .distinct("auctionUuid")
                .matching(query)
                .as(String.class)
                .all();

        // 조회 결과가 없는 경우
        if (distinctAuctionUuids.isEmpty()) throw new CustomException(ResponseStatus.NO_DATA);

        // 조회 결과가 있는 경우
        return distinctAuctionUuids.stream()
                .map(auctionUuid -> ParticipatedAuctionHistoryProjection.builder()
                        .auctionUuid(auctionUuid)
                        .build())
                .collect(Collectors.toList());
    }

    // MongoDB 경매글 저장
    private void createReadOnlyAuction(CreateAuctionDto createAuctionDto) {
        ReadOnlyAuction readOnlyAuction = ReadOnlyAuction.builder()
                .auctionUuid(createAuctionDto.getAuctionUuid())
                .sellerUuid(createAuctionDto.getSellerUuid())
                .title(createAuctionDto.getTitle())
                .content(createAuctionDto.getContent())
                .category(createAuctionDto.getCategory())
                .minimumBiddingPrice(createAuctionDto.getMinimumBiddingPrice())
                .createdAt(LocalDateTime.now())
                .endedAt(LocalDateTime.now().plusDays(1))
                .bidderUuid("추가 필요")
                .bidPrice(-1)
                .state(AuctionStateEnum.AUCTION_IS_IN_PROGRESS)
                .build();
        try {
            readOnlyAuctionRepository.save(readOnlyAuction);
        } catch (Exception e) {
            throw new CustomException(ResponseStatus.MONGODB_ERROR);
        }
    }

    // PostgreSQL 경매글 저장
    private void createCommandOnlyAution(CreateAuctionDto createAuctionDto) {
        CommandOnlyAuction commandOnlyAuction = CommandOnlyAuction.builder()
                .auctionUuid(createAuctionDto.getAuctionUuid())
                .sellerUuid(createAuctionDto.getSellerUuid())
                .title(createAuctionDto.getTitle())
                .content(createAuctionDto.getContent())
                .category(createAuctionDto.getCategory())
                .minimumBiddingPrice(createAuctionDto.getMinimumBiddingPrice())
                .bidderUuid("추가 필요")
                .bidPrice(-1)
                .state(AuctionStateEnum.AUCTION_IS_IN_PROGRESS)
                .build();

        try {
            commandOnlyAuctionRepository.save(commandOnlyAuction);
        } catch (Exception e) {
            throw new CustomException(ResponseStatus.POSTGRESQL_ERROR);
        }
    }

}
