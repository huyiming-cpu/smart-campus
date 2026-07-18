package com.smartcampus.smartcampus.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Entity @Table(name = "role_permission")
public class RolePermission {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "role_id", nullable = false) private Long roleId;
    @Column(name = "permission_id", nullable = false) private Long permissionId;
    @Column(name = "created_time", nullable = false, updatable = false) private LocalDateTime createdTime;
    @PrePersist void onCreate() { createdTime = LocalDateTime.now(); }
}
