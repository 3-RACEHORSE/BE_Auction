package com.skyhorsemanpower.auction.data.vo;

import java.util.List;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CreateAuctionRequestVo {
    private String title;
    private String content;
    private long majorCategoryId;
    private long minorCategoryId;
    private int minimumBiddingPrice;
    private String thumbnail;
    private List<String> images;
//    private List<>
}
