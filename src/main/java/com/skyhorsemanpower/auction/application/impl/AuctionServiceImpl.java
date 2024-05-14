package com.skyhorsemanpower.auction.application.impl;

import com.skyhorsemanpower.auction.application.AuctionService;
import com.skyhorsemanpower.auction.common.CustomException;
import com.skyhorsemanpower.auction.common.ResponseStatus;
import com.skyhorsemanpower.auction.data.dto.CreateAuctionDto;
import com.skyhorsemanpower.auction.data.dto.SearchAllAuctionDto;
import com.skyhorsemanpower.auction.data.dto.SearchAuctionDto;
import com.skyhorsemanpower.auction.data.vo.SearchAllAuctionResponseVo;
import com.skyhorsemanpower.auction.data.vo.SearchAuctionResponseVo;
import com.skyhorsemanpower.auction.domain.AuctionImages;
import com.skyhorsemanpower.auction.domain.cqrs.command.CommandOnlyAuction;
import com.skyhorsemanpower.auction.domain.cqrs.read.ReadOnlyAuction;
import com.skyhorsemanpower.auction.repository.AuctionImagesRepository;
import com.skyhorsemanpower.auction.repository.cqrs.command.CommandOnlyAuctionRepository;
import com.skyhorsemanpower.auction.repository.cqrs.read.ReadOnlyAuctionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuctionServiceImpl implements AuctionService {

    private final CommandOnlyAuctionRepository commandOnlyAuctionRepository;
    private final ReadOnlyAuctionRepository readOnlyAuctionRepository;
    private final AuctionImagesRepository auctionImagesRepository;
    //    private final AuctionHistoryRepository auctionHistoryRepository;
    private final MongoTemplate mongoTemplate;

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
    }

    @Override
    public List<SearchAllAuctionResponseVo> searchAllAuctionResponseVo(SearchAllAuctionDto searchAuctionDto) {
        List<SearchAllAuctionResponseVo> searchAllAuctionResponseVos = new ArrayList<>();
        SearchAllAuctionResponseVo searchAllAuctionResponseVo;
        List<ReadOnlyAuction> readOnlyAuctions;

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
            searchAllAuctionResponseVo = SearchAllAuctionResponseVo.readOnlyAuctionToSearchAllAuctionResponseVo(readOnlyAuction);
            searchAllAuctionResponseVos.add(searchAllAuctionResponseVo);
        }
        return searchAllAuctionResponseVos;
    }


    @Override
    public SearchAuctionResponseVo searchAuction(SearchAuctionDto searchAuctionDto) {
        return SearchAuctionResponseVo.builder()
                .readOnlyAuction(readOnlyAuctionRepository.findByAuctionUuid(searchAuctionDto.getAuctionUuid()).orElseThrow(
                        () -> new CustomException(ResponseStatus.MONGODB_NOT_FOUND)
                ))
                .build();
    }


    // MongoDB 경매글 저장
    private void createReadOnlyAuction(CreateAuctionDto createAuctionDto) {
        ReadOnlyAuction readOnlyAuction = ReadOnlyAuction.builder()
                .auctionUuid(createAuctionDto.getAuctionUuid())
                .uuid(createAuctionDto.getUuid())
                .handle(createAuctionDto.getHandle())
                .title(createAuctionDto.getTitle())
                .content(createAuctionDto.getContent())
                .minimumBiddingPrice(createAuctionDto.getMinimumBiddingPrice())
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
                .uuid(createAuctionDto.getUuid())
                .handle(createAuctionDto.getHandle())
                .title(createAuctionDto.getTitle())
                .content(createAuctionDto.getContent())
                .minimumBiddingPrice(createAuctionDto.getMinimumBiddingPrice())
                .build();
        try {
            commandOnlyAuctionRepository.save(commandOnlyAuction);
        } catch (Exception e) {
            throw new CustomException(ResponseStatus.POSTGRESQL_ERROR);
        }
    }

}
