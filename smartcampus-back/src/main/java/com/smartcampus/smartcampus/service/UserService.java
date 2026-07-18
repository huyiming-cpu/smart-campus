package com.smartcampus.smartcampus.service;

import com.smartcampus.smartcampus.dto.Request;
import com.smartcampus.smartcampus.dto.Response;
import com.smartcampus.smartcampus.entity.IdentityRegistry;
import com.smartcampus.smartcampus.entity.Role;
import com.smartcampus.smartcampus.entity.User;
import com.smartcampus.smartcampus.entity.UserProfile;
import com.smartcampus.smartcampus.repository.IdentityRegistryRepository;
import com.smartcampus.smartcampus.repository.RegisterApplicationRepository;
import com.smartcampus.smartcampus.repository.UserProfileRepository;
import com.smartcampus.smartcampus.repository.UserRepository;
import com.smartcampus.smartcampus.security.JwtUtil;
import com.smartcampus.smartcampus.entity.RegisterApplication;
import com.smartcampus.smartcampus.entity.Role;
import com.smartcampus.smartcampus.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final IdentityRegistryRepository identityRegistryRepository;
    private final RegisterApplicationRepository registerApplicationRepository;
    private final JwtUtil jwtUtil;

    // ==================== 1. 登录 ====================
    public Response.Login login(Request.Login request) {
        User user = userRepository.findByIdentityNumber(request.getIdentityNumber())
                .orElseThrow(() -> new RuntimeException("账号或密码错误"));
        if (!request.getPassword().equals(user.getPasswordHash()))
            throw new RuntimeException("账号或密码错误");
        if ("LOCKED".equals(user.getStatus()))
            throw new RuntimeException("账号已被锁定，请联系管理员");
        if ("DELETED".equals(user.getStatus()))
            throw new RuntimeException("账号已注销");

        List<Role> roles = userRepository.findRolesByUserId(user.getId());
        if (roles.isEmpty())
            throw new RuntimeException("该用户未分配角色，请联系管理员");
        Role role = roles.get(0);
        String token = jwtUtil.generateToken(user.getId(), user.getIdentityNumber(), role.getRoleCode());
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);

        Response.Login resp = new Response.Login();
        resp.setToken(token); resp.setUserId(user.getId());
        resp.setIdentityNumber(user.getIdentityNumber()); resp.setName(user.getName());
        resp.setRoleCode(role.getRoleCode()); resp.setRoleName(role.getRoleName());
        return resp;
    }

    // ==================== 2. 注册 ====================
    @Transactional
    public void register(Request.Register request) {
        if (userRepository.existsByIdentityNumber(request.getIdentityNumber()))
            throw new RuntimeException("该学号/工号已被注册");

        IdentityRegistry registry = identityRegistryRepository
                .findByIdentityNumberAndNameAndIdCardLast6(
                        request.getIdentityNumber(), request.getName(), request.getIdCardLast6())
                .orElseThrow(() -> new RuntimeException("身份验证失败，请检查学号、姓名和身份证后6位"));
        if (registry.getStatus() == 1)
            throw new RuntimeException("该学号/工号已完成注册");

        User user = new User();
        user.setIdentityNumber(request.getIdentityNumber());
        user.setPasswordHash(request.getPassword());
        user.setName(request.getName());
        user.setGender("UNKNOWN");
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setStatus("NORMAL");
        user.setInfoCompleteness(0);
        User savedUser = userRepository.save(user);

        userRepository.saveUserRole(savedUser.getId(), registry.getRoleId());

        // 创建 user_profile，根据年级自动计算入学/毕业时间
        UserProfile profile = new UserProfile();
        profile.setUserId(savedUser.getId());
        profile.setDepartment(registry.getDepartment());
        profile.setMajor(registry.getMajor());
        profile.setGrade(registry.getGrade());
        profile.setClassName(registry.getClassName());
        String grade = registry.getGrade();
        if (grade != null && grade.matches("\\d{4}级")) {
            int year = Integer.parseInt(grade.substring(0, 4));
            profile.setEnrollmentDate(LocalDate.of(year, 9, 1).atStartOfDay());
            profile.setGraduationDate(LocalDate.of(year + 4, 6, 1).atStartOfDay());
        }
        userProfileRepository.save(profile);

        registry.setStatus(1);
        registry.setPhone(request.getPhone());
        identityRegistryRepository.save(registry);
    }

    // ==================== 3. 查个人信息 ====================
    public Response.UserProfile getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        UserProfile profile = userProfileRepository.findById(userId).orElse(null);

        Response.UserProfile resp = new Response.UserProfile();
        resp.setId(user.getId());
        resp.setIdentityNumber(user.getIdentityNumber());
        resp.setName(user.getName());
        resp.setGender(user.getGender());
        resp.setPhone(user.getPhone());
        resp.setEmail(user.getEmail());
        resp.setAvatarUrl(user.getAvatarUrl());
        if (profile != null) {
            resp.setDepartment(profile.getDepartment());
            resp.setMajor(profile.getMajor());
            resp.setGrade(profile.getGrade());
            resp.setClassName(profile.getClassName());
            resp.setTitle(profile.getTitle());
            resp.setAddress(profile.getAddress());
            resp.setEmergencyContact(profile.getEmergencyContact());
            resp.setEmergencyPhone(profile.getEmergencyPhone());
            resp.setEnrollmentDate(profile.getEnrollmentDate());
            resp.setGraduationDate(profile.getGraduationDate());
        }
        return resp;
    }

    // ==================== 4. 改个人信息 ====================
    @Transactional
    public void updateProfile(Long userId, Request.UpdateProfile request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setName(request.getName());
        user.setGender(request.getGender());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setAvatarUrl(request.getAvatarUrl());
        userRepository.save(user);

        UserProfile profile = userProfileRepository.findById(userId)
                .orElseGet(() -> {
                    UserProfile p = new UserProfile();
                    p.setUserId(userId);
                    return p;
                });
        profile.setMajor(request.getMajor());
        profile.setTitle(request.getTitle());
        profile.setAddress(request.getAddress());
        profile.setEmergencyContact(request.getEmergencyContact());
        profile.setEmergencyPhone(request.getEmergencyPhone());
        if (request.getEnrollmentDate() != null && !request.getEnrollmentDate().isEmpty())
            profile.setEnrollmentDate(LocalDateTime.parse(request.getEnrollmentDate()));
        if (request.getGraduationDate() != null && !request.getGraduationDate().isEmpty())
            profile.setGraduationDate(LocalDateTime.parse(request.getGraduationDate()));
        userProfileRepository.save(profile);
    }

    // ==================== 修改密码 ====================
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        if (!oldPassword.equals(user.getPasswordHash()))
            throw new RuntimeException("当前密码错误");
        user.setPasswordHash(newPassword);
        userRepository.save(user);
    }

    // ==================== A-03 用户管理（管理员） ====================
    public List<Map<String, Object>> listAllUsers() {
        List<User> users = userRepository.findAllUsers();
        List<Map<String, Object>> result = new ArrayList<>();
        for (User u : users) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", u.getId()); m.put("identityNumber", u.getIdentityNumber());
            m.put("name", u.getName()); m.put("gender", u.getGender());
            m.put("phone", u.getPhone()); m.put("email", u.getEmail());
            m.put("status", u.getStatus());
            List<Role> roles = userRepository.findRolesByUserId(u.getId());
            if (!roles.isEmpty()) {
                m.put("roleCode", roles.get(0).getRoleCode());
                m.put("roleName", roles.get(0).getRoleName());
            }
            result.add(m);
        }
        return result;
    }

    /** 教师列表（可按学院筛选） */
    public List<Map<String, Object>> listTeachers(String department) {
        List<User> all = userRepository.findAllUsers();
        List<Map<String, Object>> result = new ArrayList<>();
        for (User u : all) {
            List<Role> roles = userRepository.findRolesByUserId(u.getId());
            boolean isTeacher = roles.stream().anyMatch(r -> "TEACHER".equals(r.getRoleCode()));
            if (!isTeacher) continue;
            UserProfile p = userProfileRepository.findById(u.getId()).orElse(null);
            if (department != null && p != null && !department.equals(p.getDepartment())) continue;
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", u.getId()); m.put("name", u.getName());
            m.put("department", p != null ? p.getDepartment() : "");
            m.put("title", p != null ? p.getTitle() : "");
            result.add(m);
        }
        return result;
    }

    @Transactional
    public User updateUserStatus(Long userId, String status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setStatus(status);
        return userRepository.save(user);
    }

    @Transactional
    public void resetPassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setPasswordHash(newPassword);
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        userRepository.deleteById(userId);
    }

    // ==================== A-04 注册审核 ====================
    public List<RegisterApplication> listRegisterApplications(String status) {
        if (status != null) return registerApplicationRepository.findByStatusOrderByApplyTimeDesc(status);
        return registerApplicationRepository.findAll();
    }

    @Transactional
    public RegisterApplication auditRegistration(Long id, String status, String rejectReason, Long auditorId) {
        RegisterApplication app = registerApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("申请不存在"));
        app.setStatus(status);
        app.setRejectReason(rejectReason);
        app.setAuditorId(auditorId);
        app.setAuditTime(LocalDateTime.now());
        return registerApplicationRepository.save(app);
    }

    public long countPendingRegistrations() {
        return registerApplicationRepository.countByStatus("PENDING");
    }

    // ==================== A-05/A-06 角色权限 + L-03 通讯录 ====================
    public List<UserProfile> getAllProfiles(String department) {
        if (department != null) return userProfileRepository.findByDepartment(department);
        return userProfileRepository.findAll();
    }

    public List<Role> getAllRoles() {
        return userRepository.findAllRoles();
    }

    @Transactional
    public void updateAvatar(Long userId, String avatarUrl) {
        userRepository.findById(userId).ifPresent(u -> {
            u.setAvatarUrl(avatarUrl);
            userRepository.save(u);
        });
    }
}
