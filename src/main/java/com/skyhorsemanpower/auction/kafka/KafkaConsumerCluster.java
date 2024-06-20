package com.skyhorsemanpower.auction.kafka;

import com.skyhorsemanpower.auction.common.DateTimeConverter;
import com.skyhorsemanpower.auction.config.QuartzConfig;
import com.skyhorsemanpower.auction.domain.RoundInfo;
import com.skyhorsemanpower.auction.kafka.dto.InitialAuctionDto;
import com.skyhorsemanpower.auction.repository.RoundInfoRepository;
import com.skyhorsemanpower.auction.status.RoundTimeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaConsumerCluster {
    private final RoundInfoRepository roundInfoRepository;
    private final QuartzConfig quartzConfig;

    @KafkaListener(topics = Topics.Constant.INITIAL_AUCTION, groupId = "${spring.kafka.consumer.group-id}")
    public void initialAuction(@Payload LinkedHashMap<String, Object> message,
        @Headers MessageHeaders messageHeaders) {
        log.info("consumer: success >>> message: {}, headers: {}", message.toString(),
                messageHeaders);

        // round_info 초기 데이터 저장
        InitialAuctionDto initialAuctionDto = InitialAuctionDto.builder()
                .auctionUuid(message.get("auctionUuid").toString())
                .startPrice(new BigDecimal(message.get("startPrice").toString()))
                .numberOfEventParticipants((Integer) message.get("numberOfEventParticipants"))
                .auctionStartTime((Long) message.get("auctionStartTime"))
                .auctionEndTime((Long) message.get("auctionEndTime"))
                .incrementUnit(new BigDecimal(message.get("incrementUnit").toString()))
                .build();
        log.info("InitialAuctionDto >>> {}", initialAuctionDto.toString());

        initialRoundInfo(initialAuctionDto);

        // 경매 마감 스케줄러 등록
        try {
            quartzConfig.schedulerUpdateAuctionStateJob(initialAuctionDto);
        } catch (Exception e1) {
            log.warn(e1.getMessage());
        }
    }

    private void initialRoundInfo(InitialAuctionDto initialAuctionDto) {
        // Instant 타입을 LocalDateTime 변환
        LocalDateTime roundStartTime = DateTimeConverter.
                instantToLocalDateTime(initialAuctionDto.getAuctionStartTime());

        RoundInfo roundinfo = RoundInfo.builder()
                .auctionUuid(initialAuctionDto.getAuctionUuid())
                .round(1)
                .roundStartTime(roundStartTime)
                .roundEndTime(roundStartTime.plusSeconds(RoundTimeEnum.SECONDS_60.getSecond()))
                .incrementUnit(initialAuctionDto.getIncrementUnit())
                .price(initialAuctionDto.getStartPrice())
                .isActive(true)
                .numberOfParticipants((long) initialAuctionDto.getNumberOfEventParticipants())
                .leftNumberOfParticipants((long) initialAuctionDto.getNumberOfEventParticipants())
                .createdAt(LocalDateTime.now())
                .build();

        log.info("Initial round_info >>> {}", roundinfo);
        roundInfoRepository.save(roundinfo);
    }
}
