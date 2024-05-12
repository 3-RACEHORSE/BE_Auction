package com.skyhorsemanpower.auction.domain;

import com.skyhorsemanpower.auction.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Auction extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long auctionPostId;

    @Column(nullable = false)
    private String auctionUuid;

    @Column(nullable = false)
    private String uuid;

    @Column(nullable = false)
    private String handle;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int minimumBiddingPrice;

    @Column(nullable = false)
    private LocalDateTime endedAt;


    @Builder
    public Auction(String auctionUuid, String uuid, String handle, String title, String content, int minimumBiddingPrice) {
        this.auctionUuid = auctionUuid;
        this.uuid = uuid;
        this.handle = handle;
        this.title = title;
        this.content = content;
        this.minimumBiddingPrice = minimumBiddingPrice;
        this.endedAt = getCreatedDate().plusDays(1);
    }
}
