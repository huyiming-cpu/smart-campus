package com.smartcampus.smartcampus.repository;

import com.smartcampus.smartcampus.entity.LoginLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {
    List<LoginLog> findByUserIdOrderByLoginTimeDesc(Long userId);
    long countByStatus(String status);
    @Query("SELECT COUNT(DISTINCT l.userId) FROM LoginLog l WHERE l.status = 'SUCCESS' AND l.loginTime >= CURRENT_DATE")
    long countTodayActiveUsers();
}
