package com.smartcampus.smartcampus.repository;

import com.smartcampus.smartcampus.entity.GraduationDesign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GraduationDesignRepository extends JpaRepository<GraduationDesign, Long> {
    List<GraduationDesign> findByStudentId(Long studentId);
    List<GraduationDesign> findByTeacherIdOrderBySubmitTimeDesc(Long teacherId);
}
