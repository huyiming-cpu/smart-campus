package com.smartcampus.smartcampus.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Entity @Table(name = "lab")
public class Lab {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "lab_name", nullable = false, length = 100) private String labName;
    @Column(name = "lab_code", nullable = false, unique = true, length = 20) private String labCode;
    @Column(length = 100) private String location;
    private Integer capacity;
    @Column(precision = 8, scale = 2) private BigDecimal area;
    @Column(name = "equipment_desc", columnDefinition = "TEXT") private String equipmentDesc;
    @Column(length = 100) private String department;
    @Column(name = "manager_id") private Long managerId;
    @Column(name = "manager_name", length = 50) private String managerName;
    @Column(name = "lab_type", length = 20) private String labType;
    @Column(length = 20) private String status;
    @Column(name = "open_time", length = 50) private String openTime;
    @Column(length = 255) private String remark;
    @Column(name = "created_time", nullable = false, updatable = false) private LocalDateTime createdTime;
    @Column(name = "updated_time", nullable = false) private LocalDateTime updatedTime;
    @PrePersist void onCreate() { createdTime = updatedTime = LocalDateTime.now(); }
    @PreUpdate void onUpdate() { updatedTime = LocalDateTime.now(); }
}
