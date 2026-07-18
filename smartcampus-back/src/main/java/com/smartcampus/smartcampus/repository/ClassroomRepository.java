package com.smartcampus.smartcampus.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ClassroomRepository extends JpaRepository<com.smartcampus.smartcampus.entity.Classroom, Long> {
    List<com.smartcampus.smartcampus.entity.Classroom> findByStatus(String status);
    @Query("SELECT DISTINCT c.building FROM Classroom c")
    List<String> findDistinctBuildings();
}
