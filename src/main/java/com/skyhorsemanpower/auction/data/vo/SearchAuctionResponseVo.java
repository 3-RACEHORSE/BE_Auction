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

    //Todo images, category 엔티티 추가해서 아래 수정할 것
    private String thumbnail;
    private List<String> images;
    private String categoryMajorName;
    private String categoryMinorName;

    //Todo AuctionHistory 구현하면 아래도 바껴야 한다.
    private int bidPrice;


    @Builder
    public SearchAuctionResponseVo(ReadOnlyAuction readOnlyAuction, String thumbnail, List<String> images, String categoryMajorName, String categoryMinorName, int bidPrice) {
        this.readOnlyAuction = readOnlyAuction;
        this.thumbnail = thumbnail;
        this.images = images;
        this.categoryMajorName = categoryMajorName;
        this.categoryMinorName = categoryMinorName;
        this.bidPrice = bidPrice;
    }

}
