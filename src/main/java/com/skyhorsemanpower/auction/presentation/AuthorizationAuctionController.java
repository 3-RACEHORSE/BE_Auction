package com.skyhorsemanpower.auction.presentation;

import com.skyhorsemanpower.auction.application.AuctionService;
import com.skyhorsemanpower.auction.common.SuccessResponse;
import com.skyhorsemanpower.auction.data.dto.CreateAuctionDto;
import com.skyhorsemanpower.auction.data.dto.CreatedAuctionHistoryDto;
import com.skyhorsemanpower.auction.data.dto.OfferBiddingPriceDto;
import com.skyhorsemanpower.auction.data.dto.ParticipatedAuctionHistoryDto;
import com.skyhorsemanpower.auction.data.vo.CreateAuctionRequestVo;
import com.skyhorsemanpower.auction.data.vo.CreatedAuctionHistoryResponseVo;
import com.skyhorsemanpower.auction.data.vo.OfferBiddingPriceRequestVo;
import com.skyhorsemanpower.auction.data.vo.ParticipatedAuctionHistoryResponseVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    // 자신이 참여한 경매 이력 조회
    @GetMapping("/participate-history")
    @Operation(summary = "참여한 경매 이력 조회", description = "참여한 경매 이력 조회")
    public SuccessResponse<List<ParticipatedAuctionHistoryResponseVo>> participatedAuctionHistory(
            @RequestHeader String uuid) {
        List<ParticipatedAuctionHistoryResponseVo> participatedAuctionHistoryResponseVos = auctionService.
                participatedAuctionHistory(ParticipatedAuctionHistoryDto.builder().participateUuid(uuid).build());
        return new SuccessResponse<>(participatedAuctionHistoryResponseVos);
    }
}
