package com.skyhorsemanpower.auction.domain.cqrs.command;

import com.skyhorsemanpower.auction.common.BaseCreateAndEndTimeEntity;
import jakarta.persistence.*;

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
@Table(name = "auction")
public class CommandOnlyAuction extends BaseCreateAndEndTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long auctionPostId;

    @Column(nullable = false)
    private String auctionUuid;

    @Column(nullable = false)
    private String sellerUuid;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private int minimumBiddingPrice;

    @Column(nullable = false)
    private String bidderUuid;

    @Column(nullable = false)
    private int bidPrice;


    @Builder
    public CommandOnlyAuction(long auctionPostId, String auctionUuid, String sellerUuid, String title, String content, String category, int minimumBiddingPrice, String bidderUuid, int bidPrice) {
        this.auctionPostId = auctionPostId;
        this.auctionUuid = auctionUuid;
        this.sellerUuid = sellerUuid;
        this.title = title;
        this.content = content;
        this.category = category;
        this.minimumBiddingPrice = minimumBiddingPrice;
        this.bidderUuid = bidderUuid;
        this.bidPrice = bidPrice;
    }
}
