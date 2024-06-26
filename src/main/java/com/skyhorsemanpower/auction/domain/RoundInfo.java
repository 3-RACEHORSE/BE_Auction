package com.skyhorsemanpower.auction.domain;

import com.skyhorsemanpower.auction.status.RoundTimeEnum;
import com.skyhorsemanpower.auction.status.StandbyTimeEnum;
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
    private LocalDateTime auctionEndTime;
    private Boolean isLastRound;

    @Builder
    public RoundInfo(String auctionUuid, Integer round, LocalDateTime roundStartTime, LocalDateTime roundEndTime,
                     BigDecimal incrementUnit, BigDecimal price, Boolean isActive, Long numberOfParticipants,
                     Long leftNumberOfParticipants, LocalDateTime createdAt,
                     LocalDateTime auctionEndTime, Boolean isLastRound) {
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
        this.auctionEndTime = auctionEndTime;
        this.isLastRound = isLastRound;
    }

    public static RoundInfo nextRoundUpdate(RoundInfo roundInfo) {
        Integer nextRound = roundInfo.getRound() + 1;
        LocalDateTime nextRoundStartTime = LocalDateTime.now().plusSeconds(StandbyTimeEnum.SECONDS_15.getSecond());
        LocalDateTime nextRoundEndTime = nextRoundStartTime.plusSeconds(RoundTimeEnum.SECONDS_60.getSecond());
        BigDecimal nextPrice = roundInfo.getPrice().add(roundInfo.getIncrementUnit());
        LocalDateTime auctionEndTime = roundInfo.getAuctionEndTime();

        // nextRoundStartTime <= auctionEndTime <= nextRoundEndTime 인 경우 다음 라운드가 마지막 라운드
        boolean isLastRound = nextRoundStartTime.isBefore(auctionEndTime) && auctionEndTime.isBefore(nextRoundEndTime);

        return RoundInfo.builder()
                .auctionUuid(roundInfo.getAuctionUuid())
                .round(nextRound)
                .roundStartTime(nextRoundStartTime)
                .roundEndTime(nextRoundEndTime)
                .incrementUnit(roundInfo.getIncrementUnit())
                .price(nextPrice)
                .isActive(false)        // 대기 상태로 변경
                .numberOfParticipants(roundInfo.getNumberOfParticipants())
                .leftNumberOfParticipants(roundInfo.getNumberOfParticipants())
                .isLastRound(isLastRound)
                .build();
    }

    public static RoundInfo currentRoundUpdate(RoundInfo roundInfo) {
        Long nextNumberOfParticipants = roundInfo.getLeftNumberOfParticipants() - 1;

        return RoundInfo.builder()
                .auctionUuid(roundInfo.getAuctionUuid())
                .round(roundInfo.getRound())
                .roundStartTime(roundInfo.getRoundStartTime())
                .roundEndTime(roundInfo.getRoundEndTime())
                .incrementUnit(roundInfo.getIncrementUnit())
                .price(roundInfo.getPrice())
                .isActive(true)
                .numberOfParticipants(roundInfo.getNumberOfParticipants())
                .leftNumberOfParticipants(nextNumberOfParticipants)
                .build();
    }

    public static RoundInfo setIsActiveTrue(RoundInfo roundInfo) {
        return RoundInfo.builder()
                .auctionUuid(roundInfo.getAuctionUuid())
                .round(roundInfo.getRound())
                .roundStartTime(roundInfo.getRoundStartTime())
                .roundEndTime(roundInfo.getRoundEndTime())
                .incrementUnit(roundInfo.getIncrementUnit())
                .price(roundInfo.getPrice())
                .isActive(true)
                .numberOfParticipants(roundInfo.getNumberOfParticipants())
                .leftNumberOfParticipants(roundInfo.getLeftNumberOfParticipants())
                .createdAt(LocalDateTime.now())
                .build();
    }
}
