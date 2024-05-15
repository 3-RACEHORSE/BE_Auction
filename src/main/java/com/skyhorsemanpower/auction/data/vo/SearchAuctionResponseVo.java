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

    //Todo AuctionHistory 구현하면 수정 필요
    private int bidPrice;


    @Builder
    public SearchAuctionResponseVo(ReadOnlyAuction readOnlyAuction, String thumbnail, List<String> images, int bidPrice) {
        this.readOnlyAuction = readOnlyAuction;
        this.thumbnail = thumbnail;
        this.images = images;
        this.bidPrice = bidPrice;
    }

}
