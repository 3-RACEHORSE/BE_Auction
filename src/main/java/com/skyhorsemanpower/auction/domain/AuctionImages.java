package com.skyhorsemanpower.auction.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "auction_images")
public class AuctionImages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long auctionImagesId;

    @Column(nullable = false)
    private String auctionUuid;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private boolean thumbnail;

    @Builder
    public AuctionImages(long auctionImagesId, String auctionUuid, String imageUrl) {
        this.auctionImagesId = auctionImagesId;
        this.auctionUuid = auctionUuid;
        this.imageUrl = imageUrl;
        this.thumbnail = false;
    }

    @Builder
    public AuctionImages(long auctionImagesId, String auctionUuid, String imageUrl, boolean thumbnail) {
        this.auctionImagesId = auctionImagesId;
        this.auctionUuid = auctionUuid;
        this.imageUrl = imageUrl;
        this.thumbnail = thumbnail;
    }
}
