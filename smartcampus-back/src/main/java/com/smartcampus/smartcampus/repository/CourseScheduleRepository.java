package com.smartcampus.smartcampus.repository;

import com.smartcampus.smartcampus.entity.CourseSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseScheduleRepository extends JpaRepository<CourseSchedule, Long> {
    List<CourseSchedule> findByTeacherIdAndSemester(Long teacherId, String semester);
    List<CourseSchedule> findBySemesterAndClassName(String semester, String className);
    List<CourseSchedule> findBySemesterOrderByWeekdayAsc(String semester);
}
