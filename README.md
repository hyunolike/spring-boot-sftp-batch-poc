# card-delivery-batch

**SFTP 기반 대외계 배치 인터페이스 PoC** — 카드 배송 시스템 컨셉

카드사(내부)에서 배송업체(외부)로 배송요청 파일을 주기적으로 전송하는
File-based B2B Batch Integration 구조를 재현한다.

```
카드 발급 DB
 ↓ 스케줄러 (READY 상태 배송 대상 조회)
 ↓ 발급유형별 고정길이 파일 생성 (NEW / REN / RET)
 ↓ AES-256-GCM 암호화
 ↓ SFTP 업로드 (.part 업로드 후 rename)
 ↓ 상태 갱신 (READY → REQUESTED) + 배치 이력 기록
```

## 실행 방법

```bash
# 1. 로컬 배송업체 SFTP 서버 기동
docker compose up -d

# 2. 애플리케이션 실행 (기동 직후 배치 1회 + 매 분 스케줄)
# gradle wrapper가 없다면 최초 1회: gradle wrapper --gradle-version 8.10
./gradlew bootRun

# 3. 업로드 결과 확인
ls docker/sftp-data/
# CJX_NEW_20260911xxxxxx_CJX.dat.enc 등이 보이면 성공

# 4. H2 콘솔에서 상태/이력 확인
# http://localhost:8080/h2-console (jdbc:h2:mem:carddelivery / sa)
SELECT * FROM delivery_request;  -- status = REQUESTED 확인
SELECT * FROM job_history;
```

## 패키지 구조 (헥사고날 라이트)

```
com.poc.carddelivery
├── batch/
│   ├── scheduler/    # @Scheduled 트리거, 기동 시 1회 실행 러너
│   └── job/          # DeliveryJobRunner — 배송업체 순회, 업체 단위 예외 격리
├── domain/
│   ├── delivery/     # CardIssue, DeliveryRequest(상태 머신), JobHistory
│   └── courier/      # 배송업체 설정 (SFTP 접속정보)
├── application/      # DeliveryExportService + 포트 3종
│   ├── DeliveryFileWriter   (파일 생성 포트)
│   ├── FileEncryptor        (암호화 포트)
│   └── FileTransferer       (전송 포트 — SFTP → S3 교체 가능)
└── infrastructure/
    ├── file/         # 고정길이 파일 생성
    ├── crypto/       # AES-256-GCM
    ├── sftp/         # JSch(mwiede fork) 업로더
    └── persistence/  # (Spring Data JPA — 도메인 패키지의 Repository 인터페이스 사용)
```

## 설계 포인트

| 주제 | 선택 | 이유 |
|---|---|---|
| 멱등성 | 상태 기반 조회 (READY만 대상) + 트랜잭션 롤백 | 중간 실패 시 READY 유지 → 다음 배치가 자동 재시도. 이중 전송 방지 |
| 이력 기록 | `REQUIRES_NEW` 별도 트랜잭션 | 배치가 롤백돼도 "실패했다"는 이력은 남아야 함 |
| SFTP 업로드 | `.part` 업로드 후 rename | 수신측이 쓰다 만 파일을 집어가는 사고 방지 |
| 업체 격리 | 업체 단위 try-catch | A업체 장애가 B업체 전송을 막지 않음 |
| 암호화 | AES-256-GCM, IV 파일 선두 12바이트 | 무결성 검증(GCM 태그) 포함 |

## 알려진 단순화 (실전과의 차이)

- 고정길이 패딩이 **문자 수** 기준 — 실무 규격은 보통 EUC-KR **바이트** 기준
- AES 키가 application.yml 평문 — 실전은 KMS/Vault + 키 로테이션
- `StrictHostKeyChecking=no` — 실전은 known_hosts 등록 필수
- 전송 성공 ↔ 상태 갱신 사이 프로세스 다운 시나리오 미해결
  → 파일명 기준 원격 존재 확인 or 수신측 ACK 파일 설계가 다음 과제

## 로드맵

- [ ] 배송결과 회신 파일 수신 (inbound: REQUESTED → DELIVERED/RETURNED)
- [ ] Spring Batch로 리팩토링 후 비교 (JobRepository, chunk, retry)
- [ ] 바이트 기준 고정길이 패딩 (EUC-KR)
- [ ] Testcontainers 기반 SFTP 통합 테스트
