package com.smartcampus.smartcampus.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Entity @Table(name = "level_exam_registration")
public class LevelExamRegistration {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "exam_id", nullable = false) private Long examId;
    @Column(name = "student_id", nullable = false) private Long studentId;
    @Column(name = "registration_time") private LocalDateTime registrationTime;
    @Column(name = "payment_status") private Integer paymentStatus;
    @Column(name = "exam_status", length = 20) private String examStatus;
    @Column(precision = 5, scale = 2) private BigDecimal score;
    @Column(name = "is_passed") private Integer isPassed;
    @Column(name = "created_time", nullable = false, updatable = false) private LocalDateTime createdTime;
    @Column(name = "updated_time", nullable = false) private LocalDateTime updatedTime;
    @PrePersist void onCreate() { createdTime = updatedTime = LocalDateTime.now(); }
    @PreUpdate void onUpdate() { updatedTime = LocalDateTime.now(); }
}
