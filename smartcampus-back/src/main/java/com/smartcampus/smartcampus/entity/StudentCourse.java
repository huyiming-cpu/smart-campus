package com.smartcampus.smartcampus.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Entity @Table(name = "student_course")
public class StudentCourse {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "student_id", nullable = false) private Long studentId;
    @Column(name = "schedule_id", nullable = false) private Long scheduleId;
    @Column(nullable = false, length = 20) private String semester;
    @Column(length = 20) private String status;
    @Column(precision = 5, scale = 2) private BigDecimal score;
    @Column(precision = 3, scale = 2) private BigDecimal gpa;
    @Column(name = "grade_level", length = 10) private String gradeLevel;
    @Column(name = "exam_type", length = 10) private String examType;
    @Column(name = "is_retake") private Integer isRetake;
    @Column(name = "is_makeup") private Integer isMakeup;
    @Column(name = "select_time") private LocalDateTime selectTime;
    @Column(name = "drop_time") private LocalDateTime dropTime;
    @Column(name = "created_time", nullable = false, updatable = false) private LocalDateTime createdTime;
    @Column(name = "updated_time", nullable = false) private LocalDateTime updatedTime;
    @PrePersist void onCreate() { createdTime = updatedTime = LocalDateTime.now(); }
    @PreUpdate void onUpdate() { updatedTime = LocalDateTime.now(); }
}
