package com.smartcampus.smartcampus.controller;

import com.smartcampus.smartcampus.common.CourseSelectConfig;
import com.smartcampus.smartcampus.common.Result;
import com.smartcampus.smartcampus.dto.Response;
import com.smartcampus.smartcampus.entity.*;
import com.smartcampus.smartcampus.repository.ClassroomRepository;
import com.smartcampus.smartcampus.repository.IdentityRegistryRepository;
import com.smartcampus.smartcampus.security.JwtUtil;
import com.smartcampus.smartcampus.service.SystemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class SystemController {

    private final SystemService systemService;
    private final JwtUtil jwtUtil;
    private final ClassroomRepository classroomRepo;
    private final IdentityRegistryRepository identityRegistryRepo;
    private final CourseSelectConfig courseSelectConfig;

    @GetMapping("/course-select/status")
    public Result<Boolean> getSelectStatus() { return Result.success(courseSelectConfig.isOpen()); }

    @PutMapping("/course-select/toggle")
    public Result<Boolean> toggleSelect(@RequestBody Map<String,Boolean> body) {
        boolean open = body.getOrDefault("open", true);
        courseSelectConfig.setOpen(open);
        return Result.success(courseSelectConfig.isOpen());
    }

    /** 领导部门（校办=全校，其他=本院） */
    private String dept(String token) {
        String d = systemService.getDepartment(jwtUtil.getUserIdFromToken(token));
        return "校办".equals(d) ? null : d;
    }

    @GetMapping("/admin/dashboard")
    public Result<Response.Dashboard> dashboard() { return Result.success(systemService.getDashboard()); }

    @GetMapping("/admin/config") public Result<List<SysConfig>> listConfigs(@RequestParam(required=false) String t) { return Result.success(systemService.listConfigs(t)); }
    @GetMapping("/admin/config/key") public Result<String> getConfig(@RequestParam String k) { return Result.success(systemService.getConfig(k)); }
    @PutMapping("/admin/config/{id}") public Result<SysConfig> updateConfig(@PathVariable Long id, @RequestBody Map<String,String> b) { return Result.success(systemService.updateConfig(id,b.get("configValue"))); }
    @PutMapping("/admin/config/key") public Result<String> updateConfigByKey(@RequestBody Map<String,String> b) { systemService.updateConfigByKey(b.get("key"),b.get("configValue")); return Result.success("OK"); }
    @GetMapping("/admin/logs/login") public Result<List<LoginLog>> listLoginLogs(@RequestParam(required=false) Long uid, @RequestParam(defaultValue="50") int l) { return Result.success(systemService.listLoginLogs(uid,l)); }

    @GetMapping("/admin/assets") public Result<List<FixedAsset>> listAssets(@RequestParam(required=false) String d) { return Result.success(systemService.listAssets(d)); }
    @PostMapping("/admin/assets") public Result<FixedAsset> saveAsset(@RequestBody FixedAsset a) { return Result.success(systemService.saveAsset(a)); }
    @DeleteMapping("/admin/assets/{id}") public Result<Void> deleteAsset(@PathVariable Long id) { systemService.deleteAsset(id); return Result.success(); }

    @GetMapping("/admin/labs") public Result<List<Lab>> listLabs(@RequestParam(required=false) String d, @RequestParam(required=false) String s) { return Result.success(systemService.listLabs(d,s)); }
    @PostMapping("/admin/labs") public Result<Lab> saveLab(@RequestBody Lab l) { return Result.success(systemService.saveLab(l)); }
    @DeleteMapping("/admin/labs/{id}") public Result<Void> deleteLab(@PathVariable Long id) { systemService.deleteLab(id); return Result.success(); }

    // ==================== 领导端 ====================

    @GetMapping("/leader/directory")
    public Result<List<Map<String, Object>>> getDirectory(@RequestHeader("Authorization") String token, @RequestParam(required=false) String department) {
        return Result.success(systemService.getDirectory(department));
    }

    /** T-14 教师通讯录 */
    @GetMapping("/teacher/directory")
    public Result<List<Map<String, Object>>> getTeacherDirectory(@RequestParam(required=false) String department) {
        return Result.success(systemService.getDirectory(department));
    }

    @GetMapping("/leader/assets/stats")
    public Result<Response.AssetStats> assetStats(@RequestHeader("Authorization") String token) {
        return Result.success(systemService.assetStats(dept(token)));
    }

    @GetMapping("/leader/statistics/enrollment")
    public Result<List<Response.StatCard>> enrollmentStats(@RequestHeader("Authorization") String token) {
        return Result.success(systemService.enrollmentStats(dept(token)));
    }

    @GetMapping("/leader/statistics/payment")
    public Result<Response.PaymentStats> paymentStats(@RequestHeader("Authorization") String token) {
        return Result.success(systemService.paymentStats(dept(token)));
    }

    @GetMapping("/leader/statistics/student-count")
    public Result<List<Response.DeptStudents>> studentCountStats(@RequestHeader("Authorization") String token) {
        return Result.success(systemService.studentCountStats(dept(token)));
    }

    // ==================== 工具API ====================
    @GetMapping("/admin/classrooms")
    public Result<List<Classroom>> listClassrooms() { return Result.success(classroomRepo.findByStatus("AVAILABLE")); }

    @GetMapping("/admin/classes")
    public Result<List<String>> listClasses() {
        return Result.success(identityRegistryRepo.findDistinctClassNames());
    }

    /** 学生查看实验室 */
    @GetMapping("/student/labs")
    public Result<List<Lab>> listStudentLabs(@RequestParam(required=false) String department) {
        return Result.success(systemService.listLabs(department, "OPEN"));
    }
}
