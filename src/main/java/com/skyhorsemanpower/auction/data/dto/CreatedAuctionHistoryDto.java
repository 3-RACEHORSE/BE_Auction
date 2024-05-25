package com.skyhorsemanpower.auction.data.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreatedAuctionHistoryDto {
    private String sellerUuid;

    @Builder
    public CreatedAuctionHistoryDto(String sellerUuid) {
        this.sellerUuid = sellerUuid;
    }
}
