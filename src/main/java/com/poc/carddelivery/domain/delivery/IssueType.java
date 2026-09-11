package com.poc.carddelivery.domain.delivery;

/**
 * 발급 유형. IUR의 AIR/TSF/REF/EMD 처럼 파일 종류 분기 기준이 된다.
 */
public enum IssueType {
    NEW("신규발급"),
    REN("재발급"),
    RET("반송 재배송");

    private final String description;

    IssueType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
