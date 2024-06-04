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
    private boolean isSubscribed;

    @Builder
    public SearchAuctionResponseVo(ReadOnlyAuction readOnlyAuction, String handle, String thumbnail, List<String> images, boolean isSubscribed) {
        this.readOnlyAuction = readOnlyAuction;
        this.handle = handle;
        this.thumbnail = thumbnail;
        this.images = images;
        this.isSubscribed = isSubscribed;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }
}
