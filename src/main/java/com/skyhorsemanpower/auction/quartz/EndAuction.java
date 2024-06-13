package com.skyhorsemanpower.auction.quartz;

import com.skyhorsemanpower.auction.repository.AuctionHistoryReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.data.mongodb.core.MongoTemplate;


@Slf4j
@RequiredArgsConstructor
public class EndAuction implements Job {
    private final AuctionHistoryReactiveRepository auctionHistoryRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // JobDataMap에서 auctionUuid 추출
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        String auctionUuid = jobDataMap.getString("auctionUuid");

        // 경매 등록
    }
}
