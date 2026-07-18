package com.smartcampus.smartcampus.service;

import com.smartcampus.smartcampus.entity.*;
import com.smartcampus.smartcampus.repository.*;
import com.smartcampus.smartcampus.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final ScientificResearchRepository researchRepo;
    private final GraduationDesignRepository designRepo;

    // ==================== 科研 T-07 ====================
    public List<ScientificResearch> listMyResearch(Long teacherId) {
        return researchRepo.findByTeacherIdOrderByStartDateDesc(teacherId);
    }

    @Transactional
    public ScientificResearch saveResearch(ScientificResearch r) {
        return researchRepo.save(r);
    }

    @Transactional
    public ScientificResearch updateResearch(Long id, ScientificResearch r) {
        ScientificResearch e = researchRepo.findById(id).orElseThrow();
        e.setProjectName(r.getProjectName()); e.setProjectCode(r.getProjectCode());
        e.setProjectType(r.getProjectType()); e.setFundAmount(r.getFundAmount());
        e.setStartDate(r.getStartDate()); e.setEndDate(r.getEndDate());
        e.setStatus(r.getStatus()); e.setMembers(r.getMembers());
        e.setAchievements(r.getAchievements());
        return researchRepo.save(e);
    }

    @Transactional
    public void deleteResearch(Long id) { researchRepo.deleteById(id); }

    /** L-06: 领导查看科研总览（department=null=全部） */
    public List<Map<String, Object>> listAllResearch(String department, String status) {
        List<ScientificResearch> list;
        if (status != null) list = researchRepo.findByStatusOrderByStartDateDesc(status);
        else list = researchRepo.findAll();
        if (department != null) {
            list = list.stream().filter(r -> {
                UserProfile tp = userProfileRepo.findById(r.getTeacherId()).orElse(null);
                return tp != null && department.equals(tp.getDepartment());
            }).collect(Collectors.toList());
        }
        return enrichResearch(list);
    }

    private List<Map<String, Object>> enrichResearch(List<ScientificResearch> list) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (ScientificResearch r : list) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", r.getId()); m.put("teacherId", r.getTeacherId());
            m.put("projectName", r.getProjectName()); m.put("projectCode", r.getProjectCode());
            m.put("projectType", r.getProjectType()); m.put("fundAmount", r.getFundAmount());
            m.put("startDate", r.getStartDate()); m.put("endDate", r.getEndDate());
            m.put("status", r.getStatus()); m.put("members", r.getMembers());
            m.put("achievements", r.getAchievements());
            userRepo.findById(r.getTeacherId()).ifPresent(t -> m.put("teacherName", t.getName()));
            userProfileRepo.findById(r.getTeacherId()).ifPresent(p -> m.put("teacherDepartment", p.getDepartment()));
            result.add(m);
        }
        return result;
    }

    private final UserRepository userRepo;
    private final UserProfileRepository userProfileRepo;

    // ==================== 毕业设计 T-08 ====================
    public List<Map<String, Object>> listMyDesigns(Long teacherId) {
        List<GraduationDesign> list = designRepo.findByTeacherIdOrderBySubmitTimeDesc(teacherId);
        return enrichDesigns(list);
    }

    public Map<String, Object> getMyDesign(Long studentId) {
        List<GraduationDesign> list = designRepo.findByStudentId(studentId);
        if (list.isEmpty()) return null;
        return enrichDesign(list.get(0));
    }

    private List<Map<String, Object>> enrichDesigns(List<GraduationDesign> list) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (GraduationDesign d : list) result.add(enrichDesign(d));
        return result;
    }

    private Map<String, Object> enrichDesign(GraduationDesign d) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", d.getId()); m.put("studentId", d.getStudentId());
        m.put("teacherId", d.getTeacherId()); m.put("topicName", d.getTopicName());
        m.put("topicDescription", d.getTopicDescription()); m.put("semester", d.getSemester());
        m.put("status", d.getStatus()); m.put("teacherComment", d.getTeacherComment());
        m.put("announcement", d.getAnnouncement()); m.put("score", d.getScore());
        m.put("submitTime", d.getSubmitTime()); m.put("defenseTime", d.getDefenseTime());
        userRepo.findById(d.getStudentId()).ifPresent(u -> m.put("studentName", u.getName()));
        userRepo.findById(d.getTeacherId()).ifPresent(t -> m.put("teacherName", t.getName()));
        return m;
    }

    @Transactional
    public GraduationDesign updateDesign(Long id, GraduationDesign d) {
        GraduationDesign e = designRepo.findById(id).orElseThrow();
        e.setTopicName(d.getTopicName()); e.setTopicDescription(d.getTopicDescription());
        e.setTeacherComment(d.getTeacherComment()); e.setAnnouncement(d.getAnnouncement());
        e.setScore(d.getScore()); e.setStatus(d.getStatus());
        if (d.getDefenseTime() != null) e.setDefenseTime(d.getDefenseTime());
        return designRepo.save(e);
    }

    @Transactional
    public Map<String, Object> approveDesign(Long id, String status, String comment) {
        GraduationDesign d = designRepo.findById(id).orElseThrow();
        d.setStatus(status); // APPROVED / REJECTED
        if (comment != null) d.setTeacherComment(comment);
        designRepo.save(d);
        return enrichDesign(d);
    }

    /** 学生提交/修改毕业设计选题 */
    @Transactional
    public Map<String, Object> submitDesign(Long studentId, Map<String, Object> body) {
        // 一个学生只能有一个进行中的毕设
        List<GraduationDesign> existing = designRepo.findByStudentId(studentId);
        GraduationDesign d;
        if (!existing.isEmpty()) {
            d = existing.get(0);
            if (!"PENDING".equals(d.getStatus()) && !"REJECTED".equals(d.getStatus())) {
                throw new RuntimeException("已有进行中的毕业设计，无法重复提交");
            }
        } else {
            d = new GraduationDesign();
            d.setStudentId(studentId);
        }
        d.setTeacherId(Long.valueOf(body.get("teacherId").toString()));
        d.setTopicName((String) body.get("topicName"));
        d.setTopicDescription((String) body.get("topicDescription"));
        d.setSemester((String) body.getOrDefault("semester", "2025-2026-2"));
        d.setStatus("PENDING");
        d.setSubmitTime(LocalDateTime.now());
        designRepo.save(d);
        return enrichDesign(d);
    }

    /** 获取学生本学院的教师列表 */
    public List<Map<String, Object>> getDepartmentTeachers(Long studentId) {
        UserProfile sp = userProfileRepo.findById(studentId).orElse(null);
        String dept = sp != null ? sp.getDepartment() : "计算机学院";
        List<UserProfile> profiles = userProfileRepo.findByDepartment(dept);
        List<Map<String, Object>> result = new ArrayList<>();
        for (UserProfile p : profiles) {
            userRepo.findById(p.getUserId()).ifPresent(u -> {
                // 只返回教师角色
                List<Role> roles = userRepo.findRolesByUserId(u.getId());
                boolean isTeacher = roles.stream().anyMatch(r -> "TEACHER".equals(r.getRoleCode()));
                if (isTeacher) {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", u.getId()); m.put("name", u.getName());
                    m.put("title", p.getTitle()); m.put("department", p.getDepartment());
                    result.add(m);
                }
            });
        }
        if (result.isEmpty()) {
            // fallback: return all teachers
            List<User> allUsers = userRepo.findAllUsers();
            for (User u : allUsers) {
                List<Role> roles = userRepo.findRolesByUserId(u.getId());
                if (roles.stream().anyMatch(r -> "TEACHER".equals(r.getRoleCode()))) {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", u.getId()); m.put("name", u.getName());
                    userProfileRepo.findById(u.getId()).ifPresent(p -> m.put("title", p.getTitle()));
                    result.add(m);
                }
            }
        }
        return result;
    }
}
