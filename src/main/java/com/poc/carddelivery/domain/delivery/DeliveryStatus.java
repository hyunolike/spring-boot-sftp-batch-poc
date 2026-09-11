package com.poc.carddelivery.domain.delivery;

public enum DeliveryStatus {
    READY,      // 배송 요청 대상 (미전송)
    REQUESTED,  // 배송업체로 파일 전송 완료
    DELIVERED,  // 배송 완료 (회신 파일 수신 시 갱신 - 확장 포인트)
    RETURNED    // 반송
}
