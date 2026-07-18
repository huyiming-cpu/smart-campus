package com.smartcampus.smartcampus.controller;

import com.smartcampus.smartcampus.common.Result;
import com.smartcampus.smartcampus.dto.Response;
import com.smartcampus.smartcampus.entity.*;
import com.smartcampus.smartcampus.repository.UserProfileRepository;
import com.smartcampus.smartcampus.security.JwtUtil;
import com.smartcampus.smartcampus.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;
    private final JwtUtil jwtUtil;

    // ==================== S-08 考试安排 ====================
    @GetMapping("/student/exams")
    public Result<List<ExamArrangement>> getExamArrangements(
            @RequestParam(defaultValue = "2025-2026-2") String semester) {
        return Result.success(examService.getExamArrangements(semester));
    }

    @GetMapping("/teacher/exams")
    public Result<List<ExamArrangement>> getSupervisorExams(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "2025-2026-2") String semester) {
        return Result.success(examService.getSupervisorExams(jwtUtil.getUserIdFromToken(token), semester));
    }

    // ==================== S-09 评教 ====================
    @GetMapping("/student/evaluations")
    public Result<List<Map<String, Object>>> getMyEvaluations(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "2025-2026-2") String semester) {
        return Result.success(examService.getMyEvaluations(jwtUtil.getUserIdFromToken(token), semester));
    }

    @PostMapping("/student/evaluations")
    public Result<Map<String, Object>> submitEvaluation(@RequestHeader("Authorization") String token,
                                                         @RequestBody Map<String, Object> body) {
        Long studentId = jwtUtil.getUserIdFromToken(token);
        return Result.success(examService.submitEvaluation(studentId, body));
    }

    @GetMapping("/teacher/evaluations")
    public Result<List<Map<String, Object>>> getTeacherEvaluations(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "2025-2026-2") String semester) {
        return Result.success(examService.getTeacherEvaluations(jwtUtil.getUserIdFromToken(token), semester));
    }

    /** L-12 评教统计 */
    @GetMapping("/leader/evaluation-stats")
    public Result<List<Response.EvalStat>> evaluationStats(@RequestHeader("Authorization") String token) {
        return Result.success(examService.evaluationStats(dept(token)));
    }

    /** 领导部门（校办=全校，其他=本院） */
    private String dept(String token) {
        String d = userProfileRepo.findById(jwtUtil.getUserIdFromToken(token)).map(UserProfile::getDepartment).orElse("校办");
        return "校办".equals(d) ? null : d;
    }
    private final UserProfileRepository userProfileRepo;

    // ==================== S-11 等级考试 ====================
    @GetMapping("/student/level-exams")
    public Result<List<LevelExam>> listLevelExams(@RequestParam(required = false) String status) {
        return Result.success(examService.listLevelExams(status));
    }

    @PostMapping("/student/level-exams/register")
    public Result<LevelExamRegistration> registerLevelExam(@RequestHeader("Authorization") String token,
                                                            @RequestBody Map<String, Long> body) {
        return Result.success(examService.registerLevelExam(
                body.get("examId"), jwtUtil.getUserIdFromToken(token)));
    }

    @GetMapping("/student/level-exams/my")
    public Result<List<Map<String, Object>>> getMyExamRegistrations(
            @RequestHeader("Authorization") String token) {
        return Result.success(examService.getMyExamRegistrations(jwtUtil.getUserIdFromToken(token)));
    }

    // ==================== A-12 管理员考试管理 ====================
    @PostMapping("/admin/exams")
    public Result<ExamArrangement> saveExamArrangement(@RequestBody ExamArrangement e) {
        return Result.success(examService.saveExamArrangement(e));
    }

    @PutMapping("/admin/exams/{id}")
    public Result<ExamArrangement> updateExamArrangement(@PathVariable Long id, @RequestBody ExamArrangement e) {
        return Result.success(examService.updateExamArrangement(id, e));
    }

    @DeleteMapping("/admin/exams/{id}")
    public Result<Void> deleteExamArrangement(@PathVariable Long id) {
        examService.deleteExamArrangement(id); return Result.success();
    }

    @PostMapping("/admin/level-exams")
    public Result<LevelExam> saveLevelExam(@RequestBody LevelExam e) {
        return Result.success(examService.saveLevelExam(e));
    }

    @DeleteMapping("/admin/level-exams/{id}")
    public Result<Void> deleteLevelExam(@PathVariable Long id) {
        examService.deleteLevelExam(id); return Result.success();
    }
}
