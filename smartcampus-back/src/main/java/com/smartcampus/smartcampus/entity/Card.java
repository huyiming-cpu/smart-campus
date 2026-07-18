package com.smartcampus.smartcampus.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Entity @Table(name = "card")
public class Card {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id", nullable = false) private Long userId;
    @Column(name = "card_no", nullable = false, unique = true, length = 20) private String cardNo;
    @Column(nullable = false, precision = 10, scale = 2) private BigDecimal balance;
    @Column(name = "card_status", length = 10) private String cardStatus;
    @Column(name = "issue_date") private LocalDate issueDate;
    @Column(name = "expire_date") private LocalDate expireDate;
    @Column(name = "daily_limit", precision = 10, scale = 2) private BigDecimal dailyLimit;
    @Column(name = "created_time", nullable = false, updatable = false) private LocalDateTime createdTime;
    @Column(name = "updated_time", nullable = false) private LocalDateTime updatedTime;
    @PrePersist void onCreate() { createdTime = updatedTime = LocalDateTime.now(); }
    @PreUpdate void onUpdate() { updatedTime = LocalDateTime.now(); }
}
