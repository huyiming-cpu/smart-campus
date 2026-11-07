package com.smartcampus.smartcampus.controller;

import com.smartcampus.smartcampus.common.Result;
import com.smartcampus.smartcampus.entity.SchoolCalendarEvent;
import com.smartcampus.smartcampus.service.SchoolCalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class SchoolCalendarController {
    private final SchoolCalendarService calendarService;

    @GetMapping("/calendar/events")
    public Result<List<SchoolCalendarEvent>> getEvents(
            @RequestParam(required = false) String semester) {
        if (semester == null || semester.isEmpty()) {
            semester = (String) calendarService.getCurrentSemester().get("semester");
        }
        return Result.success(calendarService.getCalendarEvents(semester));
    }

    @GetMapping("/calendar/current-semester")
    public Result<Map<String, Object>> getCurrentSemester() {
        return Result.success(calendarService.getCurrentSemester());
    }
}
