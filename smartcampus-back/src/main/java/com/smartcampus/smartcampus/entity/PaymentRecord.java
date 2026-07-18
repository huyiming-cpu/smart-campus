package com.smartcampus.smartcampus.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Entity @Table(name = "payment_record")
public class PaymentRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "student_id", nullable = false) private Long studentId;
    @Column(nullable = false, length = 20) private String semester;
    @Column(name = "payment_type", nullable = false, length = 20) private String paymentType;
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2) private BigDecimal totalAmount;
    @Column(name = "paid_amount", precision = 10, scale = 2) private BigDecimal paidAmount;
    @Column(name = "unpaid_amount", precision = 10, scale = 2, insertable = false, updatable = false) private BigDecimal unpaidAmount;
    @Column(name = "payment_status", length = 10) private String paymentStatus;
    @Column(name = "payment_time") private LocalDateTime paymentTime;
    @Column(name = "payment_method", length = 50) private String paymentMethod;
    @Column(name = "payment_no", length = 50) private String paymentNo;
    @Column(length = 255) private String remark;
    @Column(name = "created_time", nullable = false, updatable = false) private LocalDateTime createdTime;
    @Column(name = "updated_time", nullable = false) private LocalDateTime updatedTime;
    @PrePersist void onCreate() { createdTime = updatedTime = LocalDateTime.now(); }
    @PreUpdate void onUpdate() { updatedTime = LocalDateTime.now(); }
}
