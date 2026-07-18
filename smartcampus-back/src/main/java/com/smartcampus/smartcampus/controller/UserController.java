package com.smartcampus.smartcampus.controller;

import com.smartcampus.smartcampus.common.Result;
import com.smartcampus.smartcampus.dto.Request;
import com.smartcampus.smartcampus.dto.Response;
import com.smartcampus.smartcampus.entity.RegisterApplication;
import com.smartcampus.smartcampus.entity.Role;
import com.smartcampus.smartcampus.entity.User;
import com.smartcampus.smartcampus.entity.UserProfile;
import com.smartcampus.smartcampus.security.JwtUtil;
import com.smartcampus.smartcampus.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    // ==================== 1. 登录 ====================
    @PostMapping("/auth/login")
    public Result<Response.Login> login(@RequestBody Request.Login request) {
        return Result.success(userService.login(request));
    }

    // ==================== 2. 注册 ====================
    @PostMapping("/auth/register")
    public Result<Void> register(@RequestBody Request.Register request) {
        userService.register(request);
        return Result.success();
    }

    // ==================== 3. 查个人信息 ====================
    @GetMapping("/user/profile")
    public Result<Response.UserProfile> getProfile(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        return Result.success(userService.getProfile(userId));
    }

    /** 管理员查看任意用户信息 */
    @GetMapping("/admin/user-profile/{userId}")
    public Result<Response.UserProfile> getUserProfile(@PathVariable Long userId) {
        return Result.success(userService.getProfile(userId));
    }

    // ==================== 4. 改个人信息 ====================
    @PutMapping("/user/profile")
    public Result<Void> updateProfile(@RequestHeader("Authorization") String token,
                                       @RequestBody Request.UpdateProfile request) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        userService.updateProfile(userId, request);
        return Result.success();
    }

    // ==================== 修改密码 ====================
    @PutMapping("/user/change-password")
    public Result<Void> changePassword(@RequestHeader("Authorization") String token,
                                        @RequestBody Map<String, String> body) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        userService.changePassword(userId, body.get("oldPassword"), body.get("newPassword"));
        return Result.success();
    }

    // ==================== A-03 用户管理 ====================
    @GetMapping("/admin/users")
    public Result<List<Map<String, Object>>> listAllUsers() {
        return Result.success(userService.listAllUsers());
    }

    @GetMapping("/admin/teachers")
    public Result<List<Map<String, Object>>> listTeachers(@RequestParam(required = false) String department) {
        return Result.success(userService.listTeachers(department));
    }

    @PutMapping("/admin/users/{id}/status")
    public Result<User> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return Result.success(userService.updateUserStatus(id, body.get("status")));
    }

    @PutMapping("/admin/users/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
        userService.resetPassword(id, body.get("password"));
        return Result.success();
    }

    @DeleteMapping("/admin/users/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }

    // ==================== A-04 注册审核 ====================
    @GetMapping("/admin/register-applications")
    public Result<List<RegisterApplication>> listRegisterApplications(
            @RequestParam(required = false) String status) {
        return Result.success(userService.listRegisterApplications(status));
    }

    @PutMapping("/admin/register-applications/{id}/audit")
    public Result<RegisterApplication> auditRegistration(
            @PathVariable Long id, @RequestBody Map<String, String> body) {
        return Result.success(userService.auditRegistration(id,
                body.get("status"), body.get("rejectReason"),
                Long.valueOf(body.getOrDefault("auditorId", "0"))));
    }

    @GetMapping("/admin/register-applications/count")
    public Result<Long> countPendingRegistrations() {
        return Result.success(userService.countPendingRegistrations());
    }

    // ==================== A-05/A-06 角色 & L-03 通讯录 ====================
    @GetMapping("/admin/roles")
    public Result<List<Role>> getAllRoles() {
        return Result.success(userService.getAllRoles());
    }

    // ==================== 头像上传 ====================
    @PostMapping("/user/avatar")
    public Result<String> uploadAvatar(@RequestHeader("Authorization") String token,
                                        @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        try {
            String origName = file.getOriginalFilename();
            String ext = origName != null && origName.contains(".") ? origName.substring(origName.lastIndexOf(".")) : ".jpg";
            String fileName = "avatar_" + jwtUtil.getUserIdFromToken(token) + ext;
            java.io.File dest = new java.io.File("D:/software/smart-campus/uploads/avatars/" + fileName);
            file.transferTo(dest);
            userService.updateAvatar(jwtUtil.getUserIdFromToken(token), fileName);
            return Result.success("/uploads/avatars/" + fileName);
        } catch (Exception e) {
            return Result.error("头像上传失败");
        }
    }
}