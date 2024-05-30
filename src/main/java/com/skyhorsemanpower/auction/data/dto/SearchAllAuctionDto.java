package com.skyhorsemanpower.auction.data.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SearchAllAuctionDto {
    private String keyword;
    private String category;
    private Integer page;
    private Integer size;
    private String uuid;
    private String token;
}
