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
    private Integer page;
    private Integer size;

    @Builder
    public SearchAllAuctionDto(String keyword, String category, Integer page, Integer size) {
        this.keyword = keyword;
        this.category = category;
        this.page = page;
        this.size = size;
    }
}
