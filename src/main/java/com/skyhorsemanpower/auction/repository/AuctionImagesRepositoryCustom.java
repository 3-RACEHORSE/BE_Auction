package com.skyhorsemanpower.auction.repository;

import java.util.List;

public interface AuctionImagesRepositoryCustom {
    String getThumbnailUrl(String auctionUuid);
    List<String> getImagesUrl(String auctionUuid);
}
