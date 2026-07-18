package com.smartcampus.smartcampus.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Entity @Table(name = "student_application")
public class StudentApplication {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "student_id", nullable = false) private Long studentId;
    @Column(name = "application_type", nullable = false, length = 20) private String applicationType;
    @Column(nullable = false, length = 200) private String title;
    @Column(columnDefinition = "TEXT") private String content;
    @Column(length = 500) private String attachments;
    @Column(length = 20) private String semester;
    @Column(length = 20) private String status;
    @Column(name = "review_comment", length = 500) private String reviewComment;
    @Column(name = "submit_time") private LocalDateTime submitTime;
    @Column(name = "review_time") private LocalDateTime reviewTime;
    @Column(name = "reviewer_id") private Long reviewerId;
    @Column(name = "created_time", nullable = false, updatable = false) private LocalDateTime createdTime;
    @Column(name = "updated_time", nullable = false) private LocalDateTime updatedTime;
    @PrePersist void onCreate() { createdTime = updatedTime = LocalDateTime.now(); }
    @PreUpdate void onUpdate() { updatedTime = LocalDateTime.now(); }
}
