package com.poc.carddelivery.application;

import com.poc.carddelivery.domain.delivery.DeliveryRequest;
import com.poc.carddelivery.domain.delivery.IssueType;
import java.nio.file.Path;
import java.util.List;

/** 배송요청 파일 생성 포트 (구현: infrastructure.file) */
public interface DeliveryFileWriter {

    Path write(String courierCode, IssueType issueType, String batchId, List<DeliveryRequest> items);
}
