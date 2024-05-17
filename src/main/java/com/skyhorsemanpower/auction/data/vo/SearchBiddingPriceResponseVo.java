package com.skyhorsemanpower.auction.data.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class SearchBiddingPriceResponseVo {
    private List<Integer> biddingPriceList;
}
