package com.poc.carddelivery.domain.delivery;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 배치 실행 이력. 운영에서 "어제 파일 나갔어요?" 를 답하는 테이블.
 */
@Entity
@Table(name = "job_history")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JobHistory {

    public enum Result { SUCCESS, FAIL }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 실행 단위 식별자 (파일명에도 포함) */
    @Column(nullable = false, length = 40)
    private String batchId;

    @Column(nullable = false, length = 10)
    private String courierCode;

    @Column(nullable = false)
    private int targetCount;

    @Column(nullable = false)
    private int sentCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Result result;

    @Column(length = 500)
    private String errorMessage;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    private LocalDateTime finishedAt;
}
