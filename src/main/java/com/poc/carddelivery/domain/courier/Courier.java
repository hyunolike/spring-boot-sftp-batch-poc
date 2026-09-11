package com.poc.carddelivery.domain.courier;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 배송업체(대외계) 설정.
 * 항공 IUR 구조의 Agency/PCC 에 대응한다.
 */
@Entity
@Table(name = "courier")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Courier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 배송업체 코드 (파일명/디렉터리 분기 기준) */
    @Column(nullable = false, unique = true, length = 10)
    private String code;

    @Column(nullable = false, length = 50)
    private String name;

    // --- SFTP 접속 정보 (PoC라 평문, 실전은 Vault/KMS) ---
    @Column(nullable = false)
    private String host;

    @Column(nullable = false)
    private int port;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    /** 원격 업로드 디렉터리 */
    @Column(nullable = false)
    private String remoteDir;

    @Column(nullable = false)
    private boolean active;
}
