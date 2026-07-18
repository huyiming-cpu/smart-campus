package com.smartcampus.smartcampus.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Entity @Table(name = "teaching_evaluation")
public class TeachingEvaluation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "student_id", nullable = false) private Long studentId;
    @Column(name = "teacher_id", nullable = false) private Long teacherId;
    @Column(name = "course_id", nullable = false) private Long courseId;
    @Column(nullable = false, length = 20) private String semester;
    @Column(name = "dimension1_score") private Integer dimension1Score;
    @Column(name = "dimension2_score") private Integer dimension2Score;
    @Column(name = "dimension3_score") private Integer dimension3Score;
    @Column(name = "dimension4_score") private Integer dimension4Score;
    @Column(name = "overall_score", precision = 3, scale = 2) private BigDecimal overallScore;
    @Column(columnDefinition = "TEXT") private String comment;
    @Column(name = "is_anonymous") private Integer isAnonymous;
    @Column(length = 20) private String status;
    @Column(name = "created_time", nullable = false, updatable = false) private LocalDateTime createdTime;
    @Column(name = "updated_time", nullable = false) private LocalDateTime updatedTime;
    @PrePersist void onCreate() { createdTime = updatedTime = LocalDateTime.now(); }
    @PreUpdate void onUpdate() { updatedTime = LocalDateTime.now(); }
}
