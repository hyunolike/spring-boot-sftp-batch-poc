package com.poc.carddelivery.application;

import java.nio.file.Path;

/** 파일 암호화 포트 (구현: infrastructure.crypto) */
public interface FileEncryptor {

    /** 평문 파일을 암호화한 새 파일 경로를 반환한다. */
    Path encrypt(Path plainFile);
}
