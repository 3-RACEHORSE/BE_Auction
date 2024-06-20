package com.skyhorsemanpower.auction.kafka.dto;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.skyhorsemanpower.auction.status.AuctionStateEnum;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@JsonSerialize
@JsonDeserialize
@NoArgsConstructor
@ToString
public class SuccessfulBidDto {
    private String auctionUuid;
    private List<String> memberUuids;
    private BigDecimal price;
    private AuctionStateEnum auctionState;

    @Builder
    public SuccessfulBidDto(String auctionUuid, List<String> memberUuids,
                            BigDecimal price, AuctionStateEnum auctionState) {
        this.auctionUuid = auctionUuid;
        this.memberUuids = memberUuids;
        this.price = price;
        this.auctionState = auctionState;
    }
}
