package com.smartcampus.smartcampus.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_profile")
public class UserProfile {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(length = 100)
    private String department;

    @Column(length = 100)
    private String major;

    @Column(length = 20)
    private String grade;

    @Column(name = "class_name", length = 50)
    private String className;

    @Column(length = 50)
    private String title;

    @Column(name = "id_card", length = 255)
    private String idCard;

    @Column(length = 255)
    private String address;

    @Column(name = "emergency_contact", length = 100)
    private String emergencyContact;

    @Column(name = "emergency_phone", length = 15)
    private String emergencyPhone;

    @Column(name = "enrollment_date")
    private LocalDateTime enrollmentDate;

    @Column(name = "graduation_date")
    private LocalDateTime graduationDate;

    @Column(name = "created_time", nullable = false, updatable = false)
    private LocalDateTime createdTime;

    @Column(name = "updated_time", nullable = false)
    private LocalDateTime updatedTime;

    @PrePersist void onCreate() { createdTime = updatedTime = LocalDateTime.now(); }
    @PreUpdate  void onUpdate()  { updatedTime = LocalDateTime.now(); }
}
