package com.smartcampus.smartcampus.repository;

import com.smartcampus.smartcampus.entity.ScientificResearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ScientificResearchRepository extends JpaRepository<ScientificResearch, Long> {
    List<ScientificResearch> findByTeacherIdOrderByStartDateDesc(Long teacherId);
    List<ScientificResearch> findByStatusOrderByStartDateDesc(String status);
}
