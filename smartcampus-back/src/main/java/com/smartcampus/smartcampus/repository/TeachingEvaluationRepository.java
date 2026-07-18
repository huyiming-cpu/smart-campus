package com.smartcampus.smartcampus.repository;

import com.smartcampus.smartcampus.entity.TeachingEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TeachingEvaluationRepository extends JpaRepository<TeachingEvaluation, Long> {
    List<TeachingEvaluation> findByStudentIdAndSemester(Long studentId, String semester);
    List<TeachingEvaluation> findByTeacherIdAndSemester(Long teacherId, String semester);
    @Query("SELECT te.teacherId, AVG(te.overallScore), COUNT(te) FROM TeachingEvaluation te GROUP BY te.teacherId")
    List<Object[]> statsByTeacher();
}
