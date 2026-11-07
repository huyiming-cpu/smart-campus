package com.smartcampus.smartcampus.service;

import com.smartcampus.smartcampus.entity.SchoolCalendarEvent;
import com.smartcampus.smartcampus.repository.SchoolCalendarEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SchoolCalendarService {
    private final SchoolCalendarEventRepository repo;

    public List<SchoolCalendarEvent> getCalendarEvents(String semester) {
        return repo.findBySemesterOrderByEventDateAsc(semester);
    }

    public Map<String, Object> getCurrentSemester() {
        String semester = computeCurrentSemester();
        Map<String, Object> result = new HashMap<>();
        result.put("semester", semester);
        result.put("eventCount", repo.findBySemesterOrderByEventDateAsc(semester).size());
        return result;
    }

    private String computeCurrentSemester() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();

        if (month >= 2 && month <= 8) {
            return (year - 1) + "-" + year + "-2";
        } else {
            return year + "-" + (year + 1) + "-1";
        }
    }
}
