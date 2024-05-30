package com.skyhorsemanpower.auction.data.dto;

import lombok.Getter;

@Getter
public class IsSubscribedDto {
    private boolean isSubscribed;

    public boolean isSubscribed() {
        return this.isSubscribed;
    }
}
