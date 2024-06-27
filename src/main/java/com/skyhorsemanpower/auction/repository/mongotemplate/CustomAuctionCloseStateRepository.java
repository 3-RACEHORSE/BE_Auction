package com.skyhorsemanpower.auction.repository.mongotemplate;

public interface CustomAuctionCloseStateRepository {
    boolean setAuctionClosedInNotExists(String auctionUuid);
}
