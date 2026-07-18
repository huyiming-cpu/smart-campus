package com.smartcampus.smartcampus.repository;

import com.smartcampus.smartcampus.entity.PracticeProjectRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PracticeProjectRegistrationRepository extends JpaRepository<PracticeProjectRegistration, Long> {
    List<PracticeProjectRegistration> findByStudentIdOrderByRegistrationTimeDesc(Long studentId);
    List<PracticeProjectRegistration> findByTeacherId(Long teacherId);
    List<PracticeProjectRegistration> findByStatus(String status);
}
