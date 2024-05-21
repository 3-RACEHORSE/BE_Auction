package com.skyhorsemanpower.auction.quartz;

import com.skyhorsemanpower.auction.common.CustomException;
import com.skyhorsemanpower.auction.status.ResponseStatus;
import com.skyhorsemanpower.auction.domain.cqrs.read.ReadOnlyAuction;
import com.skyhorsemanpower.auction.repository.AuctionHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import reactor.core.publisher.Mono;


@Slf4j
@RequiredArgsConstructor
public class EndAuction implements Job {
    private final AuctionHistoryRepository auctionHistoryRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // JobDataMap에서 auctionUuid 추출
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        String auctionUuid = jobDataMap.getString("auctionUuid");

        // auctionUuid를 통한 로직 구현
        // 가장 큰 biddingPrice 레코드에서 biddingUuid, biddingPrice 추출
        auctionHistoryRepository.findTopByAuctionUuidOrderByBiddingPriceDesc(auctionUuid)
                .switchIfEmpty(Mono.error(new CustomException(ResponseStatus.NO_PARTICIPATE_AUCTION)))
                .subscribe(auctionHistory -> {
                    String bidderUuid = auctionHistory.getBiddingUuid();
                    int bidPrice = auctionHistory.getBiddingPrice();

                    // 해당 ReadOnlyAuction 엔티티의 bidderUuid, bidPrice 갱신
                    Query query = new Query(Criteria.where("auctionUuid").is(auctionUuid));
                    Update update = new Update().set("bidderUuid", bidderUuid).set("bidPrice", bidPrice);
                    mongoTemplate.updateFirst(query, update, ReadOnlyAuction.class);
                }, throwable -> {
                    // 오류 처리
                    log.error("There was an error in closing the auction.", throwable);
                    try {
                        throw new JobExecutionException(throwable);
                    } catch (JobExecutionException e) {
                        log.error("Call the JobExecutionException", e);
                    }
                });
    }
}
