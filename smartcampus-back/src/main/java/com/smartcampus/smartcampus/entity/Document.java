package com.smartcampus.smartcampus.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Entity @Table(name = "document")
public class Document {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 200) private String title;
    @Column(columnDefinition = "TEXT") private String content;
    @Column(name = "document_type", nullable = false, length = 10) private String documentType;
    @Column(name = "submitter_id", nullable = false) private Long submitterId;
    @Column(name = "submitter_name", length = 50) private String submitterName;
    @Column(length = 100) private String department;
    @Column(length = 20) private String status;
    @Column(name = "approver_id") private Long approverId;
    @Column(name = "approver_name", length = 50) private String approverName;
    @Column(name = "approval_comment", length = 500) private String approvalComment;
    @Column(name = "submit_time") private LocalDateTime submitTime;
    @Column(name = "approval_time") private LocalDateTime approvalTime;
    @Column(name = "created_time", nullable = false, updatable = false) private LocalDateTime createdTime;
    @Column(name = "updated_time", nullable = false) private LocalDateTime updatedTime;
    @PrePersist void onCreate() { createdTime = updatedTime = LocalDateTime.now(); }
    @PreUpdate void onUpdate() { updatedTime = LocalDateTime.now(); }
}
