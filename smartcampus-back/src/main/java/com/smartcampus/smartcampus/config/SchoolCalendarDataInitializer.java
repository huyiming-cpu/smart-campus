package com.smartcampus.smartcampus.config;

import com.smartcampus.smartcampus.entity.SchoolCalendarEvent;
import com.smartcampus.smartcampus.repository.SchoolCalendarEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.data-init", name = "school-calendar",
        havingValue = "true", matchIfMissing = true)
public class SchoolCalendarDataInitializer implements CommandLineRunner {
    private final SchoolCalendarEventRepository repo;

    @Override
    public void run(String... args) {
        if (repo.count() > 0) return;

        List<SchoolCalendarEvent> events = List.of(
                event("2025-2026-2", "2026-02-23", "春季学期开学", "SEMESTER_START", 1, "春季学期正式开始上课"),
                event("2025-2026-2", "2026-07-10", "春季学期结束", "SEMESTER_END", 20, "春季学期课程结束"),
                event("2025-2026-2", "2026-04-04", "清明节", "HOLIDAY", 5, "清明节假期，4月4日-6日放假"),
                event("2025-2026-2", "2026-05-01", "劳动节", "HOLIDAY", 8, "劳动节假期，5月1日-5日放假"),
                event("2025-2026-2", "2026-05-31", "端午节", "HOLIDAY", 11, "端午节假期，5月31日-6月2日放假"),
                event("2025-2026-2", "2026-06-22", "期末考试周开始", "EXAM_WEEK", 14, "期末考试周，请关注各科考试安排"),
                event("2025-2026-2", "2026-07-03", "期末考试周结束", "EXAM_WEEK", 16, "期末考试结束"),
                event("2025-2026-2", "2026-04-23", "春季运动会", "ACTIVITY", 6, "学校春季田径运动会，全校停课一天"),
                event("2025-2026-2", "2026-05-15", "校园科技文化节", "ACTIVITY", 9, "科技文化节开幕，持续一周"),
                event("2025-2026-2", "2026-06-10", "毕业典礼", "ACTIVITY", 12, "2026届毕业生毕业典礼暨学位授予仪式"),
                event("2025-2026-2", "2026-04-26", "补课（4月23日课程）", "MAKEUP", 7, "因运动会停课，安排补课"),
                event("2025-2026-2", "2026-07-11", "暑假开始", "VACATION", 21, "暑假正式开始，9月1日开学"),
                event("2025-2026-2", "2026-08-31", "暑假结束", "VACATION", 22, "暑假结束，请按时返校报到")
        );
        repo.saveAll(events);
    }

    private SchoolCalendarEvent event(String semester, String date, String title,
                                       String type, int sort, String desc) {
        SchoolCalendarEvent e = new SchoolCalendarEvent();
        e.setSemester(semester);
        e.setEventDate(LocalDate.parse(date));
        e.setEventTitle(title);
        e.setEventType(type);
        e.setSortOrder(sort);
        e.setDescription(desc);
        return e;
    }
}
