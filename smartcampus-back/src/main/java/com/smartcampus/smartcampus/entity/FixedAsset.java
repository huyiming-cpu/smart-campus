package com.smartcampus.smartcampus.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Entity @Table(name = "fixed_asset")
public class FixedAsset {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "asset_name", nullable = false, length = 100) private String assetName;
    @Column(name = "asset_no", nullable = false, unique = true, length = 50) private String assetNo;
    @Column(name = "asset_type", nullable = false, length = 20) private String assetType;
    @Column(length = 100) private String model;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal price;
    @Column(name = "purchase_date") private LocalDate purchaseDate;
    @Column(length = 100) private String department;
    @Column(name = "user_id") private Long userId;
    @Column(name = "user_name", length = 50) private String userName;
    @Column(length = 100) private String location;
    @Column(name = "lab_id") private Long labId;
    @Column(length = 20) private String status;
    @Column(name = "scrap_date") private LocalDate scrapDate;
    @Column(length = 255) private String remark;
    @Column(name = "created_time", nullable = false, updatable = false) private LocalDateTime createdTime;
    @Column(name = "updated_time", nullable = false) private LocalDateTime updatedTime;
    @PrePersist void onCreate() { createdTime = updatedTime = LocalDateTime.now(); }
    @PreUpdate void onUpdate() { updatedTime = LocalDateTime.now(); }
}
