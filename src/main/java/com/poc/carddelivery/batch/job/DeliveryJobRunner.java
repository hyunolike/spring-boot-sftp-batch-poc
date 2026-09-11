package com.poc.carddelivery.batch.job;

import com.poc.carddelivery.application.DeliveryExportService;
import com.poc.carddelivery.domain.courier.Courier;
import com.poc.carddelivery.domain.courier.CourierRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 활성 배송업체 전체를 순회 실행.
 * 한 업체 실패가 다른 업체 전송을 막지 않도록 업체 단위로 예외를 격리한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryJobRunner {

    private final CourierRepository courierRepository;
    private final DeliveryExportService deliveryExportService;

    public void run() {
        List<Courier> couriers = courierRepository.findAllByActiveTrue();
        log.info("배송 배치 시작 - 대상 배송업체 {}곳", couriers.size());

        for (Courier courier : couriers) {
            try {
                deliveryExportService.export(courier);
            } catch (Exception e) {
                // export 내부에서 이력 기록 완료. 다음 업체 계속 진행.
                log.warn("[{}] 배치 실패, 다음 업체로 진행", courier.getCode());
            }
        }
        log.info("배송 배치 종료");
    }
}
