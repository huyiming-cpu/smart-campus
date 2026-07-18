package com.smartcampus.smartcampus.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Entity @Table(name = "announcement")
public class Announcement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 200) private String title;
    @Column(columnDefinition = "LONGTEXT") private String content;
    @Column(name = "publisher_id", nullable = false) private Long publisherId;
    @Column(name = "publisher_name", length = 50) private String publisherName;
    @Column(name = "target_roles", length = 100) private String targetRoles;
    @Column(name = "is_top") private Integer isTop;
    @Column(name = "is_urgent") private Integer isUrgent;
    @Column(name = "start_time") private LocalDateTime startTime;
    @Column(name = "end_time") private LocalDateTime endTime;
    @Column(name = "view_count") private Integer viewCount;
    @Column(length = 20) private String status;
    @Column(name = "created_time", nullable = false, updatable = false) private LocalDateTime createdTime;
    @Column(name = "updated_time", nullable = false) private LocalDateTime updatedTime;
    @PrePersist void onCreate() { createdTime = updatedTime = LocalDateTime.now(); }
    @PreUpdate void onUpdate() { updatedTime = LocalDateTime.now(); }
}
