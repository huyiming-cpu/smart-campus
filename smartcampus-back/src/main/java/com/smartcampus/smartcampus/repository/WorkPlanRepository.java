package com.smartcampus.smartcampus.repository;

import com.smartcampus.smartcampus.entity.WorkPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkPlanRepository extends JpaRepository<WorkPlan, Long> {
    List<WorkPlan> findByUserIdOrderByPlanDateDesc(Long userId);
    List<WorkPlan> findByDepartmentOrderByPlanDateDesc(String department);
    List<WorkPlan> findByPlanDateBetweenOrderByPlanDateAsc(LocalDate start, LocalDate end);
}
