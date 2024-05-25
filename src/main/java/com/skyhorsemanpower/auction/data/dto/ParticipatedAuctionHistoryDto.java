package com.skyhorsemanpower.auction.data.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ParticipatedAuctionHistoryDto {
    private String sellerUuid;

    @Builder
    public ParticipatedAuctionHistoryDto(String sellerUuid) {
        this.sellerUuid = sellerUuid;
    }
}
