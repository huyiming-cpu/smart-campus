package com.smartcampus.smartcampus.repository;

import com.smartcampus.smartcampus.entity.LevelExam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LevelExamRepository extends JpaRepository<LevelExam, Long> {
    List<LevelExam> findByStatus(String status);
}
