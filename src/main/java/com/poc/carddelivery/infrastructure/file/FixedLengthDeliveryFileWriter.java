package com.poc.carddelivery.infrastructure.file;

import com.poc.carddelivery.application.DeliveryFileWriter;
import com.poc.carddelivery.domain.delivery.CardIssue;
import com.poc.carddelivery.domain.delivery.DeliveryRequest;
import com.poc.carddelivery.domain.delivery.IssueType;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 고정길이 배송요청 파일 생성기.
 *
 * 레코드 규격 (총 132자 + 개행):
 *   구분      길이   비고
 *   ----------------------------------
 *   발급유형    3    NEW / REN / RET
 *   카드번호   19    마스킹된 형태
 *   수령인     10    좌측정렬, 공백 패딩
 *   주소      100    좌측정렬, 공백 패딩
 *
 * 트레일러: "T" + 건수(9, 우측정렬 0패딩)
 *
 * NOTE: 실무 고정길이 규격은 보통 "바이트" 기준(EUC-KR 한글 2바이트)이라
 *       문자 수 기준인 이 구현은 PoC 단순화다. 규격 정합이 필요하면
 *       byte-length padding으로 교체할 것.
 */
@Component
public class FixedLengthDeliveryFileWriter implements DeliveryFileWriter {

    private final Path outputDir;

    public FixedLengthDeliveryFileWriter(@Value("${delivery.file.output-dir}") String outputDir) {
        this.outputDir = Path.of(outputDir);
    }

    @Override
    public Path write(String courierCode, IssueType issueType, String batchId,
                      List<DeliveryRequest> items) {
        try {
            Files.createDirectories(outputDir);
            // 파일명 자체가 멱등성 키: 배송업체_유형_배치ID.dat
            Path file = outputDir.resolve(
                    "%s_%s_%s.dat".formatted(courierCode, issueType, batchId));

            StringBuilder sb = new StringBuilder();
            for (DeliveryRequest item : items) {
                sb.append(toLine(item.getCardIssue())).append('\n');
            }
            sb.append(trailer(items.size())).append('\n');

            Files.writeString(file, sb.toString(), StandardCharsets.UTF_8);
            return file;
        } catch (IOException e) {
            throw new UncheckedIOException("배송요청 파일 생성 실패", e);
        }
    }

    private String toLine(CardIssue issue) {
        return padRight(issue.getIssueType().name(), 3)
                + padRight(issue.getMaskedCardNo(), 19)
                + padRight(issue.getHolderName(), 10)
                + padRight(issue.getAddress(), 100);
    }

    private String trailer(int count) {
        return "T" + String.format("%09d", count);
    }

    static String padRight(String value, int length) {
        String v = value == null ? "" : value;
        if (v.length() > length) {
            return v.substring(0, length);
        }
        return v + " ".repeat(length - v.length());
    }
}
