package com.smartcampus.smartcampus.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Entity @Table(name = "course_schedule")
public class CourseSchedule {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "course_id", nullable = false) private Long courseId;
    @Column(name = "teacher_id", nullable = false) private Long teacherId;
    @Column(nullable = false, length = 20) private String semester;
    @Column(name = "class_name", length = 50) private String className;
    @Column(nullable = false) private Integer weekday;
    @Column(name = "section_start", nullable = false) private Integer sectionStart;
    @Column(name = "section_end", nullable = false) private Integer sectionEnd;
    @Column(length = 50) private String classroom;
    @Column(length = 50) private String campus;
    @Column(length = 50) private String weeks;
    @Column(name = "max_students") private Integer maxStudents;
    @Column(name = "current_students") private Integer currentStudents;
    @Column(length = 20) private String status;
    @Column(name = "created_time", nullable = false, updatable = false) private LocalDateTime createdTime;
    @Column(name = "updated_time", nullable = false) private LocalDateTime updatedTime;
    @PrePersist void onCreate() { createdTime = updatedTime = LocalDateTime.now(); }
    @PreUpdate void onUpdate() { updatedTime = LocalDateTime.now(); }

    @Transient private String courseName;
    @Transient private String teacherName;
}
