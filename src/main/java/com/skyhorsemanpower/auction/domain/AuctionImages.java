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
    private boolean isThumbnail;

    @Builder
    public AuctionImages(String auctionUuid, String imageUrl, boolean isThumbnail) {
        this.auctionUuid = auctionUuid;
        this.imageUrl = imageUrl;
        this.isThumbnail = isThumbnail;
    }
}
