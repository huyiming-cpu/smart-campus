package com.smartcampus.smartcampus.repository;

import com.smartcampus.smartcampus.entity.AiConversationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AiConversationHistoryRepository extends JpaRepository<AiConversationHistory, Long> {
    List<AiConversationHistory> findByUserIdAndSessionIdOrderByCreatedTimeAsc(Long userId, String sessionId);
    List<AiConversationHistory> findByUserIdOrderByCreatedTimeDesc(Long userId);
}
