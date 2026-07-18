package com.smartcampus.smartcampus.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "identity_registry")
public class IdentityRegistry {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "identity_number", nullable = false, unique = true, length = 20)
    private String identityNumber;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "id_card_last6", nullable = false, length = 6)
    private String idCardLast6;

    @Column(name = "role_id", nullable = false)
    private Long roleId;

    @Column(length = 100)
    private String department;

    @Column(length = 100)
    private String major;

    @Column(length = 20)
    private String grade;

    @Column(name = "class_name", length = 50)
    private String className;

    @Column(length = 15)
    private String phone;

    @Column(nullable = false)
    private Integer status;

    @Column(name = "created_time", nullable = false, updatable = false)
    private LocalDateTime createdTime;

    @Column(name = "updated_time", nullable = false)
    private LocalDateTime updatedTime;

    @PrePersist void onCreate() { createdTime = updatedTime = LocalDateTime.now(); }
    @PreUpdate  void onUpdate()  { updatedTime = LocalDateTime.now(); }
}
