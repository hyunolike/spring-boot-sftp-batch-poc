package com.poc.carddelivery.infrastructure.crypto;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class AesGcmFileEncryptorTest {

    // 테스트 전용 32바이트 키
    private static final String TEST_KEY =
            "q83vESNFZ4mrze8SNFZ4iavN7xEjRWeJq83vESNFZ4k=";

    @Test
    void 암호화_후_복호화하면_원문과_같다(@TempDir Path tempDir) throws Exception {
        AesGcmFileEncryptor encryptor = new AesGcmFileEncryptor(TEST_KEY);
        Path plain = tempDir.resolve("plain.dat");
        String content = "NEW9410-12**-****-3456김민준     서울특별시 강남구";
        Files.writeString(plain, content, StandardCharsets.UTF_8);

        Path encrypted = encryptor.encrypt(plain);

        assertThat(encrypted.getFileName().toString()).endsWith(".enc");
        byte[] decrypted = encryptor.decrypt(Files.readAllBytes(encrypted));
        assertThat(new String(decrypted, StandardCharsets.UTF_8)).isEqualTo(content);
    }

    @Test
    void 같은_평문도_IV가_달라_암호문이_다르다(@TempDir Path tempDir) throws Exception {
        AesGcmFileEncryptor encryptor = new AesGcmFileEncryptor(TEST_KEY);
        Path plain = tempDir.resolve("plain.dat");
        Files.writeString(plain, "동일한 내용");

        byte[] first = Files.readAllBytes(encryptor.encrypt(plain));
        Files.delete(plain.resolveSibling("plain.dat.enc"));
        byte[] second = Files.readAllBytes(encryptor.encrypt(plain));

        assertThat(first).isNotEqualTo(second);
    }
}
