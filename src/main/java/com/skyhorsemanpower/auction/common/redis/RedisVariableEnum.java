package com.skyhorsemanpower.auction.common.redis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RedisVariableEnum {
    ROUND("round"),
    CURRENT_ROUND_START_TIME("currentRoundStartTime"),
    CURRENT_ROUND_END_TIME("currentRoundEndTime"),
    CURRENT_PRICE("currentPrice"),
    NUMBER_OF_EVENT_PARTICIPANTS("numberOfEventParticipants");

    private final String variable;
}
