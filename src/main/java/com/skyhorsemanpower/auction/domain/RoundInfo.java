package com.skyhorsemanpower.auction.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@ToString
@Document(collection = "round_info")
public class RoundInfo {
    @Id
    private String roundInfoId;

    private String auctionUuid;
    private Integer round;
    private LocalDateTime roundStartTime;
    private LocalDateTime roundEndTime;
    private BigDecimal incrementUnit;
    private BigDecimal price;
    private Boolean isActive;
    private Long numberOfParticipants;
    private Long leftNumberOfParticipants;
    private LocalDateTime createdAt;

    @Builder
    public RoundInfo(String auctionUuid, Integer round, LocalDateTime roundStartTime, LocalDateTime roundEndTime,
                     BigDecimal incrementUnit, BigDecimal price, Boolean isActive, Long numberOfParticipants,
                     Long leftNumberOfParticipants, LocalDateTime createdAt) {
        this.auctionUuid = auctionUuid;
        this.round = round;
        this.roundStartTime = roundStartTime;
        this.roundEndTime = roundEndTime;
        this.incrementUnit = incrementUnit;
        this.price = price;
        this.isActive = isActive;
        this.numberOfParticipants = numberOfParticipants;
        this.leftNumberOfParticipants = leftNumberOfParticipants;
        this.createdAt = LocalDateTime.now();
    }
}
