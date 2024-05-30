package com.skyhorsemanpower.auction.data.vo;

import com.skyhorsemanpower.auction.data.dto.AuctionAndIsSubscribedDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor
public class SearchAllAuctionResponseVo {
    private List<AuctionAndIsSubscribedDto> auctionAndIsSubscribedDtos;
    private int currentPage;
    private boolean hasNext;

    @Builder
    public SearchAllAuctionResponseVo(List<AuctionAndIsSubscribedDto> auctionAndIsSubscribedDtos, int currentPage, boolean hasNext) {
        this.auctionAndIsSubscribedDtos = auctionAndIsSubscribedDtos;
        this.currentPage = currentPage;
        this.hasNext = hasNext;
    }
}

