package com.skyhorsemanpower.auction.data.vo;

import com.skyhorsemanpower.auction.common.exception.CustomException;
import com.skyhorsemanpower.auction.common.exception.ResponseStatus;
import com.skyhorsemanpower.auction.common.redis.RedisVariableEnum;
import com.skyhorsemanpower.auction.status.StandbyTimeEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Getter
@NoArgsConstructor
@Slf4j
public class StandbyResponseVo {
    private BigDecimal nextPrice;
    private LocalDateTime standbyStartTime;
    private LocalDateTime standbyEndTime;

    @Builder
    public StandbyResponseVo(BigDecimal nextPrice, LocalDateTime standbyStartTime,
                             LocalDateTime standbyEndTime) {
        this.nextPrice = nextPrice;
        this.standbyStartTime = standbyStartTime;
        this.standbyEndTime = standbyEndTime;
    }

    // 경매 참고 테이블이 다음 라운드로 갱신된 상태이다.
    // 필요한 데이터인 nextPrice는 그대로, standbyStartTime은 currentRoundStartTime에서 대기 시간을 빼고,
    //     standbyEndTime은 currentRoundStartTime이 된다.
    public static StandbyResponseVo converter(Map<Object, Object> resultMap) {
        StandbyResponseVo standbyResponseVo;
        try {
            standbyResponseVo = StandbyResponseVo.builder()
                    .nextPrice(new BigDecimal((String) resultMap.get(RedisVariableEnum.CURRENT_PRICE.getVariable())))
                    .standbyStartTime(LocalDateTime.parse((String) resultMap.get(
                                            RedisVariableEnum.CURRENT_ROUND_START_TIME.getVariable()),
                                    DateTimeFormatter.ISO_DATE_TIME)
                            .plusSeconds(StandbyTimeEnum.SECONDS_15.getSecond()))
                    .standbyEndTime(LocalDateTime.parse((String) resultMap.get(
                            RedisVariableEnum.CURRENT_ROUND_START_TIME.getVariable()), DateTimeFormatter.ISO_DATE_TIME))
                    .build();
        } catch (Exception e) {
            log.warn("Standby Page API Error >>> {}", e.getMessage());
            throw new CustomException(ResponseStatus.NO_DATA);
        }
        log.info("Standby Page Result >>> {}", standbyResponseVo.toString());
        return standbyResponseVo;
    }
}
