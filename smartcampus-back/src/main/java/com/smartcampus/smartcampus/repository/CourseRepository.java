package com.smartcampus.smartcampus.repository;

import com.smartcampus.smartcampus.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCourseCode(String courseCode);
    List<Course> findByDepartment(String department);
    List<Course> findByCourseType(String courseType);
    List<Course> findByStatus(Integer status);
}
