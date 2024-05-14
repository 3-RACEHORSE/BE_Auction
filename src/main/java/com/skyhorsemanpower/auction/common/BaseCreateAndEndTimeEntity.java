package com.skyhorsemanpower.auction.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseCreateAndEndTimeEntity {

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime endedAt;

    public BaseCreateAndEndTimeEntity() {
        this.createdAt = LocalDateTime.now(); // 현재 시간으로 설정
        this.endedAt = this.createdAt.plusDays(1);
    }
}
