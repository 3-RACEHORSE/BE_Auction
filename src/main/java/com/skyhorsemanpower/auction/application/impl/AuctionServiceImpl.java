package com.skyhorsemanpower.auction.application.impl;

import com.skyhorsemanpower.auction.application.AuctionService;
import com.skyhorsemanpower.auction.common.CustomException;
import com.skyhorsemanpower.auction.data.projection.CheckBiddingPriceProjection;
import com.skyhorsemanpower.auction.data.projection.ParticipatedAuctionHistoryProjection;
import com.skyhorsemanpower.auction.data.vo.*;
import com.skyhorsemanpower.auction.repository.AuctionHistoryRepository;
import com.skyhorsemanpower.auction.status.AuctionStateEnum;
import com.skyhorsemanpower.auction.status.PageState;
import com.skyhorsemanpower.auction.status.ResponseStatus;
import com.skyhorsemanpower.auction.config.QuartzConfig;
import com.skyhorsemanpower.auction.data.dto.*;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuctionServiceImpl implements AuctionService {

    private final CommandOnlyAuctionRepository commandOnlyAuctionRepository;
    private final ReadOnlyAuctionRepository readOnlyAuctionRepository;
    private final AuctionImagesRepository auctionImagesRepository;
    private final AuctionHistoryRepository auctionHistoryRepository;
    private final AuctionHistoryReactiveRepository auctionHistoryReactiveRepository;
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
    public SearchAllAuctionResponseVo searchAllAuction(SearchAllAuctionDto searchAuctionDto) {
        Integer page = searchAuctionDto.getPage();
        Integer size = searchAuctionDto.getSize();

        // page, size 미지정 시, 기본값 할당
        if (page == null || page < 0) page = PageState.DEFAULT.getPage();
        if (size == null || size <= 0) size = PageState.DEFAULT.getSize();

        List<SearchAllAuctionResponseVo> searchAllAuctionResponseVos = new ArrayList<>();
        Page<ReadOnlyAuction> readOnlyAuctionPage = Page.empty();
        List<ReadOnlyAuction> readOnlyAuctions = new ArrayList<>();

        // keyword와 category 없으면 전체 검색
        if (searchAuctionDto.getKeyword() == null && searchAuctionDto.getCategory() == null) {
            readOnlyAuctionPage = searchAllAuction(page, size);
        }
        // keyword 검색
        else if (searchAuctionDto.getKeyword() != null && searchAuctionDto.getCategory() == null) {
            readOnlyAuctionPage = searchAuctionByKeyword(searchAuctionDto.getKeyword(), page, size);
        }
        // category 검색
        else if (searchAuctionDto.getKeyword() == null && searchAuctionDto.getCategory() != null) {
            readOnlyAuctionPage = searchAuctionByCategory(searchAuctionDto.getCategory(), page, size);
        }
        // keyword, category 혼합 검색
        else {
            readOnlyAuctionPage = searchAuctionByKeywordAndCategory(searchAuctionDto.getKeyword(), searchAuctionDto.getCategory(), page, size);
        }

        readOnlyAuctions = readOnlyAuctionPage.getContent();
        List<SearchAllAuction> searchAllAuctionList = new ArrayList<>();

        for (ReadOnlyAuction readOnlyAuction : readOnlyAuctions) {
            // 각 경매의 auctionUuid를 통해 thumbnail 가져오기
            // thumbnail은 null이 가능하다.
            String thumbnail = auctionImagesRepository.getThumbnailUrl(readOnlyAuction.getAuctionUuid());

            // Todo handle을 회원 서비스에서 받아와야 한다.
            String handle = "handle";

            searchAllAuctionList.add(SearchAllAuction.builder()
                    .auctionUuid(readOnlyAuction.getAuctionUuid())
                    .handle(handle)
                    .sellerUuid(readOnlyAuction.getSellerUuid())
                    .title(readOnlyAuction.getTitle())
                    .content(readOnlyAuction.getContent())
                    .category(readOnlyAuction.getCategory())
                    .minimumBiddingPrice(readOnlyAuction.getMinimumBiddingPrice())
                    .thumbnail(thumbnail)
                    .createdAt(readOnlyAuction.getCreatedAt())
                    .endedAt(readOnlyAuction.getEndedAt())
                    .build());
        }

        boolean hasNext = readOnlyAuctionPage.hasNext();
        return new SearchAllAuctionResponseVo(searchAllAuctionList, page, hasNext);
    }


    // keyword, category 혼합 검색
    private Page<ReadOnlyAuction> searchAuctionByKeywordAndCategory(String keyword, String category, int page, int size) {
        Page<ReadOnlyAuction> results = readOnlyAuctionRepository.findAllByTitleLikeAndCategory(keyword, category, PageRequest.of(page, size));
        // 조회 결과 있는 경우
        if (!results.isEmpty()) return results;
            // 조회 결과 없는 경우
        else throw new CustomException(ResponseStatus.NO_DATA);
    }


    // category 검색
    private Page<ReadOnlyAuction> searchAuctionByCategory(String category, int page, int size) {
        Page<ReadOnlyAuction> results = readOnlyAuctionRepository.findAllByCategory(category, PageRequest.of(page, size));
        // 조회 결과 있는 경우
        if (!results.isEmpty()) return results;
            // 조회 결과 없는 경우
        else throw new CustomException(ResponseStatus.NO_DATA);
    }


    // keyword 검색
    private Page<ReadOnlyAuction> searchAuctionByKeyword(String keyword, int page, int size) {
        Page<ReadOnlyAuction> results = readOnlyAuctionRepository.findAllByTitleLike(keyword, PageRequest.of(page, size));
        // 조회 결과 있는 경우
        if (!results.isEmpty()) return results;
            // 조회 결과 없는 경우
        else throw new CustomException(ResponseStatus.NO_DATA);
    }

    // 현재 진행되는 전체 경매글 검색
    private Page<ReadOnlyAuction> searchAllAuction(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Criteria criteria = new Criteria().andOperator(
                Criteria.where("createdAt").lte(LocalDateTime.now()),
                Criteria.where("endedAt").gte(LocalDateTime.now())
        );

        Query query = new Query(criteria).with(pageable)
                // 페이지 번호에 따라 결과를 건너뛴다.
                .skip(pageable.getPageSize() * pageable.getPageNumber())
                .limit(pageable.getPageSize());

        List<ReadOnlyAuction> filteredReadOnlyAuction = mongoTemplate.find(query, ReadOnlyAuction.class);

        // 쿼리 결과 목록을 통해 페이지 객체 생성 페이지 객체 생성
        return PageableExecutionUtils.getPage(
                filteredReadOnlyAuction,
                pageable,
                // -1은 정확하게 세겠다는 의미
                () -> mongoTemplate.count(query.skip(-1).limit(-1), ReadOnlyAuction.class)
        );
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
        // 조건1. 마감 시간이 현재 시간보다 미래면 입찰 제시 가능
        // 조건2. 입찰 제시가가 최고 입찰가보다 커야한다.
        if (isAuctionActive(offerBiddingPriceDto.getAuctionUuid())
                && checkBiddingPrice(offerBiddingPriceDto.getBiddingUuid(), offerBiddingPriceDto.getAuctionUuid(), offerBiddingPriceDto.getBiddingPrice())) {
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
    }

    private boolean checkBiddingPrice(String biddingUuid, String auctionUuid, int biddingPrice) {
        Optional<CheckBiddingPriceProjection> optionalMaxBiddingPrice = auctionHistoryRepository.findMaxBiddingPriceByAuctionUuid(auctionUuid);
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
    public List<CreatedAuctionHistoryResponseVo> createdAuctionHistory(CreatedAuctionHistoryDto createdAuctionHistoryDto) {
        List<CreatedAuctionHistoryResponseVo> createdAuctionHistoryResponseVos = new ArrayList<>();
        CreatedAuctionHistoryResponseVo createdAuctionHistoryResponseVo = new CreatedAuctionHistoryResponseVo();

        // 최신순으로 자신의 경매 내역 조회
        List<ReadOnlyAuction> auctions = readOnlyAuctionRepository.findAllBySellerUuidOrderByCreatedAtDesc(createdAuctionHistoryDto.getSellerUuid());

        // 조회 결과 없는 경우
        if (auctions.isEmpty()) throw new CustomException(ResponseStatus.NO_DATA);
            // 조회 결과 있는 경우
        else {
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

    @Override
    public MainStatisticResponseVo mainStatistic() {
        //Todo
        // 스프링 배치 도입하면 집계 테이블에서 해당 데이터를 받아와야 한다.
        String totalAuctionCount = "9999";
        String weeklyAddedAuctionCount = "99";
        String dailyTotalAuctionCount = "99";
        String currentTimeAddedAuctionCount = "9";
        String biddingRate = "99";
        String closedAuctionCount = "999";
        String progressingAuctionCount = "99";
        return MainStatisticResponseVo.builder()
                .totalAuctionCount(totalAuctionCount)
                .weeklyAddedAuctionCount(weeklyAddedAuctionCount)
                .dailyTotalAuctionCount(dailyTotalAuctionCount)
                .currentTimeAddedAuctionCount(currentTimeAddedAuctionCount)
                .biddingRate(biddingRate)
                .closedAuctionCount(closedAuctionCount)
                .progressingAuctionCount(progressingAuctionCount)
                .build();
    }

    @Override
    public List<MainHotAuctionResponseVo> mainHotAuction() {
        //Todo
        // 스프링 배치 도입하면 집계 테이블에서 해당 데이터를 받아와야 한다.
        List<MainHotAuctionResponseVo> mainHotAuctionResponseVos = new ArrayList<>();

        String[] titles = {"Java 강의합니다.", "포트폴리오 피드백 해드립니다.", "코테 문제 풀이", "운명 봐드립니다.",
                "영상 제작 경력 10년", "GPT보다 글 잘 씁니다.", "디자인 진짜 잘합니다.",
                "금쪽이 성격 개조 잘합니다.", "무회전 불꽃 슛 알려드립니다.", "wu shi zongguren"};
        String[] categories = {"IT·프로그래밍", "취업·입시", "IT·프로그래밍", "운세", "영상·사진·음향",
                "문서·글쓰기", "디자인", "심리상담", "투잡·노하우", "번역·통역"};
        int[] minimumBiddingPrices = {10000, 100000, 120000, 40000, 123000,
                60000, 3400, 23000, 90000, 12300, 530000};
        String[] handles = {"이서용의 코딩용 손가락", "조윤찬의 파워포인트", "한문철", "트페장인", "잇섭",
                "뤼튼", "유니온 그래픽스", "오은영", "호날도", "모택동"
        };
        String[] thumbnails = {"https://i.ibb.co/1mgWHH5/2024-05-26-024430.png",
                "https://i.ibb.co/Qkz2YnF/2024-05-26-024510.png",
                "https://i.ibb.co/CttbvVk/2024-05-26-024725.png",
                "https://i.ibb.co/zV02kNy/2024-05-26-024532.png",
                "https://i.ibb.co/fF3z16x/2024-05-26-024755.png",
                "https://i.ibb.co/JjqdbX2/2024-05-26-025312.png",
                "https://i.ibb.co/nRNkTVK/2024-05-26-025121.png",
                "https://i.ibb.co/Nmn1Xn1/2024-05-26-025050.png",
                "https://i.ibb.co/RTdJmYg/2024-05-26-024945.png",
                "https://i.ibb.co/4P7qhjG/2024-05-26-025357.png"};
        String[] contents = {"내용물내용물내용물내용물", "내용물내용물내용물내용물", "내용물내용물내용물내용물",
                "내용물내용물내용물내용물", "내용물내용물내용물내용물", "내용물내용물내용물내용물",
                "내용물내용물내용물내용물", "내용물내용물내용물내용물", "내용물내용물내용물내용물", "내용물내용물내용물내용물"};
        for (int i = 0; i < 10; i++) {
            mainHotAuctionResponseVos.add(MainHotAuctionResponseVo.builder()
                    .auctionUuid("auction_" + i)
                    .title(titles[i])
                    .category(categories[i])
                    .minimumBiddingPrice(minimumBiddingPrices[i])
                    .handle(handles[i])
                    .thumbnail(thumbnails[i])
                    .content(contents[i])
                    .createdAt(LocalDateTime.now())
                    .endedAt(LocalDateTime.now().plusDays(1))
                    .build());
        }
        return mainHotAuctionResponseVos;
    }

    @Override
    public List<MainCategoryHotAuctionResponseVo> mainCategoryHotAuction() {
        //Todo
        // 스프링 배치 도입하면 집계 테이블에서 해당 데이터를 받아와야 한다.
        List<MainCategoryHotAuctionResponseVo> mainCategoryHotAuctionResponseVos = new ArrayList<>();
        String[] titles = {"Java 강의합니다.", "포트폴리오 피드백 해드립니다.", "코테 문제 풀이", "백준 플레 입니다.",
                "백엔드 강의합니다.", "GPT보다 코딩 잘 합니다.", "프론트 진짜 잘합니다.",
                "디자인 패턴 알려드립니다.", "키보드 부숴드립니다.", "wu shi zongguren"};
        int[] minimumBiddingPrices = {10000, 100000, 120000, 40000, 123000,
                60000, 3400, 23000, 90000, 12300, 530000};
        String[] handles = {"이서용의 코딩용 손가락", "조윤찬의 파워포인트", "한문철", "트페장인", "잇섭",
                "뤼튼", "유니온 그래픽스", "오은영", "호날도", "모택동"
        };
        String[] thumbnails = {"https://i.ibb.co/1mgWHH5/2024-05-26-024430.png",
                "https://i.ibb.co/Qkz2YnF/2024-05-26-024510.png",
                "https://i.ibb.co/CttbvVk/2024-05-26-024725.png",
                "https://i.ibb.co/zV02kNy/2024-05-26-024532.png",
                "https://i.ibb.co/fF3z16x/2024-05-26-024755.png",
                "https://i.ibb.co/JjqdbX2/2024-05-26-025312.png",
                "https://i.ibb.co/nRNkTVK/2024-05-26-025121.png",
                "https://i.ibb.co/Nmn1Xn1/2024-05-26-025050.png",
                "https://i.ibb.co/RTdJmYg/2024-05-26-024945.png",
                "https://i.ibb.co/4P7qhjG/2024-05-26-025357.png"};
        String[] contents = {"내용물내용물내용물내용물", "내용물내용물내용물내용물", "내용물내용물내용물내용물",
                "내용물내용물내용물내용물", "내용물내용물내용물내용물", "내용물내용물내용물내용물",
                "내용물내용물내용물내용물", "내용물내용물내용물내용물", "내용물내용물내용물내용물", "내용물내용물내용물내용물"};
        for (int i = 0; i < 10; i++) {
            mainCategoryHotAuctionResponseVos.add(MainCategoryHotAuctionResponseVo.builder()
                    .auctionUuid("auction_" + i)
                    .title(titles[i])
                    .category("IT·프로그래밍")
                    .minimumBiddingPrice(minimumBiddingPrices[i])
                    .handle(handles[i])
                    .thumbnail(thumbnails[i])
                    .content(contents[i])
                    .createdAt(LocalDateTime.now())
                    .endedAt(LocalDateTime.now().plusDays(1))
                    .build());
        }
        return mainCategoryHotAuctionResponseVos;
    }

    @Override
    public List<MainHighBiddingPriceAuctionResponseVo> mainHighBiddingPriceAuction() {
        //Todo
        // 스프링 배치 도입하면 집계 테이블에서 해당 데이터를 받아와야 한다.
        List<MainHighBiddingPriceAuctionResponseVo> mainHighBiddingPriceAuctionResponseVos = new ArrayList<>();

        String[] titles = {"Java 강의합니다.", "포트폴리오 피드백 해드립니다.", "코테 문제 풀이", "운명 봐드립니다.",
                "영상 제작 경력 10년", "GPT보다 글 잘 씁니다.", "디자인 진짜 잘합니다.",
                "금쪽이 성격 개조 잘합니다.", "무회전 불꽃 슛 알려드립니다.", "wu shi zongguren"};
        String[] categories = {"IT·프로그래밍", "취업·입시", "IT·프로그래밍", "운세", "영상·사진·음향",
                "문서·글쓰기", "디자인", "심리상담", "투잡·노하우", "번역·통역"};
        int[] minimumBiddingPrices = {10000, 100000, 120000, 40000, 123000,
                60000, 3400, 23000, 90000, 12300, 530000};
        String[] handles = {"이서용의 코딩용 손가락", "조윤찬의 파워포인트", "한문철", "트페장인", "잇섭",
                "뤼튼", "유니온 그래픽스", "오은영", "호날도", "모택동"
        };
        String[] thumbnails = {"https://i.ibb.co/1mgWHH5/2024-05-26-024430.png",
                "https://i.ibb.co/Qkz2YnF/2024-05-26-024510.png",
                "https://i.ibb.co/CttbvVk/2024-05-26-024725.png",
                "https://i.ibb.co/zV02kNy/2024-05-26-024532.png",
                "https://i.ibb.co/fF3z16x/2024-05-26-024755.png",
                "https://i.ibb.co/JjqdbX2/2024-05-26-025312.png",
                "https://i.ibb.co/nRNkTVK/2024-05-26-025121.png",
                "https://i.ibb.co/Nmn1Xn1/2024-05-26-025050.png",
                "https://i.ibb.co/RTdJmYg/2024-05-26-024945.png",
                "https://i.ibb.co/4P7qhjG/2024-05-26-025357.png"};
        String[] contents = {"내용물내용물내용물내용물", "내용물내용물내용물내용물", "내용물내용물내용물내용물",
                "내용물내용물내용물내용물", "내용물내용물내용물내용물", "내용물내용물내용물내용물",
                "내용물내용물내용물내용물", "내용물내용물내용물내용물", "내용물내용물내용물내용물", "내용물내용물내용물내용물"};
        for (int i = 9; i >= 0; i--) {
            mainHighBiddingPriceAuctionResponseVos.add(MainHighBiddingPriceAuctionResponseVo.builder()
                    .auctionUuid("auction_" + i)
                    .title(titles[i])
                    .category(categories[i])
                    .minimumBiddingPrice(minimumBiddingPrices[i])
                    .handle(handles[i])
                    .thumbnail(thumbnails[i])
                    .content(contents[i])
                    .createdAt(LocalDateTime.now())
                    .endedAt(LocalDateTime.now().plusDays(1))
                    .build());
        }
        return mainHighBiddingPriceAuctionResponseVos;
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
