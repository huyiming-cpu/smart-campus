package com.smartcampus.smartcampus.controller;

import com.smartcampus.smartcampus.common.CourseSelectConfig;
import com.smartcampus.smartcampus.common.Result;
import com.smartcampus.smartcampus.entity.*;
import com.smartcampus.smartcampus.security.JwtUtil;
import com.smartcampus.smartcampus.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final JwtUtil jwtUtil;
    private final CourseSelectConfig courseSelectConfig;

    // ==================== 学生 ====================
    @GetMapping("/student/schedule")
    public Result<List<CourseSchedule>> getStudentSchedule(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "2025-2026-2") String semester) {
        return Result.success(courseService.getStudentSchedule(jwtUtil.getUserIdFromToken(token), semester));
    }

    @GetMapping("/student/course-overview")
    public Result<Map<String, Object>> getCourseOverview(@RequestHeader("Authorization") String token) {
        return Result.success(courseService.getCourseOverview(jwtUtil.getUserIdFromToken(token)));
    }

    @GetMapping("/student/scores")
    public Result<List<Map<String, Object>>> getMyScores(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) String semester) {
        return Result.success(courseService.getMyScores(jwtUtil.getUserIdFromToken(token), semester));
    }

    @GetMapping("/student/retakes")
    public Result<List<Map<String, Object>>> getMyRetakes(@RequestHeader("Authorization") String token) {
        return Result.success(courseService.getMyRetakes(jwtUtil.getUserIdFromToken(token)));
    }

    @PostMapping("/student/retakes")
    public Result<Map<String, Object>> applyRetake(@RequestHeader("Authorization") String token,
                                                    @RequestBody Map<String, Object> body) {
        return Result.success(courseService.applyRetake(jwtUtil.getUserIdFromToken(token), body));
    }

    /** 教师/辅导员审批补考申请 */
    @PutMapping("/teacher/retakes/{id}/approve")
    public Result<StudentRetake> approveRetake(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return Result.success(courseService.approveRetake(id, body.get("status")));
    }

    // ==================== 教师 ====================
    @GetMapping("/teacher/schedule")
    public Result<List<CourseSchedule>> getTeacherSchedule(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "2025-2026-2") String semester) {
        return Result.success(courseService.getTeacherSchedule(jwtUtil.getUserIdFromToken(token), semester));
    }

    @GetMapping("/teacher/roster/{scheduleId}")
    public Result<List<Map<String, Object>>> getClassRoster(@PathVariable Long scheduleId) {
        return Result.success(courseService.getClassRoster(scheduleId));
    }

    /** T-13 教师查看所授课程选课情况 */
    @GetMapping("/teacher/enrollment")
    public Result<List<Map<String, Object>>> getTeacherEnrollment(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "2025-2026-2") String semester) {
        return Result.success(courseService.getTeacherEnrollment(jwtUtil.getUserIdFromToken(token), semester));
    }

    @PutMapping("/teacher/scores/{studentCourseId}")
    public Result<StudentCourse> enterScore(@RequestHeader("Authorization") String token,
                                             @PathVariable Long studentCourseId,
                                             @RequestBody Map<String, String> body) {
        return Result.success(courseService.enterScore(studentCourseId,
                new BigDecimal(body.get("score")), body.get("gradeLevel"),
                body.get("examType"), jwtUtil.getUserIdFromToken(token),
                body.get("operatorName")));
    }

    // ==================== 管理员 ====================
    @GetMapping("/admin/courses")
    public Result<List<Course>> listAllCourses(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String courseType) {
        return Result.success(courseService.listAllCourses(department, courseType));
    }

    @PostMapping("/admin/courses")
    public Result<Course> saveCourse(@RequestBody Course c) { return Result.success(courseService.saveCourse(c)); }

    @PutMapping("/admin/courses/{id}")
    public Result<Course> updateCourse(@PathVariable Long id, @RequestBody Course c) {
        return Result.success(courseService.updateCourse(id, c));
    }

    @DeleteMapping("/admin/courses/{id}")
    public Result<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id); return Result.success();
    }

    @GetMapping("/admin/schedules")
    public Result<List<CourseSchedule>> listAllSchedules(@RequestParam(required = false) String semester) {
        return Result.success(courseService.listAllSchedules(semester));
    }

    @PostMapping("/admin/schedules")
    public Result<CourseSchedule> saveSchedule(@RequestBody CourseSchedule s) {
        return Result.success(courseService.saveSchedule(s));
    }

    @DeleteMapping("/admin/schedules/{id}")
    public Result<Void> deleteSchedule(@PathVariable Long id) {
        courseService.deleteSchedule(id); return Result.success();
    }

    @GetMapping("/admin/student-courses")
    public Result<List<StudentCourse>> listStudentCourses(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) String semester) {
        return Result.success(courseService.listStudentCourses(studentId, semester));
    }

    @PostMapping("/admin/enroll")
    public Result<StudentCourse> enrollStudent(@RequestBody Map<String, Object> body) {
        if (!courseSelectConfig.isOpen()) {
            return Result.error(403, "选课功能暂未开放，请联系管理员");
        }
        return Result.success(courseService.enrollStudent(
                Long.valueOf(body.get("studentId").toString()),
                Long.valueOf(body.get("scheduleId").toString()),
                (String) body.get("semester")));
    }

    @PutMapping("/admin/student-courses/{id}/drop")
    public Result<Void> dropStudent(@PathVariable Long id) {
        courseService.dropStudent(id); return Result.success();
    }

    /** 教师端退课 */
    @PutMapping("/teacher/student-courses/{id}/drop")
    public Result<Void> teacherDropStudent(@PathVariable Long id) {
        courseService.dropStudent(id); return Result.success();
    }
}
