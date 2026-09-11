package com.poc.carddelivery.infrastructure.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.poc.carddelivery.application.FileTransferer;
import com.poc.carddelivery.domain.courier.Courier;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * JSch(mwiede fork) 기반 SFTP 업로더.
 *
 * 업로드 전략: 임시 파일명(.part)으로 올린 뒤 rename.
 * 수신측이 "쓰다 만 파일"을 집어가는 고전적인 대외계 사고를 방지한다.
 */
@Slf4j
@Component
public class JschSftpUploader implements FileTransferer {

    private static final int CONNECT_TIMEOUT_MS = 10_000;

    @Override
    public void upload(Courier courier, Path file) {
        Session session = null;
        ChannelSftp channel = null;
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(courier.getUsername(), courier.getHost(), courier.getPort());
            session.setPassword(courier.getPassword());
            // PoC: 호스트키 검증 생략. 실전은 known_hosts 등록 필수.
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(CONNECT_TIMEOUT_MS);

            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect(CONNECT_TIMEOUT_MS);

            String fileName = file.getFileName().toString();
            String remoteTmp = courier.getRemoteDir() + "/" + fileName + ".part";
            String remoteFinal = courier.getRemoteDir() + "/" + fileName;

            channel.put(file.toAbsolutePath().toString(), remoteTmp);
            channel.rename(remoteTmp, remoteFinal);

            log.info("[{}] SFTP 업로드 완료: {}", courier.getCode(), remoteFinal);
        } catch (JSchException | SftpException e) {
            throw new IllegalStateException(
                    "[%s] SFTP 업로드 실패: %s".formatted(courier.getCode(), file.getFileName()), e);
        } finally {
            if (channel != null) channel.disconnect();
            if (session != null) session.disconnect();
        }
    }
}
