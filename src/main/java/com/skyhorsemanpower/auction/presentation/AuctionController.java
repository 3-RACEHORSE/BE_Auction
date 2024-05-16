package com.skyhorsemanpower.auction.presentation;

import com.skyhorsemanpower.auction.application.AuctionService;
import com.skyhorsemanpower.auction.common.SuccessResponse;
import com.skyhorsemanpower.auction.data.dto.CreateAuctionDto;
import com.skyhorsemanpower.auction.data.dto.SearchAllAuctionDto;
import com.skyhorsemanpower.auction.data.dto.SearchAuctionDto;
import com.skyhorsemanpower.auction.data.vo.CreateAuctionRequestVo;
import com.skyhorsemanpower.auction.data.vo.SearchAllAuctionResponseVo;
import com.skyhorsemanpower.auction.data.vo.SearchAuctionResponseVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "경매", description = "경매 서비스 API")
@RequestMapping("api/v1/auction")
public class AuctionController {

    private final AuctionService auctionService;

    // 경매 등록
    @PostMapping("")
    @Operation(summary = "경매 등록", description = "경매 등록")
    public SuccessResponse<Object> createAuction (
            @RequestHeader String uuid,
            @RequestBody CreateAuctionRequestVo createAuctionRequestVo) {
        auctionService.createAuction(CreateAuctionDto.createAuctionVoToDto(uuid, createAuctionRequestVo));
        return new SuccessResponse<>(null);
    }

    // 키워드를 통한 경매 리스트 조회
    @GetMapping("/search")
    @Operation(summary = "경매 리스트 조회", description = "경매 리스트 조회")
    public SuccessResponse<List<SearchAllAuctionResponseVo>> searchAllAuction (
            @RequestParam(required = false) String keyword) {
        List<SearchAllAuctionResponseVo> searchAllAuctionResponseVos = auctionService.searchAllAuctionResponseVo(SearchAllAuctionDto.builder().keyword(keyword).build());
        return new SuccessResponse<>(searchAllAuctionResponseVos);
    }

    // auction_uuid를 통한 경매 상세 조회
    @GetMapping("/{auctionUuid}")
    @Operation(summary = "경매 상세 조회", description = "경매 상세 조회")
    public SuccessResponse<SearchAuctionResponseVo> searchAuction (@PathVariable("auctionUuid") String auctionUuid) {
        SearchAuctionResponseVo searchAuctionResponseVo = auctionService.searchAuction(SearchAuctionDto.builder().auctionUuid(auctionUuid).build());
        return new SuccessResponse<>(searchAuctionResponseVo);
    }

}