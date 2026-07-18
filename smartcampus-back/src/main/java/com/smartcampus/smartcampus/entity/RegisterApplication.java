package com.smartcampus.smartcampus.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Entity @Table(name = "register_application")
public class RegisterApplication {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "identity_number", nullable = false, unique = true, length = 20) private String identityNumber;
    @Column(name = "id_card_last6", nullable = false, length = 255) private String idCardLast6;
    @Column(nullable = false, length = 50) private String name;
    @Column(name = "user_type", nullable = false, length = 10) private String userType;
    @Column(length = 100) private String department;
    @Column(length = 100) private String major;
    @Column(length = 15) private String phone;
    @Column(length = 100) private String email;
    @Column(name = "password_hash", nullable = false, length = 255) private String passwordHash;
    @Column(length = 20) private String status;
    @Column(name = "reject_reason", length = 255) private String rejectReason;
    @Column(name = "apply_time") private LocalDateTime applyTime;
    @Column(name = "audit_time") private LocalDateTime auditTime;
    @Column(name = "auditor_id") private Long auditorId;
    @Column(name = "created_time", nullable = false, updatable = false) private LocalDateTime createdTime;
    @Column(name = "updated_time", nullable = false) private LocalDateTime updatedTime;
    @PrePersist void onCreate() { createdTime = updatedTime = LocalDateTime.now(); }
    @PreUpdate void onUpdate() { updatedTime = LocalDateTime.now(); }
}
