package com.smartcampus.smartcampus.repository;

import com.smartcampus.smartcampus.entity.SchoolCalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SchoolCalendarEventRepository extends JpaRepository<SchoolCalendarEvent, Long> {
    List<SchoolCalendarEvent> findBySemesterOrderByEventDateAsc(String semester);
    List<SchoolCalendarEvent> findBySemesterAndEventType(String semester, String eventType);
}
