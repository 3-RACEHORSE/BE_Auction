package com.skyhorsemanpower.auction.application.impl;

import com.skyhorsemanpower.auction.application.AuctionService;
import com.skyhorsemanpower.auction.data.dto.CreateAuctionDto;
import com.skyhorsemanpower.auction.domain.command.CommandOnlyAuction;
import com.skyhorsemanpower.auction.repository.command.CommandOnlyAuctionRepository;
//import com.skyhorsemanpower.auction.repository.read.AuctionHistoryRepository;
//import com.skyhorsemanpower.auction.repository.read.ReadOnlyAuctionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuctionServiceImpl implements AuctionService {

    private final CommandOnlyAuctionRepository commandOnlyAuctionRepository;
//    private final ReadOnlyAuctionRepository readOnlyAuctionRepository;
//    private final AuctionHistoryRepository auctionHistoryRepository;

    @Override
    public void createAuction(CreateAuctionDto createAuctionDto) {
        // PostgreSQL 저장

        // uuid로 handle 요청
        //todo 회원 서비스에서 uuid로 handle 요청
        String handle = "handle";

        UUID auctionUuid = UUID.randomUUID();
        String auctionUuidToString = LocalDateTime.now() + auctionUuid.toString();
        CommandOnlyAuction commandOnlyAuction = CommandOnlyAuction.builder()
                .auctionUuid(auctionUuidToString)
                .uuid(createAuctionDto.getUuid())
                .handle(handle)
                .title(createAuctionDto.getTitle())
                .content(createAuctionDto.getContent())
                .minimumBiddingPrice(createAuctionDto.getMinimumBiddingPrice())
                .build();
        commandOnlyAuctionRepository.save(commandOnlyAuction);
    }
}
