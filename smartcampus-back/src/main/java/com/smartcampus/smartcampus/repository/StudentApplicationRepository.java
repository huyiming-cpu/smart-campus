package com.smartcampus.smartcampus.repository;

import com.smartcampus.smartcampus.entity.StudentApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StudentApplicationRepository extends JpaRepository<StudentApplication, Long> {
    List<StudentApplication> findByStudentIdOrderBySubmitTimeDesc(Long studentId);
    List<StudentApplication> findByStatusOrderBySubmitTimeDesc(String status);
    List<StudentApplication> findByApplicationTypeOrderBySubmitTimeDesc(String applicationType);
}
