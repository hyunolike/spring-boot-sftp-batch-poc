package com.poc.carddelivery.application;

import com.poc.carddelivery.domain.courier.Courier;
import com.poc.carddelivery.domain.delivery.*;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 배송업체 1곳에 대한 배치 파이프라인:
 * 대상 조회 -> 유형별 파일 생성 -> 암호화 -> SFTP 전송 -> 상태 갱신 -> 이력 기록
 *
 * 트랜잭션 전략:
 * - 상태 갱신(markRequested)은 배치 트랜잭션 안에서 수행.
 * - 중간 실패 시 전체 롤백 -> READY 유지 -> 다음 배치에서 자동 재시도.
 * - 이력은 JobHistoryRecorder(REQUIRES_NEW)로 롤백과 무관하게 기록.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryExportService {

    private static final DateTimeFormatter BATCH_ID_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final DeliveryRequestRepository deliveryRequestRepository;
    private final JobHistoryRecorder jobHistoryRecorder;
    private final DeliveryFileWriter fileWriter;
    private final FileEncryptor fileEncryptor;
    private final FileTransferer fileTransferer;

    @Transactional
    public void export(Courier courier) {
        LocalDateTime startedAt = LocalDateTime.now();
        String batchId = startedAt.format(BATCH_ID_FORMAT) + "_" + courier.getCode();

        List<DeliveryRequest> targets = deliveryRequestRepository
                .findAllByCourierCodeAndStatus(courier.getCode(), DeliveryStatus.READY);

        if (targets.isEmpty()) {
            log.info("[{}] 전송 대상 없음", courier.getCode());
            return;
        }

        int sentCount = 0;
        try {
            Map<IssueType, List<DeliveryRequest>> byType = targets.stream()
                    .collect(Collectors.groupingBy(r -> r.getCardIssue().getIssueType()));

            for (Map.Entry<IssueType, List<DeliveryRequest>> entry : byType.entrySet()) {
                sentCount += exportOneFile(courier, batchId, entry.getKey(), entry.getValue());
            }

            jobHistoryRecorder.record(batchId, courier.getCode(), targets.size(), sentCount,
                    JobHistory.Result.SUCCESS, null, startedAt);
        } catch (Exception e) {
            log.error("[{}] 배치 실패 batchId={}", courier.getCode(), batchId, e);
            jobHistoryRecorder.record(batchId, courier.getCode(), targets.size(), sentCount,
                    JobHistory.Result.FAIL, truncate(e.getMessage()), startedAt);
            throw e; // 상태 갱신 롤백 -> READY 유지 -> 다음 배치에서 재시도
        }
    }

    private int exportOneFile(Courier courier, String batchId,
                              IssueType issueType, List<DeliveryRequest> items) {
        Path plainFile = fileWriter.write(courier.getCode(), issueType, batchId, items);
        Path encryptedFile = fileEncryptor.encrypt(plainFile);
        fileTransferer.upload(courier, encryptedFile);

        String fileName = encryptedFile.getFileName().toString();
        LocalDateTime sentAt = LocalDateTime.now();
        items.forEach(item -> item.markRequested(fileName, sentAt));

        log.info("[{}] {} {}건 전송 완료 -> {}",
                courier.getCode(), issueType, items.size(), fileName);
        return items.size();
    }

    private String truncate(String message) {
        if (message == null) {
            return null;
        }
        return message.length() <= 500 ? message : message.substring(0, 500);
    }
}
