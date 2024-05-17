package com.skyhorsemanpower.auction.data.vo;

import com.skyhorsemanpower.auction.domain.cqrs.read.ReadOnlyAuction;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class SearchAuctionResponseVo {
    private ReadOnlyAuction readOnlyAuction;

    private String thumbnail;
    private List<String> images;

    @Builder
    public SearchAuctionResponseVo(ReadOnlyAuction readOnlyAuction, String thumbnail, List<String> images) {
        this.readOnlyAuction = readOnlyAuction;
        this.thumbnail = thumbnail;
        this.images = images;
    }

}
