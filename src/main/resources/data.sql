-- 배송업체 (로컬 도커 SFTP 서버)
INSERT INTO courier (code, name, host, port, username, password, remote_dir, active)
VALUES ('CJX', '씨제이엑스배송', 'localhost', 2222, 'courier', 'courierpass', 'upload', true);

INSERT INTO courier (code, name, host, port, username, password, remote_dir, active)
VALUES ('HNJ', '한진퀵배송', 'localhost', 2222, 'courier', 'courierpass', 'upload', true);

-- 카드 발급 데이터 (마스킹된 카드번호만 보관한다는 가정)
INSERT INTO card_issue (masked_card_no, holder_name, address, issue_type, created_at)
VALUES ('9410-12**-****-3456', '김민준', '서울특별시 강남구 테헤란로 123, 45동 678호', 'NEW', CURRENT_TIMESTAMP);
INSERT INTO card_issue (masked_card_no, holder_name, address, issue_type, created_at)
VALUES ('9410-34**-****-7890', '이서연', '경기도 성남시 분당구 판교역로 100', 'NEW', CURRENT_TIMESTAMP);
INSERT INTO card_issue (masked_card_no, holder_name, address, issue_type, created_at)
VALUES ('9410-56**-****-1234', '박도윤', '부산광역시 해운대구 센텀중앙로 55', 'REN', CURRENT_TIMESTAMP);
INSERT INTO card_issue (masked_card_no, holder_name, address, issue_type, created_at)
VALUES ('9410-78**-****-5678', '최하은', '대전광역시 유성구 대학로 99', 'RET', CURRENT_TIMESTAMP);
INSERT INTO card_issue (masked_card_no, holder_name, address, issue_type, created_at)
VALUES ('9410-90**-****-9012', '정지호', '인천광역시 연수구 송도과학로 32', 'NEW', CURRENT_TIMESTAMP);

-- 배송 요청 (READY = 전송 대상)
INSERT INTO delivery_request (card_issue_id, courier_code, status) VALUES (1, 'CJX', 'READY');
INSERT INTO delivery_request (card_issue_id, courier_code, status) VALUES (2, 'CJX', 'READY');
INSERT INTO delivery_request (card_issue_id, courier_code, status) VALUES (3, 'CJX', 'READY');
INSERT INTO delivery_request (card_issue_id, courier_code, status) VALUES (4, 'HNJ', 'READY');
INSERT INTO delivery_request (card_issue_id, courier_code, status) VALUES (5, 'HNJ', 'READY');
