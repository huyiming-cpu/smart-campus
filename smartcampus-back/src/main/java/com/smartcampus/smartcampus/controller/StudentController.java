package com.smartcampus.smartcampus.controller;

import com.smartcampus.smartcampus.common.Result;
import com.smartcampus.smartcampus.entity.*;
import com.smartcampus.smartcampus.repository.UserProfileRepository;
import com.smartcampus.smartcampus.security.JwtUtil;
import com.smartcampus.smartcampus.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final JwtUtil jwtUtil;
    private final UserProfileRepository userProfileRepo;

    private String dept(String token) {
        String d = userProfileRepo.findById(jwtUtil.getUserIdFromToken(token)).map(UserProfile::getDepartment).orElse("校办");
        return "校办".equals(d) ? null : d;
    }

    // ==================== 学生申请 S-14 ====================
    @GetMapping("/student/applications")
    public Result<List<Map<String, Object>>> listMyApplications(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) String type) {
        return Result.success(studentService.listMyApplications(jwtUtil.getUserIdFromToken(token), type));
    }

    @PostMapping("/student/applications")
    public Result<StudentApplication> submitApplication(@RequestHeader("Authorization") String token,
                                                         @RequestBody Map<String, Object> body) {
        Long studentId = jwtUtil.getUserIdFromToken(token);
        StudentApplication app = new StudentApplication();
        app.setStudentId(studentId);
        app.setApplicationType((String) body.getOrDefault("type", body.get("applicationType")));
        app.setTitle((String) body.get("title"));
        app.setContent((String) body.get("content"));
        app.setSemester((String) body.getOrDefault("semester", "2025-2026-2"));
        return Result.success(studentService.submitApplication(app));
    }

    /** T-12: 辅导员审核（只看本院学生） */
    @GetMapping("/teacher/applications/pending")
    public Result<List<Map<String, Object>>> listPendingApplications(@RequestHeader("Authorization") String token) {
        return Result.success(studentService.listPendingApplications(dept(token)));
    }

    @GetMapping("/teacher/applications/reviewed")
    public Result<List<Map<String, Object>>> listReviewedApplications(@RequestHeader("Authorization") String token) {
        return Result.success(studentService.listReviewedApplications(dept(token)));
    }

    /** 院长查看待二审的申请 */
    @GetMapping("/leader/applications/pending")
    public Result<List<Map<String, Object>>> listFirstApprovedApplications(@RequestHeader("Authorization") String token) {
        return Result.success(studentService.listFirstApprovedApplications(dept(token)));
    }

    /** 院长查看已审批的申请 */
    @GetMapping("/leader/applications/reviewed")
    public Result<List<Map<String, Object>>> listFinalReviewedApplications(@RequestHeader("Authorization") String token) {
        return Result.success(studentService.listFinalReviewedApplications(dept(token)));
    }

    /** 院长最终审批 */
    @PutMapping("/leader/applications/{id}/final-approve")
    public Result<Map<String, Object>> finalApprove(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return Result.success(studentService.finalApprove(id,
                body.get("status"), body.get("comment"),
                Long.valueOf(body.getOrDefault("reviewerId", "0"))));
    }

    @PutMapping("/teacher/applications/{id}/review")
    public Result<String> reviewApplication(@PathVariable Long id,
                                             @RequestBody Map<String, String> body) {
        studentService.reviewApplication(id,
                body.get("status"), body.get("comment"),
                Long.valueOf(body.getOrDefault("reviewerId", "0")));
        return Result.success("审核完成");
    }

    // ==================== 竞赛报名 S-14 ====================
    @GetMapping("/student/competitions")
    public Result<List<CompetitionRegistration>> listMyCompetitions(@RequestHeader("Authorization") String token) {
        return Result.success(studentService.listMyCompetitions(jwtUtil.getUserIdFromToken(token)));
    }

    @PostMapping("/student/competitions")
    public Result<CompetitionRegistration> registerCompetition(@RequestBody CompetitionRegistration c) {
        return Result.success(studentService.registerCompetition(c));
    }

    // ==================== 心理咨询 S-14 ====================
    @GetMapping("/student/consultations")
    public Result<List<ConsultationAppointment>> listMyConsultations(@RequestHeader("Authorization") String token) {
        return Result.success(studentService.listMyConsultations(jwtUtil.getUserIdFromToken(token)));
    }

    @PostMapping("/student/consultations")
    public Result<ConsultationAppointment> bookConsultation(@RequestBody ConsultationAppointment c) {
        return Result.success(studentService.bookConsultation(c));
    }

    // ==================== 实践项目 S-14 ====================
    @GetMapping("/student/practices")
    public Result<List<PracticeProjectRegistration>> listMyPractices(@RequestHeader("Authorization") String token) {
        return Result.success(studentService.listMyPractices(jwtUtil.getUserIdFromToken(token)));
    }

    @PostMapping("/student/practices")
    public Result<PracticeProjectRegistration> registerPractice(@RequestBody PracticeProjectRegistration p) {
        return Result.success(studentService.registerPractice(p));
    }
}
