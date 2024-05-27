package com.skyhorsemanpower.auction.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ServerPathEnum {
    MEMBER_SERVER("http://43.203.131.185:8000/member-service"),
    GET_HANDLE("/api/v1/non-authorization/users/datarequest");

    private final String server;
}
