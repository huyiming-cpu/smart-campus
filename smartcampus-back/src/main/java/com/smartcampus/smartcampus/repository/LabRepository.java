package com.smartcampus.smartcampus.repository;

import com.smartcampus.smartcampus.entity.Lab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LabRepository extends JpaRepository<Lab, Long> {
    List<Lab> findByDepartment(String department);
    List<Lab> findByStatus(String status);
}
