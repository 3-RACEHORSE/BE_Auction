package com.skyhorsemanpower.auction.presentation;

import com.skyhorsemanpower.auction.application.AuctionService;
import com.skyhorsemanpower.auction.common.SuccessResponse;
import com.skyhorsemanpower.auction.data.dto.CreateAuctionDto;
import com.skyhorsemanpower.auction.data.dto.InquiryAuctionHistoryDto;
import com.skyhorsemanpower.auction.data.dto.OfferBiddingPriceDto;
import com.skyhorsemanpower.auction.data.vo.CreateAuctionRequestVo;
import com.skyhorsemanpower.auction.data.vo.InquiryAuctionHistoryResponseVo;
import com.skyhorsemanpower.auction.data.vo.OfferBiddingPriceRequestVo;
import com.skyhorsemanpower.auction.domain.cqrs.read.ReadOnlyAuction;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "인가가 필요한 경매 서비스", description = "인가가 필요한 경매 서비스 API")
@RequestMapping("/api/v1/authorization/auction")
public class AuthorizationAuctionController {
    private final AuctionService auctionService;

    // 경매 등록
    @PostMapping("")
    @Operation(summary = "경매 등록", description = "경매 등록")
    public SuccessResponse<Object> createAuction(
            @RequestHeader String uuid,
            @RequestBody CreateAuctionRequestVo createAuctionRequestVo) {
        auctionService.createAuction(CreateAuctionDto.createAuctionVoToDto(uuid, createAuctionRequestVo));
        return new SuccessResponse<>(null);
    }

    // 경매 입찰가 제시
    @PostMapping("/bidding")
    @Operation(summary = "경매 입찰가 제시", description = "경매 입찰가 제시")
    public SuccessResponse<Object> offerBiddingPrice(
            @RequestHeader String uuid,
            @RequestBody OfferBiddingPriceRequestVo offerBiddingPriceRequestVo) {
        auctionService.offerBiddingPrice(OfferBiddingPriceDto.voToDto(offerBiddingPriceRequestVo, uuid));
        return new SuccessResponse<>(null);
    }

    // 경매글 이력 조회
    @GetMapping("/history")
    @Operation(summary = "경매글 이력 조회", description = "경매글 이력 조회")
    public SuccessResponse<List<ReadOnlyAuction>> inquiryAuctionHistory(
            @RequestHeader String uuid) {
        List<ReadOnlyAuction> readOnlyAuctions = auctionService.inquiryAuctionHistory(InquiryAuctionHistoryDto.builder().sellerUuid(uuid).build());
        return new SuccessResponse<>(readOnlyAuctions);
    }

}
