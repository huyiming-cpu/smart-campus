package com.smartcampus.smartcampus.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Entity @Table(name = "competition_registration")
public class CompetitionRegistration {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "competition_name", nullable = false, length = 100) private String competitionName;
    @Column(name = "student_id", nullable = false) private Long studentId;
    @Column(name = "team_name", length = 100) private String teamName;
    @Column(length = 500) private String teammates;
    @Column(length = 50) private String advisor;
    @Column(length = 20) private String status;
    @Column(name = "registration_time") private LocalDateTime registrationTime;
    @Column(name = "created_time", nullable = false, updatable = false) private LocalDateTime createdTime;
    @Column(name = "updated_time", nullable = false) private LocalDateTime updatedTime;
    @PrePersist void onCreate() { createdTime = updatedTime = LocalDateTime.now(); }
    @PreUpdate void onUpdate() { updatedTime = LocalDateTime.now(); }
}
