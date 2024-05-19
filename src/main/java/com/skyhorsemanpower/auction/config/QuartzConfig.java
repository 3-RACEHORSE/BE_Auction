package com.skyhorsemanpower.auction.config;

import com.skyhorsemanpower.auction.quartz.EndAuction;
import com.skyhorsemanpower.auction.quartz.EndAuctionListener;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class QuartzConfig {
    private Scheduler scheduler;

    // Qurtz의 실제 처리 과정 담당
    @PostConstruct
    private void jobProgress() throws SchedulerException {
        simpleScheduler();
    }

    // SimpleScheduler 메서드
    private void simpleScheduler() throws SchedulerException {

        // Job 생성
        JobDetail job = JobBuilder
                .newJob(EndAuction.class)
                .withIdentity("EndAuctionJob", "EndAuctionGroup")
                .withDescription("경매 마감 Job")
                .build();

        // Trigger 생성
        Trigger trigger = TriggerBuilder
                .newTrigger()
                .withIdentity("EndAuctionTrigger", "EndAuctionGroup")
                .withDescription("경매 마감 Trigger")
                .startNow()
                .withSchedule(
                        SimpleScheduleBuilder
                                .simpleSchedule()
                                .withIntervalInSeconds(5)
                                .repeatForever())
                .build();

        // 스케줄러 생성 및 Job, Trigger 등록
        scheduler = new StdSchedulerFactory().getScheduler();
        EndAuctionListener endAuctionListener = new EndAuctionListener();
        scheduler.getListenerManager().addJobListener(endAuctionListener);
        scheduler.start();
        scheduler.scheduleJob(job, trigger);

    }
}
