package com.skyhorsemanpower.auction.quartz;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

public class EndAuctionListener implements JobListener {
    @Override
    public String getName() {
        return "EndAuction";
    }

    // Job 실행 이전 수행
    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
    }

    // Job 실행 취소 시점 수행
    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
    }

    // Job 실행 와뇰 시점 수행
    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        System.out.println("경매 마감 처리되었습니다.");
    }
}
