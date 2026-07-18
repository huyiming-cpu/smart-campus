package com.smartcampus.smartcampus.repository;

import com.smartcampus.smartcampus.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Announcement, Long> {

    List<Announcement> findByStatusOrderByIsTopDescCreatedTimeDesc(String status);

    @Query("SELECT a FROM Announcement a WHERE a.status = 'PUBLISHED' " +
           "AND (a.targetRoles LIKE %:role% OR a.targetRoles LIKE '%ALL%' OR a.targetRoles IS NULL) " +
           "ORDER BY a.isTop DESC, a.createdTime DESC")
    List<Announcement> findPublishedByRole(String role);

    @Query("SELECT a FROM Announcement a WHERE a.status = 'PUBLISHED' " +
           "AND (a.title LIKE %:keyword%) " +
           "ORDER BY a.isTop DESC, a.createdTime DESC")
    List<Announcement> searchPublished(String keyword);

    List<Announcement> findByStatusAndIsTopOrderByCreatedTimeDesc(String status, Integer isTop);
}
