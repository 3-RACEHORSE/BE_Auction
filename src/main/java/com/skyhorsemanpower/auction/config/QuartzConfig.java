package com.skyhorsemanpower.auction.config;

import com.skyhorsemanpower.auction.quartz.EndAuction;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Calendar;
import java.util.Date;

@Configuration
@RequiredArgsConstructor
public class QuartzConfig {
    private final Scheduler scheduler;

    @Bean
    public Scheduler scheduler() throws SchedulerException {
        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        scheduler.start();
        return scheduler;
    }

    // SimpleScheduler 메서드
    public void schedulerEndAuctionJob(String auctionUuid) throws SchedulerException {
        // 경매를 만드는 시간에서 하루를 더한 endedAt
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date endedAt = calendar.getTime();

        // JobDataMap 생성 및 auctionUuid 설정
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("auctionUuid", auctionUuid);

        // 오류로 실행되지 않았을 때를 위한 retryCount key 값
        jobDataMap.put("retryCount", 0);

        // Job 생성
        JobDetail job = JobBuilder
                .newJob(EndAuction.class)
                .withIdentity("EndAuctionJob_" + auctionUuid, "EndAuctionGroup")
                .usingJobData(jobDataMap)
                .withDescription("경매 마감 Job")
                .build();

        // Trigger 생성
        Trigger trigger = TriggerBuilder
                .newTrigger()
                .withIdentity("EndAuctionTrigger_" + auctionUuid, "EndAuctionGroup")
                .withDescription("경매 마감 Trigger")

                // test용 30초 후 시작하는 스케줄러
                .startAt(DateBuilder.futureDate(60, DateBuilder.IntervalUnit.SECOND))

                //Todo 실제 배포에서는 endedAt을 사용해야 한다.
//                .startAt(endedAt)
                .build();

        // 스케줄러 생성 및 Job, Trigger 등록
        scheduler.scheduleJob(job, trigger);

    }
}
