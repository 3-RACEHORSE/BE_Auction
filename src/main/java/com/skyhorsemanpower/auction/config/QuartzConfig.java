package com.skyhorsemanpower.auction.config;

import com.skyhorsemanpower.auction.kafka.dto.InitialAuctionDto;
import com.skyhorsemanpower.auction.quartz.AuctionClose;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.util.Date;

@Configuration
@RequiredArgsConstructor
public class QuartzConfig {
    private final Scheduler scheduler;

    // 경매 시작과 경매 마감의 상태 변경 스케줄링
    public void schedulerUpdateAuctionStateJob(InitialAuctionDto initialAuctionDto) throws SchedulerException {
        // JobDataMap 생성 및 auctionUuid 설정
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("auctionUuid", initialAuctionDto.getAuctionUuid());

        // Job 생성
        JobDetail auctionCloseJob = JobBuilder
                .newJob(AuctionClose.class)
                .withIdentity("AuctionCloseJob_" + initialAuctionDto.getAuctionUuid(),
                        "AuctionCloseGroup")
                .usingJobData(jobDataMap)
                .withDescription("경매 마감 Job")
                .build();

        Date auctionEndDate = Date.from(Instant.ofEpochMilli(initialAuctionDto.getAuctionStartTime()));

        // Trigger 생성
        Trigger auctionCloseTrigger = TriggerBuilder
                .newTrigger()
                .withIdentity("AuctionCloseTrigger_" + initialAuctionDto.getAuctionUuid(),
                        "AuctionCloseGroup")
                .withDescription("경매 마감 Trigger")

                // test용 20초 후 시작하는 스케줄러
                .startAt(DateBuilder.futureDate(20, DateBuilder.IntervalUnit.SECOND))

                //Todo 실제 배포에서는 auctionEndDate을 사용해야 한다.
//                .startAt(auctionEndDate)
                .build();

        // 스케줄러 생성 및 Job, Trigger 등록
        scheduler.scheduleJob(auctionCloseJob, auctionCloseTrigger);
    }
}
