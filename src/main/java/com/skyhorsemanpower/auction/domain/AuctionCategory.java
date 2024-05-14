package com.skyhorsemanpower.auction.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "auction_category")
public class AuctionCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long auctionCategoryid;

    @Column(nullable = false)
    private String auctionUuid;

    @ManyToOne
    private Category category;
}
