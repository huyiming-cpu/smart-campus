package com.smartcampus.smartcampus.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Entity @Table(name = "login_log")
public class LoginLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id") private Long userId;
    @Column(name = "identity_number", length = 20) private String identityNumber;
    @Column(name = "login_type", length = 10) private String loginType;
    @Column(name = "ip_address", length = 50) private String ipAddress;
    @Column(length = 100) private String location;
    @Column(name = "device_info", length = 255) private String deviceInfo;
    @Column(name = "login_time") private LocalDateTime loginTime;
    @Column(length = 10) private String status;
    @Column(name = "fail_reason", length = 100) private String failReason;
    @PrePersist void onCreate() { loginTime = LocalDateTime.now(); }
}
