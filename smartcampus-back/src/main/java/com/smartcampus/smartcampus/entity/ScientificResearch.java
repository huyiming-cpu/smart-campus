package com.smartcampus.smartcampus.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Entity @Table(name = "scientific_research")
public class ScientificResearch {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "teacher_id", nullable = false) private Long teacherId;
    @Column(name = "project_name", nullable = false, length = 200) private String projectName;
    @Column(name = "project_code", length = 50) private String projectCode;
    @Column(name = "project_type", length = 20) private String projectType;
    @Column(name = "fund_amount", precision = 12, scale = 2) private BigDecimal fundAmount;
    @Column(name = "start_date") private LocalDate startDate;
    @Column(name = "end_date") private LocalDate endDate;
    @Column(length = 20) private String status;
    @Column(length = 500) private String members;
    @Column(columnDefinition = "TEXT") private String achievements;
    @Column(name = "created_time", nullable = false, updatable = false) private LocalDateTime createdTime;
    @Column(name = "updated_time", nullable = false) private LocalDateTime updatedTime;
    @PrePersist void onCreate() { createdTime = updatedTime = LocalDateTime.now(); }
    @PreUpdate void onUpdate() { updatedTime = LocalDateTime.now(); }
}
