package com.skyhorsemanpower.auction.data.dto;

import com.skyhorsemanpower.auction.data.vo.CreateAuctionRequestVo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreateAuctionDto {
    private String uuid;
    private String handle;
    private String title;
    private String content;
    private long majorCategoryId;
    private long minorCategoryId;
    private int minimumBiddingPrice;
    private String thumbnail;
    private List<String> images;
    private String auctionUuid;

    @Builder
    public CreateAuctionDto(String uuid, String title, String content, long majorCategoryId, long minorCategoryId, int minimumBiddingPrice, String thumbnail, List<String> images) {
        this.uuid = uuid;
        this.title = title;
        this.content = content;
        this.majorCategoryId = majorCategoryId;
        this.minorCategoryId = minorCategoryId;
        this.minimumBiddingPrice = minimumBiddingPrice;
        this.thumbnail = thumbnail;
        this.images = images;
    }

    // converter
    public static CreateAuctionDto createAuctionVoToDto(String uuid, CreateAuctionRequestVo createAuctionRequestVo) {
        return CreateAuctionDto.builder()
                .uuid(uuid)
                .title(createAuctionRequestVo.getTitle())
                .content(createAuctionRequestVo.getContent())
                .majorCategoryId(createAuctionRequestVo.getMajorCategoryId())
                .minorCategoryId(createAuctionRequestVo.getMinorCategoryId())
                .minimumBiddingPrice(createAuctionRequestVo.getMinimumBiddingPrice())
                .thumbnail(createAuctionRequestVo.getThumbnail())
                .images(createAuctionRequestVo.getImages())
                .build();
    }
}
