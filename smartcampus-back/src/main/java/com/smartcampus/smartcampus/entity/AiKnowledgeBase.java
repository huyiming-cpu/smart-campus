package com.smartcampus.smartcampus.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Entity @Table(name = "ai_knowledge_base")
public class AiKnowledgeBase {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "role_type", length = 50) private String roleType;
    @Column(name = "intent_type", nullable = false, length = 20) private String intentType;
    @Column(name = "question_keywords", nullable = false, length = 500) private String questionKeywords;
    @Column(name = "question_patterns", length = 500) private String questionPatterns;
    @Column(name = "function_code", length = 50) private String functionCode;
    @Column(name = "answer_template", nullable = false, columnDefinition = "TEXT") private String answerTemplate;
    @Column(name = "redirect_url", length = 255) private String redirectUrl;
    @Column(name = "sort_order") private Integer sortOrder;
    private Integer status;
    @Column(name = "hit_count") private Integer hitCount;
    @Column(name = "created_time", nullable = false, updatable = false) private LocalDateTime createdTime;
    @Column(name = "updated_time", nullable = false) private LocalDateTime updatedTime;
    @PrePersist void onCreate() { createdTime = updatedTime = LocalDateTime.now(); }
    @PreUpdate void onUpdate() { updatedTime = LocalDateTime.now(); }
}
