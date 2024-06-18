package com.skyhorsemanpower.auction.presentation;

import com.skyhorsemanpower.auction.application.AuctionService;
import com.skyhorsemanpower.auction.application.RedisService;
import com.skyhorsemanpower.auction.common.SuccessResponse;
import com.skyhorsemanpower.auction.data.dto.OfferBiddingPriceDto;
import com.skyhorsemanpower.auction.data.vo.OfferBiddingPriceRequestVo;
import com.skyhorsemanpower.auction.data.vo.RoundInfoResponseVo;
import com.skyhorsemanpower.auction.data.vo.StandbyResponseVo;
import com.skyhorsemanpower.auction.repository.RoundInfoReactiveRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "경매 서비스", description = "경매 서비스 API")
@RequestMapping("/api/v1/auction")
@CrossOrigin(value = "*")
public class AuctionController {
    private final AuctionService auctionService;
    private final RedisService redisService;
    private final RoundInfoReactiveRepository roundInfoReactiveRepository;

    // 경매 입찰가 제시
    @PostMapping("/bidding")
    @Operation(summary = "경매 입찰가 제시", description = "경매 입찰가 제시")
    public SuccessResponse<Object> offerBiddingPrice(
            @RequestHeader String uuid,
            @RequestBody OfferBiddingPriceRequestVo offerBiddingPriceRequestVo) {
        auctionService.offerBiddingPrice(OfferBiddingPriceDto.voToDto(offerBiddingPriceRequestVo, uuid));
        return new SuccessResponse<>(null);
    }

    // 경매 페이지 API
    @GetMapping(value = "/auction-page/{auctionUuid}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "경매 페이지 API", description = "경매 페이지에 보여줄 데이터 실시간 조회")
    public Flux<RoundInfoResponseVo> auctionPage(
            @PathVariable("auctionUuid") String auctionUuid) {
        return roundInfoReactiveRepository.searchRoundInfo(auctionUuid).subscribeOn(Schedulers.boundedElastic());
    }

    // 대기 페이지 API
    @PutMapping("/standby-page/{auctionUuid}")
    @Operation(summary = "대기 페이지 API", description = "대기 페이지에 보여줄 데이터 조회 및 갱신")
    public SuccessResponse<StandbyResponseVo> standByPage(
            @PathVariable("auctionUuid") String auctionUuid) {
        return new SuccessResponse<>(redisService.getStandbyPage(auctionUuid));
    }
}
