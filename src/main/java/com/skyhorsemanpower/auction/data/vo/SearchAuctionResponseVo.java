package com.skyhorsemanpower.auction.data.vo;

import com.skyhorsemanpower.auction.domain.cqrs.read.ReadOnlyAuction;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor
public class SearchAuctionResponseVo {
    private ReadOnlyAuction readOnlyAuction;

    private String handle;
    private String thumbnail;
    private List<String> images;

    @Builder
    public SearchAuctionResponseVo(ReadOnlyAuction readOnlyAuction, String handle, String thumbnail, List<String> images) {
        this.readOnlyAuction = readOnlyAuction;
        this.handle = handle;
        this.thumbnail = thumbnail;
        this.images = images;
    }

}
