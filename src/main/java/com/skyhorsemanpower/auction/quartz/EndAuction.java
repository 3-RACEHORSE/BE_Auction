package com.skyhorsemanpower.auction.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@Slf4j
public class EndAuction implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info(">>>>>>>>>>>>>>>> execute");
    }
}
