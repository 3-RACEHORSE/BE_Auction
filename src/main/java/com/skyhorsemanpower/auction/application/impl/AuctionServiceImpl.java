package com.skyhorsemanpower.auction.application.impl;

import com.skyhorsemanpower.auction.application.AuctionService;
import com.skyhorsemanpower.auction.common.CustomException;
import com.skyhorsemanpower.auction.common.ResponseStatus;
import com.skyhorsemanpower.auction.data.dto.*;
import com.skyhorsemanpower.auction.data.vo.SearchAllAuctionResponseVo;
import com.skyhorsemanpower.auction.data.vo.SearchAuctionResponseVo;
import com.skyhorsemanpower.auction.domain.AuctionHistory;
import com.skyhorsemanpower.auction.domain.AuctionImages;
import com.skyhorsemanpower.auction.domain.cqrs.command.CommandOnlyAuction;
import com.skyhorsemanpower.auction.domain.cqrs.read.ReadOnlyAuction;
import com.skyhorsemanpower.auction.repository.AuctionHistoryRepository;
import com.skyhorsemanpower.auction.repository.AuctionImagesRepository;
import com.skyhorsemanpower.auction.repository.cqrs.command.CommandOnlyAuctionRepository;
import com.skyhorsemanpower.auction.repository.cqrs.read.ReadOnlyAuctionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuctionServiceImpl implements AuctionService {

    private final CommandOnlyAuctionRepository commandOnlyAuctionRepository;
    private final ReadOnlyAuctionRepository readOnlyAuctionRepository;
    private final AuctionImagesRepository auctionImagesRepository;
    private final AuctionHistoryRepository auctionHistoryRepository;
    private final MongoTemplate mongoTemplate;
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    @Override
    @Transactional
    public void createAuction(CreateAuctionDto createAuctionDto) {
        // uuid로 handle 요청
        //todo 회원 서비스에서 uuid로 handle 요청
        String handle = "handle";
        createAuctionDto.setHandle(handle);
        UUID auctionUuid = UUID.randomUUID();
        String auctionUuidToString = LocalDate.now() + auctionUuid.toString();
        createAuctionDto.setAuctionUuid(auctionUuidToString);

        // PostgreSQL 저장
        createCommandOnlyAution(createAuctionDto);
        createAuctionImages(createAuctionDto);

        // MongoDB 저장
        createReadOnlyAuction(createAuctionDto);
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
        List<ReadOnlyAuction> readOnlyAuctions;
        String thumbnail;

        // keyword 없으면 전체 검색
        if (searchAuctionDto.getKeyword() == null) {
            Criteria criteria = new Criteria().andOperator(
                    Criteria.where("createdAt").lte(LocalDateTime.now()),
                    Criteria.where("endedAt").gte(LocalDateTime.now())
            );

            Query query = new Query(criteria);

            try {
                readOnlyAuctions = mongoTemplate.find(query, ReadOnlyAuction.class);
            } catch (Exception e) {
                throw new CustomException(ResponseStatus.MONGODB_ERROR);
            }
        }

        // keyword 검색
        else {
            try {
                readOnlyAuctions = readOnlyAuctionRepository.findAllByTitleLike(searchAuctionDto.getKeyword());
            } catch (Exception e) {
                throw new CustomException(ResponseStatus.MONGODB_ERROR);
            }
        }

        for (ReadOnlyAuction readOnlyAuction : readOnlyAuctions) {
            // 각 경매의 auctionUuid를 통해 thumbnail 가져오기
            // thumbnail은 null이 가능하다.
            thumbnail = auctionImagesRepository.getThumbnailUrl(readOnlyAuction.getAuctionUuid());

            searchAllAuctionResponseVo = SearchAllAuctionResponseVo.readOnlyAuctionToSearchAllAuctionResponseVo(readOnlyAuction, thumbnail);
            searchAllAuctionResponseVos.add(searchAllAuctionResponseVo);
        }
        return searchAllAuctionResponseVos;
    }

    @Override
    public SearchAuctionResponseVo searchAuction(SearchAuctionDto searchAuctionDto) {
        ReadOnlyAuction auction = readOnlyAuctionRepository.findByAuctionUuid(searchAuctionDto.getAuctionUuid()).orElseThrow(
                () -> new CustomException(ResponseStatus.MONGODB_NOT_FOUND)
        );
        return SearchAuctionResponseVo.builder()
                .readOnlyAuction(auction)
                .thumbnail(auctionImagesRepository.getThumbnailUrl(searchAuctionDto.getAuctionUuid()))
                .images(auctionImagesRepository.getImagesUrl(searchAuctionDto.getAuctionUuid()))
                .build();
    }

    @Override
    public void offerBiddingPrice(OfferBiddingPriceDto offerBiddingPriceDto) {
        AuctionHistory auctionHistory = AuctionHistory.builder()
                .auctionUuid(offerBiddingPriceDto.getAuctionUuid())
                .biddingUuid(offerBiddingPriceDto.getBiddingUuid())
                .biddingPrice(offerBiddingPriceDto.getBiddingPrice())
                .biddingTime(LocalDateTime.now())
                .build();
        try {
            auctionHistoryRepository.save(auctionHistory).subscribe();
        } catch (Exception e) {
            throw new CustomException(ResponseStatus.MONGODB_ERROR);
        }
    }

    @Override
    public Flux<AuctionHistory> searchBiddingPrice(SearchBiddingPriceDto searchBiddingPriceDto) {

        return auctionHistoryRepository.searchBiddingPrice(searchBiddingPriceDto.getAuctionUuid());
    }

    // MongoDB 경매글 저장
    private void createReadOnlyAuction(CreateAuctionDto createAuctionDto) {
        ReadOnlyAuction readOnlyAuction = ReadOnlyAuction.builder()
                .auctionUuid(createAuctionDto.getAuctionUuid())
                .sellerUuid(createAuctionDto.getSellerUuid())
                .handle(createAuctionDto.getHandle())
                .title(createAuctionDto.getTitle())
                .content(createAuctionDto.getContent())
                .category(createAuctionDto.getCategory())
                .minimumBiddingPrice(createAuctionDto.getMinimumBiddingPrice())
                .createdAt(LocalDateTime.now())
                .endedAt(LocalDateTime.now().plusDays(1))
                .bidderUuid("추가 필요")
                .bidPrice(-1)
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
                .handle(createAuctionDto.getHandle())
                .title(createAuctionDto.getTitle())
                .content(createAuctionDto.getContent())
                .category(createAuctionDto.getCategory())
                .minimumBiddingPrice(createAuctionDto.getMinimumBiddingPrice())
                .bidderUuid("추가 필요")
                .bidPrice(-1)
                .build();

        try {
            commandOnlyAuctionRepository.save(commandOnlyAuction);
        } catch (Exception e) {
            throw new CustomException(ResponseStatus.POSTGRESQL_ERROR);
        }
    }

}
