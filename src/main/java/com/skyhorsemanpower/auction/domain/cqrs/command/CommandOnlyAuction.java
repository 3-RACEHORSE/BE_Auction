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
    private String uuid;

    @Column(nullable = false)
    private String handle;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int minimumBiddingPrice;


    @Builder
    public CommandOnlyAuction(String auctionUuid, String uuid, String handle, String title, String content, int minimumBiddingPrice) {
        this.auctionUuid = auctionUuid;
        this.uuid = uuid;
        this.handle = handle;
        this.title = title;
        this.content = content;
        this.minimumBiddingPrice = minimumBiddingPrice;
    }
}
