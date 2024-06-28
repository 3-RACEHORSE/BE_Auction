package com.skyhorsemanpower.auction.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AuctionUnique {
    @Id
    private String auctionUuid;
}
