package com.smartcampus.smartcampus.repository;

import com.smartcampus.smartcampus.entity.AiKnowledgeBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AiKnowledgeBaseRepository extends JpaRepository<AiKnowledgeBase, Long> {
    @Query("SELECT a FROM AiKnowledgeBase a WHERE a.status = 1 AND (a.roleType = :roleType OR a.roleType = 'ALL') ORDER BY a.sortOrder")
    List<AiKnowledgeBase> findByRoleType(String roleType);
}
