package com.smartcampus.smartcampus.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Entity @Table(name = "level_exam")
public class LevelExam {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "exam_name", nullable = false, length = 100) private String examName;
    @Column(name = "exam_code", unique = true, length = 20) private String examCode;
    @Column(length = 50) private String category;
    @Column(length = 20) private String level;
    @Column(name = "registration_start") private LocalDate registrationStart;
    @Column(name = "registration_end") private LocalDate registrationEnd;
    @Column(name = "exam_date") private LocalDate examDate;
    @Column(precision = 10, scale = 2) private BigDecimal fee;
    @Column(columnDefinition = "TEXT") private String description;
    @Column(length = 20) private String status;
    @Column(name = "created_time", nullable = false, updatable = false) private LocalDateTime createdTime;
    @Column(name = "updated_time", nullable = false) private LocalDateTime updatedTime;
    @PrePersist void onCreate() { createdTime = updatedTime = LocalDateTime.now(); }
    @PreUpdate void onUpdate() { updatedTime = LocalDateTime.now(); }
}
