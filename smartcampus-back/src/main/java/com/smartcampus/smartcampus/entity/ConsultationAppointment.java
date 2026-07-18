package com.smartcampus.smartcampus.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Entity @Table(name = "consultation_appointment")
public class ConsultationAppointment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "student_id", nullable = false) private Long studentId;
    @Column(name = "consultant_name", nullable = false, length = 50) private String consultantName;
    @Column(name = "appointment_date", nullable = false) private LocalDate appointmentDate;
    @Column(name = "appointment_time", nullable = false, length = 20) private String appointmentTime;
    @Column(length = 100) private String topic;
    @Column(length = 20) private String status;
    @Column(columnDefinition = "TEXT") private String feedback;
    @Column(name = "created_time", nullable = false, updatable = false) private LocalDateTime createdTime;
    @Column(name = "updated_time", nullable = false) private LocalDateTime updatedTime;
    @PrePersist void onCreate() { createdTime = updatedTime = LocalDateTime.now(); }
    @PreUpdate void onUpdate() { updatedTime = LocalDateTime.now(); }
}
