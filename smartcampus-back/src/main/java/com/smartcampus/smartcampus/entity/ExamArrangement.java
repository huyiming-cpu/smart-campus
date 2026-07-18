package com.smartcampus.smartcampus.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data @Entity @Table(name = "exam_arrangement")
public class ExamArrangement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "course_id", nullable = false) private Long courseId;
    @Column(name = "course_name", length = 100) private String courseName;
    @Column(nullable = false, length = 20) private String semester;
    @Column(name = "exam_name", nullable = false, length = 100) private String examName;
    @Column(name = "exam_date", nullable = false) private LocalDate examDate;
    @Column(name = "exam_time_start", nullable = false) private LocalTime examTimeStart;
    @Column(name = "exam_time_end", nullable = false) private LocalTime examTimeEnd;
    @Column(nullable = false, length = 100) private String location;
    @Column(name = "seat_number", length = 20) private String seatNumber;
    @Column(name = "supervisor_id") private Long supervisorId;
    @Column(name = "supervisor_name", length = 50) private String supervisorName;
    @Column(name = "student_count") private Integer studentCount;
    @Column(length = 20) private String status;
    @Column(name = "created_time", nullable = false, updatable = false) private LocalDateTime createdTime;
    @Column(name = "updated_time", nullable = false) private LocalDateTime updatedTime;
    @PrePersist void onCreate() { createdTime = updatedTime = LocalDateTime.now(); }
    @PreUpdate void onUpdate() { updatedTime = LocalDateTime.now(); }
}
