package com.smartcampus.smartcampus.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Entity @Table(name = "work_plan")
public class WorkPlan {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id", nullable = false) private Long userId;
    @Column(name = "user_name", length = 50) private String userName;
    @Column(nullable = false, length = 200) private String title;
    @Column(columnDefinition = "TEXT") private String content;
    @Column(name = "plan_date", nullable = false) private LocalDate planDate;
    @Column(name = "plan_type", length = 20) private String planType;
    @Column(length = 100) private String department;
    @Column(length = 10) private String priority;
    @Column(length = 20) private String status;
    @Column(name = "finish_time") private LocalDateTime finishTime;
    @Column(name = "created_time", nullable = false, updatable = false) private LocalDateTime createdTime;
    @Column(name = "updated_time", nullable = false) private LocalDateTime updatedTime;
    @PrePersist void onCreate() { createdTime = updatedTime = LocalDateTime.now(); }
    @PreUpdate void onUpdate() { updatedTime = LocalDateTime.now(); }
}
