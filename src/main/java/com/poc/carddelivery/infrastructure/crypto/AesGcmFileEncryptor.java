package com.poc.carddelivery.infrastructure.crypto;

import com.poc.carddelivery.application.FileEncryptor;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * AES-256-GCM 파일 암호화.
 * 출력 포맷: [IV 12바이트][암호문+GCM 태그]
 *
 * 키는 PoC라 application.yml의 Base64 키를 사용.
 * 실전은 KMS/Vault + 키 로테이션 + 수신측 키 교환 규약이 필요하다.
 */
@Component
public class AesGcmFileEncryptor implements FileEncryptor {

    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH_BITS = 128;

    private final SecretKey secretKey;
    private final SecureRandom secureRandom = new SecureRandom();

    public AesGcmFileEncryptor(@Value("${delivery.crypto.key-base64}") String keyBase64) {
        byte[] keyBytes = Base64.getDecoder().decode(keyBase64);
        this.secretKey = new SecretKeySpec(keyBytes, "AES");
    }

    @Override
    public Path encrypt(Path plainFile) {
        try {
            byte[] iv = new byte[IV_LENGTH];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(TAG_LENGTH_BITS, iv));

            byte[] cipherText = cipher.doFinal(Files.readAllBytes(plainFile));

            byte[] out = new byte[IV_LENGTH + cipherText.length];
            System.arraycopy(iv, 0, out, 0, IV_LENGTH);
            System.arraycopy(cipherText, 0, out, IV_LENGTH, cipherText.length);

            Path encrypted = plainFile.resolveSibling(plainFile.getFileName() + ".enc");
            Files.write(encrypted, out);
            return encrypted;
        } catch (IOException e) {
            throw new UncheckedIOException("암호화 파일 입출력 실패", e);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("파일 암호화 실패", e);
        }
    }

    /** 수신측 검증/테스트용 복호화 */
    public byte[] decrypt(byte[] encrypted) {
        try {
            GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BITS, encrypted, 0, IV_LENGTH);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);
            return cipher.doFinal(encrypted, IV_LENGTH, encrypted.length - IV_LENGTH);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("파일 복호화 실패", e);
        }
    }
}
