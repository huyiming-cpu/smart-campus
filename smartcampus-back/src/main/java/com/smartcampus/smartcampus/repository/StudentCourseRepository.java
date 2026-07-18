package com.smartcampus.smartcampus.repository;

import com.smartcampus.smartcampus.entity.StudentCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StudentCourseRepository extends JpaRepository<StudentCourse, Long> {
    List<StudentCourse> findByStudentIdAndSemester(Long studentId, String semester);
    List<StudentCourse> findByStudentId(Long studentId);
    List<StudentCourse> findByScheduleId(Long scheduleId);
    @Query("SELECT sc FROM StudentCourse sc WHERE sc.studentId = :studentId ORDER BY sc.semester DESC")
    List<StudentCourse> findAllByStudentId(Long studentId);
}
