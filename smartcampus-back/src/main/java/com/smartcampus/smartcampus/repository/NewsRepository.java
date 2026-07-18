package com.smartcampus.smartcampus.repository;

import com.smartcampus.smartcampus.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    List<News> findByStatusOrderByPublishTimeDesc(String status);

    List<News> findByStatusAndCategoryOrderByPublishTimeDesc(String status, String category);

    @Query("SELECT n FROM News n WHERE n.status = 'PUBLISHED' " +
           "AND (n.title LIKE %:keyword% OR n.summary LIKE %:keyword%) " +
           "ORDER BY n.publishTime DESC")
    List<News> searchPublished(String keyword);
}
