package com.smartcampus.smartcampus.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Entity @Table(name = "graduation_design")
public class GraduationDesign {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "student_id", nullable = false) private Long studentId;
    @Column(name = "teacher_id", nullable = false) private Long teacherId;
    @Column(name = "topic_name", nullable = false, length = 200) private String topicName;
    @Column(name = "topic_description", columnDefinition = "TEXT") private String topicDescription;
    @Column(nullable = false, length = 20) private String semester;
    @Column(length = 20) private String status;
    @Column(name = "teacher_comment", columnDefinition = "TEXT") private String teacherComment;
    @Column(columnDefinition = "TEXT") private String announcement;
    @Column(name = "submit_time") private LocalDateTime submitTime;
    @Column(name = "defense_time") private LocalDateTime defenseTime;
    @Column(precision = 5, scale = 2) private BigDecimal score;
    @Column(name = "created_time", nullable = false, updatable = false) private LocalDateTime createdTime;
    @Column(name = "updated_time", nullable = false) private LocalDateTime updatedTime;
    @PrePersist void onCreate() { createdTime = updatedTime = LocalDateTime.now(); }
    @PreUpdate void onUpdate() { updatedTime = LocalDateTime.now(); }
}
