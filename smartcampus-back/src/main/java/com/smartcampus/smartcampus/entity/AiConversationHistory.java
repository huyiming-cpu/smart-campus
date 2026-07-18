package com.smartcampus.smartcampus.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Entity @Table(name = "ai_conversation_history")
public class AiConversationHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id", nullable = false) private Long userId;
    @Column(name = "session_id", length = 50) private String sessionId;
    @Column(nullable = false, columnDefinition = "TEXT") private String question;
    @Column(nullable = false, columnDefinition = "TEXT") private String answer;
    @Column(name = "intent_type", length = 50) private String intentType;
    @Column(name = "matched_function_code", length = 50) private String matchedFunctionCode;
    @Column(name = "is_helpful") private Integer isHelpful;
    @Column(name = "response_time") private Integer responseTime;
    @Column(name = "created_time", nullable = false, updatable = false) private LocalDateTime createdTime;
    @PrePersist void onCreate() { createdTime = LocalDateTime.now(); }
}
