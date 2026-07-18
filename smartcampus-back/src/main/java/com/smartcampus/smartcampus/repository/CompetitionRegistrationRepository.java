package com.smartcampus.smartcampus.repository;

import com.smartcampus.smartcampus.entity.CompetitionRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CompetitionRegistrationRepository extends JpaRepository<CompetitionRegistration, Long> {
    List<CompetitionRegistration> findByStudentIdOrderByRegistrationTimeDesc(Long studentId);
    List<CompetitionRegistration> findByStatus(String status);
}
