package com.skyhorsemanpower.auction.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

import java.util.Date;

@Slf4j
public class EndAuctionListener implements JobListener {
//    private static final int MAX_RETRY_ATTEMPTS = 3;
//    private static final long RETRY_DELAY_MILLIS = 5000;
private static final EndAuctionListenerEnum RETRY_POLICY = EndAuctionListenerEnum.RETRY_THREE_DELAY_5000;

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

    // Job 실행 완료 시점 수행
    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        // 경매 마감에 에러가 생긴 경우
        if (jobException != null) {
            // 오류 발생 시 재시도 로직
            Integer retryCount = context.getMergedJobDataMap().getInt("retryCount");

            if (retryCount < RETRY_POLICY.getMAX_RETRY_ATTEMPTS()) {
                retryCount++;
                context.getMergedJobDataMap().put("retryCount", retryCount);
                JobDetail jobDetail = context.getJobDetail();
                Trigger trigger = TriggerBuilder.newTrigger()
                        .withIdentity(jobDetail.getKey().getName() + "_retry", jobDetail.getKey().getGroup())
                        .startAt(new Date(System.currentTimeMillis() + RETRY_POLICY.getRETRY_DELAY_MILLIS()))
                        .build();
                try {
                    context.getScheduler().scheduleJob(jobDetail, trigger);
                    log.info("Retrying job: " + jobDetail.getKey() + " (attempt " + retryCount + ")");
                } catch (SchedulerException e) {
                    log.error("Failed to reschedule job: " + e.getMessage());
                }
            } else {
                log.error("Max retry attempts reached for job: " + context.getJobDetail().getKey());
            }
        }
        // 경매 마감이 정상적으로 처리된 경우
        else {
            log.info("Auction End Completely");
        }
    }
}
