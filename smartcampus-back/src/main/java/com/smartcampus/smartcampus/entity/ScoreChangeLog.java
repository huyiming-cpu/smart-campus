package com.smartcampus.smartcampus.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Entity @Table(name = "score_change_log")
public class ScoreChangeLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "student_course_id", nullable = false) private Long studentCourseId;
    @Column(name = "old_score", precision = 5, scale = 2) private BigDecimal oldScore;
    @Column(name = "new_score", nullable = false, precision = 5, scale = 2) private BigDecimal newScore;
    @Column(name = "change_reason", length = 255) private String changeReason;
    @Column(name = "operator_id", nullable = false) private Long operatorId;
    @Column(name = "operator_name", length = 50) private String operatorName;
    @Column(name = "created_time", nullable = false, updatable = false) private LocalDateTime createdTime;
    @PrePersist void onCreate() { createdTime = LocalDateTime.now(); }
}
