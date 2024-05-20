package com.skyhorsemanpower.auction.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseStatus {
    // 400, 401, 500 로 Status 처리

    INTERNAL_SERVER_ERROR(500, "서버 에러 응답"),

    // postgreSQL 관련 에러
    POSTGRESQL_ERROR(500, "PostgreSQL 에러"),
    POSTGRESQL_NOT_FOUND(500, "PostgreSQL에서 조회할 수 없습니다."),

    // MongoDB 관련 에러
    MONGODB_ERROR(500, "MongoDB 에러"),
    MONGODB_NOT_FOUND(404, "MongoDB에서 조회할 수 없습니다."),

    // Quartz 관련 에러
    SCHEDULER_ERROR(500, "스케줄러 등록 에러"),

    // 경매 참여자가 없는 경우
    NO_PARTICIPATE_AUCTION(404, "경매 참여자가 없습니다."),


    // 예외 테스트 용
    EXCEPTION_TEST(500, "예외 테스트");

    private final int code;
    private final String message;

}
