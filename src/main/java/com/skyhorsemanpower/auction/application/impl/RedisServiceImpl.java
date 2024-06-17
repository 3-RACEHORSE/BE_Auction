package com.skyhorsemanpower.auction.application.impl;

import com.skyhorsemanpower.auction.application.RedisService;
import com.skyhorsemanpower.auction.data.vo.AuctionPageResponseVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisServiceImpl implements RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public AuctionPageResponseVo getAuctionPage(String auctionUuid) {
        // redis 결과 조회
        Map<Object, Object> resultMap = redisTemplate.opsForHash().entries(auctionUuid);
        log.info("Auction Page Result Map >>> {}", resultMap.toString());

        // 결과를 VO에 맞게 변환해서 반환
        return AuctionPageResponseVo.converter(resultMap);
    }
}
