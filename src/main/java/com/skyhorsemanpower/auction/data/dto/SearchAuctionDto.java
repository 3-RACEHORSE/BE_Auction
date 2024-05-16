package com.skyhorsemanpower.auction.data.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SearchAuctionDto {
    private String auctionUuid;

    @Builder
    public SearchAuctionDto(String auctionUuid) {
        this.auctionUuid = auctionUuid;
    }
}