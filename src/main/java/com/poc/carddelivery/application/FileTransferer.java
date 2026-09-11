package com.poc.carddelivery.application;

import com.poc.carddelivery.domain.courier.Courier;
import java.nio.file.Path;

/** 파일 전송 포트 (구현: infrastructure.sftp — 추후 S3 등으로 교체 가능) */
public interface FileTransferer {

    void upload(Courier courier, Path file);
}
