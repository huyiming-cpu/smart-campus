package com.smartcampus.smartcampus.repository;

import com.smartcampus.smartcampus.entity.LevelExamRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LevelExamRegistrationRepository extends JpaRepository<LevelExamRegistration, Long> {
    List<LevelExamRegistration> findByStudentId(Long studentId);
    List<LevelExamRegistration> findByExamId(Long examId);
}
