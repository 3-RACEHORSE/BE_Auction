package com.skyhorsemanpower.auction.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JsonPropertyEnum {
    HANDLE(Constant.HANDLE),
    PROFILE(Constant.PROFILE),
    IS_SUBSCRIBED(Constant.IS_SUBSCRIBED);

    public static class Constant {
        public static final String HANDLE = "handle";
        public static final String PROFILE = "profileImage";
        public static final String IS_SUBSCRIBED = "isSubscribed";
    }

    private final String property;
}
