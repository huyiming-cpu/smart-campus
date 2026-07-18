package com.smartcampus.smartcampus.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Entity @Table(name = "course")
public class Course {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "course_code", nullable = false, unique = true, length = 20) private String courseCode;
    @Column(name = "course_name", nullable = false, length = 100) private String courseName;
    @Column(name = "course_name_en", length = 200) private String courseNameEn;
    @Column(nullable = false, precision = 3, scale = 1) private BigDecimal credit;
    @Column(nullable = false) private Integer hours;
    @Column(name = "weekly_hours") private Integer weeklyHours;
    @Column(name = "course_type", length = 20) private String courseType;
    @Column(length = 100) private String department;
    @Column(columnDefinition = "TEXT") private String description;
    private Integer status;
    @Column(name = "created_time", nullable = false, updatable = false) private LocalDateTime createdTime;
    @Column(name = "updated_time", nullable = false) private LocalDateTime updatedTime;
    @PrePersist void onCreate() { createdTime = updatedTime = LocalDateTime.now(); }
    @PreUpdate void onUpdate() { updatedTime = LocalDateTime.now(); }
}
