package com.smartcampus.smartcampus.controller;

import com.smartcampus.smartcampus.common.Result;
import com.smartcampus.smartcampus.entity.*;
import com.smartcampus.smartcampus.repository.UserProfileRepository;
import com.smartcampus.smartcampus.security.JwtUtil;
import com.smartcampus.smartcampus.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;
    private final JwtUtil jwtUtil;
    private final UserProfileRepository userProfileRepo;

    private String dept(String token) {
        String d = userProfileRepo.findById(jwtUtil.getUserIdFromToken(token)).map(UserProfile::getDepartment).orElse("校办");
        return "校办".equals(d) ? null : d;
    }

    // ==================== 科研 T-07 ====================
    @GetMapping("/teacher/research")
    public Result<List<ScientificResearch>> listMyResearch(@RequestHeader("Authorization") String token) {
        return Result.success(teacherService.listMyResearch(jwtUtil.getUserIdFromToken(token)));
    }

    @PostMapping("/teacher/research")
    public Result<ScientificResearch> saveResearch(@RequestHeader("Authorization") String token,
                                                    @RequestBody ScientificResearch r) {
        r.setTeacherId(jwtUtil.getUserIdFromToken(token));
        return Result.success(teacherService.saveResearch(r));
    }

    @PutMapping("/teacher/research/{id}")
    public Result<ScientificResearch> updateResearch(@PathVariable Long id, @RequestBody ScientificResearch r) {
        return Result.success(teacherService.updateResearch(id, r));
    }

    @DeleteMapping("/teacher/research/{id}")
    public Result<Void> deleteResearch(@PathVariable Long id) {
        teacherService.deleteResearch(id); return Result.success();
    }

    /** L-06 科研总览（院长看本院，校长看全校） */
    @GetMapping("/leader/research")
    public Result<List<Map<String, Object>>> listAllResearch(@RequestHeader("Authorization") String token,
                                                              @RequestParam(required = false) String status) {
        return Result.success(teacherService.listAllResearch(dept(token), status));
    }

    // ==================== 毕业设计 T-08 ====================
    @GetMapping("/teacher/graduation-design")
    public Result<List<Map<String, Object>>> listMyDesigns(@RequestHeader("Authorization") String token) {
        return Result.success(teacherService.listMyDesigns(jwtUtil.getUserIdFromToken(token)));
    }

    @GetMapping("/student/graduation-design")
    public Result<Map<String, Object>> getMyDesign(@RequestHeader("Authorization") String token) {
        return Result.success(teacherService.getMyDesign(jwtUtil.getUserIdFromToken(token)));
    }

    @PutMapping("/teacher/graduation-design/{id}")
    public Result<GraduationDesign> updateDesign(@PathVariable Long id, @RequestBody GraduationDesign d) {
        return Result.success(teacherService.updateDesign(id, d));
    }

    /** 教师审批毕业设计 */
    @PutMapping("/teacher/graduation-design/{id}/approve")
    public Result<Map<String, Object>> approveDesign(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return Result.success(teacherService.approveDesign(id, body.get("status"), body.get("comment")));
    }

    /** 学生提交/修改毕业设计选题 */
    @PostMapping("/student/graduation-design")
    public Result<Map<String, Object>> submitDesign(@RequestHeader("Authorization") String token,
                                                     @RequestBody Map<String, Object> body) {
        return Result.success(teacherService.submitDesign(jwtUtil.getUserIdFromToken(token), body));
    }

    /** 获取本院教师列表（学生选毕设老师用） */
    @GetMapping("/student/teachers")
    public Result<List<Map<String, Object>>> getDepartmentTeachers(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        return Result.success(teacherService.getDepartmentTeachers(userId));
    }
}
