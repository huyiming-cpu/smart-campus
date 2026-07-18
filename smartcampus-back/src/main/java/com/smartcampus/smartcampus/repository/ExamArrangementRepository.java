package com.smartcampus.smartcampus.repository;

import com.smartcampus.smartcampus.entity.ExamArrangement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExamArrangementRepository extends JpaRepository<ExamArrangement, Long> {
    List<ExamArrangement> findBySemesterOrderByExamDateAsc(String semester);
    List<ExamArrangement> findBySupervisorIdAndSemester(Long supervisorId, String semester);
}
