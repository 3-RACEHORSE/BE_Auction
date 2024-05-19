package com.skyhorsemanpower.auction.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseStatus {

    /**
     * 200: 요청 성공
     **/
    SUCCESS(200, "요청에 성공했습니다."),
    //
//    /**
//     * 400 : security 에러
//     */
//    WRONG_JWT_TOKEN(false, 401, "다시 로그인 해주세요"),
//
//    /**
//     * 900: 기타 에러
//     */
    INTERNAL_SERVER_ERROR(500, "Internal server error"),

    // postgreSQL 관련 에러
    POSTGRESQL_ERROR(500, "PostgreSQL error"),
    POSTGRESQL_NOT_FOUND(500, "PostgreSQL Not Found"),

    // MongoDB 관련 에러
    MONGODB_ERROR(500, "MongoDB error"),
    MONGODB_NOT_FOUND(500, "MongoDB Not Found"),

    // Quartz 관련 에러
    SCHEDULER_ERROR(500, "Scheduler Regist Error"),

    // 경매 참여자가 없는 경우
    NO_PARTICIPATE_AUCTION(500, "No people participated in the auction"),


    // 예외 테스트 용
    EXCEPTION_TEST(500, "Exception test");

    private final int code;
    private final String message;

}
