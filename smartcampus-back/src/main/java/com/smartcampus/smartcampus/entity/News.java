package com.smartcampus.smartcampus.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Entity @Table(name = "news")
public class News {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 200) private String title;
    @Column(length = 500) private String summary;
    @Column(columnDefinition = "LONGTEXT") private String content;
    @Column(name = "cover_image", length = 255) private String coverImage;
    @Column(length = 50) private String category;
    @Column(length = 100) private String source;
    @Column(name = "publisher_id", nullable = false) private Long publisherId;
    @Column(name = "publisher_name", length = 50) private String publisherName;
    @Column(name = "view_count") private Integer viewCount;
    @Column(name = "like_count") private Integer likeCount;
    @Column(length = 20) private String status;
    @Column(name = "publish_time") private LocalDateTime publishTime;
    @Column(name = "created_time", nullable = false, updatable = false) private LocalDateTime createdTime;
    @Column(name = "updated_time", nullable = false) private LocalDateTime updatedTime;
    @PrePersist void onCreate() { createdTime = updatedTime = LocalDateTime.now(); }
    @PreUpdate void onUpdate() { updatedTime = LocalDateTime.now(); }
}
