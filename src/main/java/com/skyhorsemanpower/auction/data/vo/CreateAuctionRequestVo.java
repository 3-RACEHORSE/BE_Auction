package com.skyhorsemanpower.auction.data.vo;

import java.util.List;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CreateAuctionRequestVo {
    private String title;
    private String content;
    private String category;
    private int minimumBiddingPrice;
    private String thumbnail;
    private List<String> images;
}
