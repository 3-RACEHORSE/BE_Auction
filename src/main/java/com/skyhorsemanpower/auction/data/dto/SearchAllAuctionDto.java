package com.skyhorsemanpower.auction.data.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SearchAllAuctionDto {
    private String keyword;
    private String category;

    @Builder
    public SearchAllAuctionDto(String keyword, String category) {
        this.keyword = keyword;
        this.category = category;
    }
}
