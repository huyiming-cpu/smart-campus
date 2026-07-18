package com.smartcampus.smartcampus.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Entity @Table(name = "student_retake")
public class StudentRetake {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "student_id", nullable = false) private Long studentId;
    @Column(name = "course_id", nullable = false) private Long courseId;
    @Column(nullable = false, length = 20) private String semester;
    @Column(nullable = false, length = 10) private String type;
    @Column(name = "original_score", precision = 5, scale = 2) private BigDecimal originalScore;
    @Column(length = 20) private String status;
    @Column(name = "application_time") private LocalDateTime applicationTime;
    @Column(name = "approval_time") private LocalDateTime approvalTime;
    @Column(name = "payment_status") private Integer paymentStatus;
    @Column(name = "created_time", nullable = false, updatable = false) private LocalDateTime createdTime;
    @Column(name = "updated_time", nullable = false) private LocalDateTime updatedTime;
    @PrePersist void onCreate() { createdTime = updatedTime = LocalDateTime.now(); }
    @PreUpdate void onUpdate() { updatedTime = LocalDateTime.now(); }
}
