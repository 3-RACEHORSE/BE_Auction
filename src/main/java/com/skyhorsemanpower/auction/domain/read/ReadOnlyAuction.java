package com.skyhorsemanpower.auction.domain.read;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@Document(collation = "auction")
public class ReadOnlyAuction {
    @Id
    private String auctionPostId;

    private String auctionUuid;
    private String uuid;
    private String handle;
    private String title;
    private String content;
    private int minimumBiddingPrice;


    @Builder
    public ReadOnlyAuction(String auctionUuid, String uuid, String handle, String title, String content, int minimumBiddingPrice) {
        this.auctionUuid = auctionUuid;
        this.uuid = uuid;
        this.handle = handle;
        this.title = title;
        this.content = content;
        this.minimumBiddingPrice = minimumBiddingPrice;
    }
}
