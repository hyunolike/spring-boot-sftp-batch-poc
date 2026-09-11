package com.poc.carddelivery.domain.delivery;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 배송 요청 건. 전송 상태 머신(READY -> REQUESTED -> ...)의 주체.
 */
@Entity
@Table(name = "delivery_request")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeliveryRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "card_issue_id")
    private CardIssue cardIssue;

    /** 담당 배송업체 코드 */
    @Column(nullable = false, length = 10)
    private String courierCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private DeliveryStatus status;

    /** 전송된 파일명 (멱등성 추적용) */
    @Column(length = 100)
    private String sentFileName;

    private LocalDateTime sentAt;

    /**
     * 파일 전송 성공 후 상태 전이.
     * 같은 건이 다음 배치에 다시 조회되지 않도록 하는 멱등성의 핵심.
     */
    public void markRequested(String fileName, LocalDateTime sentAt) {
        this.status = DeliveryStatus.REQUESTED;
        this.sentFileName = fileName;
        this.sentAt = sentAt;
    }
}
