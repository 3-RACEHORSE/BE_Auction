package com.skyhorsemanpower.auction.data.vo;

import com.skyhorsemanpower.auction.domain.cqrs.read.ReadOnlyAuction;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor
public class SearchAllAuctionResponseVo {
    private List<SearchAllAuction> searchAllAuctions;
    private int currentPage;
    private boolean hasNext;

    @Builder
    public SearchAllAuctionResponseVo(List<SearchAllAuction> searchAllAuctions, int currentPage, boolean hasNext) {
        this.searchAllAuctions = searchAllAuctions;
        this.currentPage = currentPage;
        this.hasNext = hasNext;
    }
}

