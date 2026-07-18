package com.smartcampus.smartcampus.repository;

import com.smartcampus.smartcampus.entity.RegisterApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RegisterApplicationRepository extends JpaRepository<RegisterApplication, Long> {
    List<RegisterApplication> findByStatusOrderByApplyTimeDesc(String status);
    long countByStatus(String status);
}
