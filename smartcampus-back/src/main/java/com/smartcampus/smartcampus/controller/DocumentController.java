package com.smartcampus.smartcampus.controller;

import com.smartcampus.smartcampus.common.Result;
import com.smartcampus.smartcampus.dto.Request;
import com.smartcampus.smartcampus.entity.*;
import com.smartcampus.smartcampus.repository.UserProfileRepository;
import com.smartcampus.smartcampus.repository.UserRepository;
import com.smartcampus.smartcampus.security.JwtUtil;
import com.smartcampus.smartcampus.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final JwtUtil jwtUtil;
    private final UserProfileRepository userProfileRepo;

    /** 领导部门（校办=全校，其他=本院） */
    private String dept(String token) {
        String d = userProfileRepo.findById(jwtUtil.getUserIdFromToken(token)).map(UserProfile::getDepartment).orElse("校办");
        return "校办".equals(d) ? null : d;
    }

    // ==================== 教师 T-10 ====================
    @GetMapping("/teacher/documents/my")
    public Result<List<Document>> listMyDocuments(@RequestHeader("Authorization") String token) {
        return Result.success(documentService.listMyDocuments(jwtUtil.getUserIdFromToken(token)));
    }
    @PostMapping("/teacher/documents")
    public Result<Document> submitDocument(@RequestHeader("Authorization") String token, @RequestBody Request.DocumentForm form) {
        form.setSubmitterId(jwtUtil.getUserIdFromToken(token));
        return Result.success(documentService.submitDocument(form));
    }
    @DeleteMapping("/teacher/documents/{id}")
    public Result<Void> deleteDocument(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        documentService.deleteDocument(id, jwtUtil.getUserIdFromToken(token)); return Result.success();
    }

    // ==================== 领导 L-05 公文审批 ====================
    @GetMapping("/leader/documents/pending")
    public Result<List<Map<String, Object>>> listPendingDocuments(@RequestHeader("Authorization") String token) {
        return Result.success(documentService.listLeaderDocuments("pending", dept(token)));
    }
    @GetMapping("/leader/documents/approved")
    public Result<List<Map<String, Object>>> listApprovedDocuments(@RequestHeader("Authorization") String token) {
        return Result.success(documentService.listLeaderDocuments("approved", dept(token)));
    }
    @PutMapping("/leader/documents/{id}/approve")
    public Result<Document> approveDocument(@RequestHeader("Authorization") String token, @PathVariable Long id, @RequestBody Map<String,String> body) {
        return Result.success(documentService.approveDocument(id, jwtUtil.getUserIdFromToken(token), body.get("approverName"), body.getOrDefault("status","APPROVED"), body.getOrDefault("comment","")));
    }

    // ==================== 工作计划 L-04 ====================
    @GetMapping("/leader/work-plans")
    public Result<List<Map<String, Object>>> listWorkPlans(@RequestHeader("Authorization") String token) {
        // 所有领导只看自己的工作计划
        return Result.success(documentService.listWorkPlans(jwtUtil.getUserIdFromToken(token), null));
    }
    @PostMapping("/leader/work-plans") public Result<WorkPlan> createWorkPlan(@RequestHeader("Authorization") String token, @RequestBody WorkPlan p) {
        Long uid = jwtUtil.getUserIdFromToken(token);
        p.setUserId(uid);
        userProfileRepo.findById(uid).ifPresent(up -> {
            if (p.getUserName() == null || p.getUserName().isEmpty()) p.setUserName(userRepo.findById(uid).map(User::getName).orElse(""));
            if (p.getDepartment() == null || p.getDepartment().isEmpty()) p.setDepartment(up.getDepartment());
        });
        return Result.success(documentService.saveWorkPlan(p));
    }
    private final UserRepository userRepo;
    @PutMapping("/leader/work-plans/{id}") public Result<WorkPlan> updateWorkPlan(@PathVariable Long id, @RequestBody WorkPlan p) { return Result.success(documentService.updateWorkPlan(id,p)); }
    @DeleteMapping("/leader/work-plans/{id}") public Result<Void> deleteWorkPlan(@PathVariable Long id) { documentService.deleteWorkPlan(id); return Result.success(); }

    // ==================== 教师工作计划 T-11 ====================
    @GetMapping("/teacher/work-plans")
    public Result<List<WorkPlan>> listMyWorkPlans(@RequestHeader("Authorization") String token) {
        return Result.success(documentService.listMyWorkPlans(jwtUtil.getUserIdFromToken(token)));
    }
    @PostMapping("/teacher/work-plans")
    public Result<WorkPlan> createMyWorkPlan(@RequestHeader("Authorization") String token, @RequestBody WorkPlan p) {
        p.setUserId(jwtUtil.getUserIdFromToken(token));
        return Result.success(documentService.saveWorkPlan(p));
    }
    @PutMapping("/teacher/work-plans/{id}")
    public Result<WorkPlan> updateMyWorkPlan(@PathVariable Long id, @RequestBody WorkPlan p) {
        return Result.success(documentService.updateWorkPlan(id, p));
    }
    @DeleteMapping("/teacher/work-plans/{id}")
    public Result<Void> deleteMyWorkPlan(@PathVariable Long id) {
        documentService.deleteWorkPlan(id); return Result.success();
    }
}
