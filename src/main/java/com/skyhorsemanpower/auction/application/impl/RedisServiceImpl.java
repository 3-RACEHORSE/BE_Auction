package com.skyhorsemanpower.auction.application.impl;

import com.skyhorsemanpower.auction.application.RedisService;
import com.skyhorsemanpower.auction.common.exception.CustomException;
import com.skyhorsemanpower.auction.common.exception.ResponseStatus;
import com.skyhorsemanpower.auction.common.redis.RedisVariableEnum;
import com.skyhorsemanpower.auction.data.vo.AuctionPageResponseVo;
import com.skyhorsemanpower.auction.data.vo.StandbyResponseVo;
import com.skyhorsemanpower.auction.repository.AuctionHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisServiceImpl implements RedisService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final AuctionHistoryRepository auctionHistoryRepository;

    @Override
    public AuctionPageResponseVo getAuctionPage(String auctionUuid) {
        // redis 결과 조회
        Map<Object, Object> resultMap = redisTemplate.opsForHash().entries(auctionUuid);
        log.info("Auction Page Result Map >>> {}", resultMap.toString());

        // 결과를 VO에 맞게 변환해서 반환
        return AuctionPageResponseVo.converter(resultMap);
    }

    @Override
    public StandbyResponseVo getStandbyPage(String auctionUuid) {
        // redis 결과 조회
        Map<Object, Object> resultMap = redisTemplate.opsForHash().entries(auctionUuid);

        // 결과를 VO에 맞게 변환해서 반환
        return StandbyResponseVo.converter(resultMap);
    }

    @Override
    public void updateAuctionReferenceTable(String auctionUuid) {
        // redis 결과 조회
        Map<Object, Object> resultMap = redisTemplate.opsForHash().entries(auctionUuid);
        log.info("Before Updated Map >>> {}", resultMap);

        // 데이터 갱신
        try {
            int round = Integer.parseInt((String) resultMap.get(RedisVariableEnum.ROUND.getVariable())) + 1;
            BigDecimal currentPrice = new BigDecimal((String) resultMap.get(RedisVariableEnum.CURRENT_PRICE.getVariable()));
            BigDecimal incrementUnit = new BigDecimal((String) resultMap.get(RedisVariableEnum.INCREMENT_UNIT.getVariable()));
            currentPrice = currentPrice.add(incrementUnit);

            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            LocalDateTime currentRoundEndTime = LocalDateTime.parse((String) resultMap.get(RedisVariableEnum.CURRENT_ROUND_END_TIME.getVariable()), formatter);
            LocalDateTime currentRoundStartTime = currentRoundEndTime.plusSeconds(15);
            LocalDateTime newRoundEndTime = currentRoundStartTime.plusMinutes(1);

            Map<Object, Object> map = new HashMap<>();
            map.put(RedisVariableEnum.ROUND.getVariable(), String.valueOf(round));
            map.put(RedisVariableEnum.CURRENT_PRICE.getVariable(), currentPrice.toString());
            map.put(RedisVariableEnum.CURRENT_ROUND_START_TIME.getVariable(), currentRoundStartTime.format(formatter));
            map.put(RedisVariableEnum.CURRENT_ROUND_END_TIME.getVariable(), newRoundEndTime.format(formatter));

            // 갱신된 데이터를 다시 Redis에 저장
            redisTemplate.opsForHash().putAll(auctionUuid, map);

            // 결과 확인
            resultMap = redisTemplate.opsForHash().entries(auctionUuid);
            log.info("After Updated Map >>> {}", resultMap);
        } catch (Exception e) {
            log.warn("Update Value Is Not Exist.");
            throw new CustomException(ResponseStatus.NO_DATA);
        }
    }
}
