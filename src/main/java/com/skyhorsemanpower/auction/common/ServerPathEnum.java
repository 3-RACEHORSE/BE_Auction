package com.skyhorsemanpower.auction.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ServerPathEnum {
    MEMBER_SERVER("http://43.203.131.185:8000/member-service"),
    GET_HANDLE("/api/v1/non-authorization/users/datarequest"),


    GET_ISSUBSCRIBED("/api/v1/authorization/subscription/auction/is-subscribed");
    private final String server;
}
