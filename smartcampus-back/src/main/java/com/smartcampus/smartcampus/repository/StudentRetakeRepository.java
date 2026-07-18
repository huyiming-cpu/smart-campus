package com.smartcampus.smartcampus.repository;

import com.smartcampus.smartcampus.entity.StudentRetake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StudentRetakeRepository extends JpaRepository<StudentRetake, Long> {
    List<StudentRetake> findByStudentIdOrderByApplicationTimeDesc(Long studentId);
    List<StudentRetake> findByStatus(String status);
}
