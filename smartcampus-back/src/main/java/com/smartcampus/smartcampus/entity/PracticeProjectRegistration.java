package com.smartcampus.smartcampus.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Entity @Table(name = "practice_project_registration")
public class PracticeProjectRegistration {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "project_name", nullable = false, length = 100) private String projectName;
    @Column(name = "student_id", nullable = false) private Long studentId;
    @Column(name = "teacher_id") private Long teacherId;
    @Column(name = "project_date") private LocalDate projectDate;
    private Integer duration;
    @Column(name = "lab_location", length = 100) private String labLocation;
    @Column(length = 20) private String status;
    @Column(name = "registration_time") private LocalDateTime registrationTime;
    @Column(name = "created_time", nullable = false, updatable = false) private LocalDateTime createdTime;
    @Column(name = "updated_time", nullable = false) private LocalDateTime updatedTime;
    @PrePersist void onCreate() { createdTime = updatedTime = LocalDateTime.now(); }
    @PreUpdate void onUpdate() { updatedTime = LocalDateTime.now(); }
}
