package com.skyhorsemanpower.auction.data.vo;

import com.skyhorsemanpower.auction.domain.cqrs.read.ReadOnlyAuction;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
public class SearchAllAuctionResponseVo {
    private String auctionUuid;
    private String handle;
    private String uuid;
    private String title;
    private String content;
    private String categoryMajorName;
    private String categoryMinorName;
    private int minimumBiddingPrice;
    private String thumbnail;
    private List<String> images;
    private LocalDateTime createdAt;
    private LocalDateTime endedAt;
//    private int bidPrice; // 진행중인 경매들만 가져오기 때문에 낙찰가는 없음

    @Builder
    public SearchAllAuctionResponseVo(String auctionUuid, String handle, String uuid, String title, String content, String categoryMajorName, String categoryMinorName, int minimumBiddingPrice, String thumbnail, List<String> images, LocalDateTime createdAt, LocalDateTime endedAt) {
        this.auctionUuid = auctionUuid;
        this.handle = handle;
        this.uuid = uuid;
        this.title = title;
        this.content = content;
        this.categoryMajorName = categoryMajorName;
        this.categoryMinorName = categoryMinorName;
        this.minimumBiddingPrice = minimumBiddingPrice;
        this.thumbnail = thumbnail;
        this.images = images;
        this.createdAt = createdAt;
        this.endedAt = endedAt;
    }

    //Todo 카테고리 테이블에서 카테고리명 연결해야 함
    //Todo 이미지 테이블에서 이미지 연결해야 함
    public static SearchAllAuctionResponseVo readOnlyAuctionToSearchAllAuctionResponseVo(ReadOnlyAuction readOnlyAuction) {
        return SearchAllAuctionResponseVo.builder().
                auctionUuid(readOnlyAuction.getAuctionUuid())
                .handle(readOnlyAuction.getHandle())
                .uuid(readOnlyAuction.getUuid())
                .title(readOnlyAuction.getTitle())
                .content(readOnlyAuction.getContent())
                .categoryMajorName("major")
                .categoryMinorName("minor")
                .minimumBiddingPrice(readOnlyAuction.getMinimumBiddingPrice())
                .thumbnail("thumbnail")
                .images(null)
                .createdAt(readOnlyAuction.getCreatedAt())
                .endedAt(readOnlyAuction.getEndedAt())
                .build();
    }
}
