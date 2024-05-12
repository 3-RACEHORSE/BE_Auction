package com.skyhorsemanpower.auction.application.impl;

import com.skyhorsemanpower.auction.application.AuctionService;
import com.skyhorsemanpower.auction.data.dto.CreateAuctionDto;
import com.skyhorsemanpower.auction.data.dto.SearchAuctionDto;
import com.skyhorsemanpower.auction.data.vo.SearchAllAuctionResponseVo;
import com.skyhorsemanpower.auction.domain.command.CommandOnlyAuction;
import com.skyhorsemanpower.auction.domain.read.ReadOnlyAuction;
import com.skyhorsemanpower.auction.repository.command.CommandOnlyAuctionRepository;
import com.skyhorsemanpower.auction.repository.read.ReadOnlyAuctionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuctionServiceImpl implements AuctionService {

    private final CommandOnlyAuctionRepository commandOnlyAuctionRepository;
    private final ReadOnlyAuctionRepository readOnlyAuctionRepository;
//    private final AuctionHistoryRepository auctionHistoryRepository;

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

        // MongoDB 저장
        createReadOnlyAuction(createAuctionDto);
    }

    @Override
    public List<SearchAllAuctionResponseVo> searchAllAuctionResponseVo(SearchAuctionDto searchAuctionDto) {
        List<SearchAllAuctionResponseVo> searchAllAuctionResponseVos = new ArrayList<>();
        SearchAllAuctionResponseVo searchAllAuctionResponseVo;
        List<ReadOnlyAuction> readOnlyAuctions;

        // keyword 없으면 전체 검색
        if (searchAuctionDto.getKeyword() == null) readOnlyAuctions = readOnlyAuctionRepository.findAll();
        // keyword 검색
        else readOnlyAuctions = readOnlyAuctionRepository.findAllByTitle(searchAuctionDto.getKeyword());

        for (ReadOnlyAuction readOnlyAuction : readOnlyAuctions) {
            searchAllAuctionResponseVo = SearchAllAuctionResponseVo.readOnlyAuctionToSearchAllAuctionResponseVo(readOnlyAuction);
            searchAllAuctionResponseVos.add(searchAllAuctionResponseVo);
        }
        return searchAllAuctionResponseVos;
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
        readOnlyAuctionRepository.save(readOnlyAuction);
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
        commandOnlyAuctionRepository.save(commandOnlyAuction);
    }


}
