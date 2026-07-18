package com.smartcampus.smartcampus.repository;

import com.smartcampus.smartcampus.entity.FixedAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FixedAssetRepository extends JpaRepository<FixedAsset, Long> {
    List<FixedAsset> findByDepartment(String department);
    @Query("SELECT f.department, COUNT(f), SUM(f.price) FROM FixedAsset f WHERE f.status = 'NORMAL' GROUP BY f.department")
    List<Object[]> statsByDepartment();
}
