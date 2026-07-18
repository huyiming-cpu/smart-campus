package com.smartcampus.smartcampus.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Entity @Table(name = "sys_config")
public class SysConfig {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "config_key", nullable = false, unique = true, length = 100) private String configKey;
    @Column(name = "config_value", nullable = false, length = 500) private String configValue;
    @Column(name = "config_desc", length = 255) private String configDesc;
    @Column(name = "config_type", length = 10) private String configType;
    private Integer status;
    @Column(name = "updated_time", nullable = false) private LocalDateTime updatedTime;
    @Column(name = "updated_by") private Long updatedBy;
    @PrePersist @PreUpdate void onSave() { updatedTime = LocalDateTime.now(); }
}
