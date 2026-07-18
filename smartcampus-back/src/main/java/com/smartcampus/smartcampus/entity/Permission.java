package com.smartcampus.smartcampus.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Entity @Table(name = "permission")
public class Permission {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "parent_id") private Long parentId;
    @Column(name = "permission_name", nullable = false, length = 50) private String permissionName;
    @Column(name = "permission_code", nullable = false, unique = true, length = 100) private String permissionCode;
    @Column(name = "permission_type", length = 10) private String permissionType;
    @Column(length = 255) private String url;
    @Column(length = 50) private String icon;
    @Column(name = "sort_order") private Integer sortOrder;
    private Integer status;
    @Column(name = "created_time", nullable = false, updatable = false) private LocalDateTime createdTime;
    @PrePersist void onCreate() { createdTime = LocalDateTime.now(); }
}
