package com.smartcampus.smartcampus.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Entity @Table(name = "school_calendar_event")
public class SchoolCalendarEvent {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;

    @Column(name = "event_title", nullable = false, length = 100)
    private String eventTitle;

    @Column(name = "event_type", nullable = false, length = 20)
    private String eventType;  // SEMESTER_START, SEMESTER_END, HOLIDAY, EXAM_WEEK, ACTIVITY, MAKEUP, VACATION

    @Column(nullable = false, length = 20)
    private String semester;   // "2025-2026-2"

    @Column(length = 500)
    private String description;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "created_time", nullable = false, updatable = false)
    private LocalDateTime createdTime;
    @Column(name = "updated_time", nullable = false)
    private LocalDateTime updatedTime;

    @PrePersist void onCreate() { createdTime = updatedTime = LocalDateTime.now(); }
    @PreUpdate void onUpdate() { updatedTime = LocalDateTime.now(); }
}
