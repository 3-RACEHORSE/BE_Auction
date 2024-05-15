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
    private String sellerUuid;
    private String handle;
    private String title;
    private String content;
    private String category;
    private int minimumBiddingPrice;
    private String thumbnail;
    private List<String> images;
    private String auctionUuid;

    @Builder
    public CreateAuctionDto(String sellerUuid, String title, String content, String category, int minimumBiddingPrice, String thumbnail, List<String> images) {
        this.sellerUuid = sellerUuid;
        this.title = title;
        this.content = content;
        this.category = category;
        this.minimumBiddingPrice = minimumBiddingPrice;
        this.thumbnail = thumbnail;
        this.images = images;
    }

    // converter
    public static CreateAuctionDto createAuctionVoToDto(String uuid, CreateAuctionRequestVo createAuctionRequestVo) {
        return CreateAuctionDto.builder()
                .sellerUuid(uuid)
                .title(createAuctionRequestVo.getTitle())
                .content(createAuctionRequestVo.getContent())
                .category(createAuctionRequestVo.getCategory())
                .minimumBiddingPrice(createAuctionRequestVo.getMinimumBiddingPrice())
                .thumbnail(createAuctionRequestVo.getThumbnail())
                .images(createAuctionRequestVo.getImages())
                .build();
    }
}
