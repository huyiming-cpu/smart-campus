package com.smartcampus.smartcampus.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Entity @Table(name = "card_transaction")
public class CardTransaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id", nullable = false) private Long userId;
    @Column(name = "card_no", nullable = false, length = 20) private String cardNo;
    @Column(name = "transaction_type", nullable = false, length = 10) private String transactionType;
    @Column(nullable = false, precision = 10, scale = 2) private BigDecimal amount;
    @Column(nullable = false, precision = 10, scale = 2) private BigDecimal balance;
    @Column(length = 100) private String merchant;
    @Column(name = "transaction_time", nullable = false) private LocalDateTime transactionTime;
    @Column(name = "transaction_no", length = 50) private String transactionNo;
    @Column(name = "sync_status") private Integer syncStatus;
    @Column(name = "created_time", nullable = false, updatable = false) private LocalDateTime createdTime;
    @PrePersist void onCreate() { createdTime = LocalDateTime.now(); }
}
