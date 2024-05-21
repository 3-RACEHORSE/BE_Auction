package com.skyhorsemanpower.auction.data.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InquiryAuctionHistoryDto {
    private String sellerUuid;

    @Builder
    public InquiryAuctionHistoryDto(String sellerUuid) {
        this.sellerUuid = sellerUuid;
    }
}
