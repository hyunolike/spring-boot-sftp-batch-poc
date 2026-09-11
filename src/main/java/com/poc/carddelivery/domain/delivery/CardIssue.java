package com.poc.carddelivery.domain.delivery;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 카드 발급 정보. 카드번호는 원장에서부터 마스킹된 형태만 보관한다는 가정.
 */
@Entity
@Table(name = "card_issue")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CardIssue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 마스킹된 카드번호 (예: 9410-12**-****-3456) */
    @Column(nullable = false, length = 19)
    private String maskedCardNo;

    @Column(nullable = false, length = 50)
    private String holderName;

    @Column(nullable = false, length = 200)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private IssueType issueType;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
