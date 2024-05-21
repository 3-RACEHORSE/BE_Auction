package com.skyhorsemanpower.auction.data.dto;

import com.skyhorsemanpower.auction.data.vo.OfferBiddingPriceRequestVo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class OfferBiddingPriceDto {
    private String auctionUuid;
    private String biddingUuid;
    private int biddingPrice;

    @Builder
    public OfferBiddingPriceDto(String auctionUuid, String biddingUuid, int biddingPrice) {
        this.auctionUuid = auctionUuid;
        this.biddingUuid = biddingUuid;
        this.biddingPrice = biddingPrice;
    }

    public static OfferBiddingPriceDto voToDto(OfferBiddingPriceRequestVo offerBiddingPriceRequestVo, String uuid) {
        return OfferBiddingPriceDto.builder()
                .auctionUuid(offerBiddingPriceRequestVo.getAuctionUuid())
                .biddingUuid(uuid)
                .biddingPrice(offerBiddingPriceRequestVo.getBiddingPrice())
                .build();
    }
}
