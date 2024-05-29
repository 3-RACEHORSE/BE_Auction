package com.skyhorsemanpower.auction.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseStatus {
    // 400, 401, 500 로 Status 처리

    INTERNAL_SERVER_ERROR(500, "서버 에러 응답"),

    // postgreSQL 관련 에러
    POSTGRESQL_ERROR(500, "PostgreSQL 에러"),

    // MongoDB 관련 에러
    MONGODB_ERROR(500, "MongoDB 에러"),

    // Quartz 관련 에러
    SCHEDULER_ERROR(500, "스케줄러 등록 에러"),

    // 경매 참여자가 없는 경우
    NO_PARTICIPATE_AUCTION(404, "경매 참여자가 없습니다."),

    // 경매 마감 시간 이후로 입찰 제한
    NOT_BIDDING_TIME(400, "입찰 가능한 시간이 아닙니다."),

    // 입찰제시금이 현재 입찰 최고가보다 작은 경우
    UNSATISFING_BIDDING_PRICE(400, "입찰 제시금이 기존 입찰금보다 적습니다."),

    // 조회 데이터가 없는 경우
    NO_DATA(404, "데이터가 없습니다."),

    // 경매 입찰 불가능
    CAN_NOT_BIDDING(404, "입찰이 불가능합니다."),

    // 예외 테스트 용
    EXCEPTION_TEST(500, "예외 테스트") ;

    private final int code;
    private final String message;

}
