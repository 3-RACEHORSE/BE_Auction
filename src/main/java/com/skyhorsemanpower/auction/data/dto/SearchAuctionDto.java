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
    private String token;
    private String uuid;

    @Builder
    public SearchAuctionDto(String auctionUuid, String token, String uuid) {
        this.auctionUuid = auctionUuid;
        this.token = token;
        this.uuid = uuid;
    }
}
