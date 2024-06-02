package com.skyhorsemanpower.auction.data.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ParticipatedAuctionHistoryDto {
    private String participateUuid;

    @Builder
    public ParticipatedAuctionHistoryDto(String participateUuid) {
        this.participateUuid = participateUuid;
    }
}
