package com.skyhorsemanpower.auction.presentation;

import com.skyhorsemanpower.auction.application.AuctionService;
import com.skyhorsemanpower.auction.common.SuccessResponse;
import com.skyhorsemanpower.auction.data.dto.CreateAuctionDto;
import com.skyhorsemanpower.auction.data.vo.CreateAuctionRequestVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auction")
public class AuctionController {

    private final AuctionService auctionService;

    @PostMapping("")
    public SuccessResponse<?> createAuction(
            @RequestHeader String uuid,
            @RequestBody CreateAuctionRequestVo createAuctionRequestVo) {
        auctionService.createAuction(CreateAuctionDto.createAuctionVoToDto(uuid, createAuctionRequestVo));
        return new SuccessResponse<>(null);
    }
}
