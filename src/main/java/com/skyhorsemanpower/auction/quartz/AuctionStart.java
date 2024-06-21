package com.skyhorsemanpower.auction.quartz;

import com.skyhorsemanpower.auction.kafka.KafkaProducerCluster;
import com.skyhorsemanpower.auction.kafka.Topics;
import com.skyhorsemanpower.auction.kafka.dto.AuctionStartDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


@Slf4j
@RequiredArgsConstructor
public class AuctionStart implements Job {
    private final KafkaProducerCluster producer;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("Auction Start Job Start!");

        // JobDataMap에서 auctionUuid 추출
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        String auctionUuid = jobDataMap.getString("auctionUuid");

        // 경매 상태를 AUCTION_IS_IN_PROGESS로 변경 메시지 전달
        AuctionStartDto auctionStartDto = AuctionStartDto.builder()
                .auctionUuid(auctionUuid)
                .build();
        log.info("AuctionStartDto >>> {}", auctionStartDto.toString());
        producer.sendMessage(Topics.AUCTION_START_STATE.getTopic(), auctionStartDto);
    }
}
