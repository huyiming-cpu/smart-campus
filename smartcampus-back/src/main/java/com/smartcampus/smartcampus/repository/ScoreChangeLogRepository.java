package com.smartcampus.smartcampus.repository;

import com.smartcampus.smartcampus.entity.ScoreChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ScoreChangeLogRepository extends JpaRepository<ScoreChangeLog, Long> {
    List<ScoreChangeLog> findByStudentCourseId(Long studentCourseId);
}
