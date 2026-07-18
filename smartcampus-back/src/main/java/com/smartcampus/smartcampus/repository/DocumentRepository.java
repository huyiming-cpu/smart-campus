package com.smartcampus.smartcampus.repository;

import com.smartcampus.smartcampus.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findBySubmitterIdOrderBySubmitTimeDesc(Long submitterId);
    List<Document> findByApproverIdOrderBySubmitTimeDesc(Long approverId);
    List<Document> findByStatusOrderBySubmitTimeDesc(String status);
    List<Document> findByDepartmentOrderBySubmitTimeDesc(String department);
}
