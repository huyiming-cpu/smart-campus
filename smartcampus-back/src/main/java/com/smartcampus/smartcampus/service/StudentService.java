package com.smartcampus.smartcampus.service;

import com.smartcampus.smartcampus.entity.*;
import com.smartcampus.smartcampus.repository.*;
import com.smartcampus.smartcampus.repository.UserRepository;
import com.smartcampus.smartcampus.repository.UserProfileRepository;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentApplicationRepository appRepo;
    private final CompetitionRegistrationRepository compRepo;
    private final ConsultationAppointmentRepository consultRepo;
    private final PracticeProjectRegistrationRepository practiceRepo;
    private final UserRepository userRepo;
    private final UserProfileRepository userProfileRepo;

    // ==================== 学生申请 S-14 (奖学金/助学金/贷款/困难生) ====================

    @Transactional
    public StudentApplication submitApplication(StudentApplication app) {
        app.setSubmitTime(LocalDateTime.now());
        app.setStatus("PENDING");
        return appRepo.save(app);
    }

    /** T-12: 教师/辅导员 审核学生申请 */
    @Transactional
    public StudentApplication reviewApplication(Long id, String status, String comment, Long reviewerId) {
        StudentApplication app = appRepo.findById(id).orElseThrow(() -> new RuntimeException("申请不存在"));
        app.setStatus(status);
        app.setReviewComment(comment);
        app.setReviewerId(reviewerId);
        app.setReviewTime(LocalDateTime.now());
        return appRepo.save(app);
    }

    /** 辅导员：待审核 + 已审核（只看本院学生） */
    public List<Map<String, Object>> listPendingApplications(String department) {
        List<StudentApplication> raw = appRepo.findByStatusOrderBySubmitTimeDesc("PENDING");
        if (department != null) raw = filterByDept(raw, department);
        return enrichApplications(raw);
    }

    public List<Map<String, Object>> listReviewedApplications(String department) {
        List<StudentApplication> list = new ArrayList<>();
        list.addAll(appRepo.findByStatusOrderBySubmitTimeDesc("FIRST_APPROVED"));
        list.addAll(appRepo.findByStatusOrderBySubmitTimeDesc("APPROVED"));
        list.addAll(appRepo.findByStatusOrderBySubmitTimeDesc("REJECTED"));
        if (department != null) list = filterByDept(list, department);
        return enrichApplications(list);
    }

    /** 院长二审：查看辅导员已通过的申请（按部门过滤） */
    public List<Map<String, Object>> listFirstApprovedApplications(String department) {
        List<StudentApplication> raw = appRepo.findByStatusOrderBySubmitTimeDesc("FIRST_APPROVED");
        if (department != null) raw = filterByDept(raw, department);
        return enrichApplications(raw);
    }

    /** 院长：已审批的申请（按部门过滤） */
    public List<Map<String, Object>> listFinalReviewedApplications(String department) {
        List<StudentApplication> list = new ArrayList<>();
        list.addAll(appRepo.findByStatusOrderBySubmitTimeDesc("APPROVED"));
        list.addAll(appRepo.findByStatusOrderBySubmitTimeDesc("REJECTED"));
        if (department != null) list = filterByDept(list, department);
        return enrichApplications(list);
    }

    private List<StudentApplication> filterByDept(List<StudentApplication> apps, String dept) {
        return apps.stream().filter(a -> {
            return userProfileRepo.findById(a.getStudentId()).map(p -> dept.equals(p.getDepartment())).orElse(false);
        }).collect(java.util.stream.Collectors.toList());
    }

    /** 院长最终审批 */
    @Transactional
    public Map<String, Object> finalApprove(Long id, String status, String comment, Long reviewerId) {
        StudentApplication app = appRepo.findById(id).orElseThrow();
        app.setStatus(status); // APPROVED / REJECTED
        app.setReviewComment((app.getReviewComment()!=null?app.getReviewComment()+"; ":"") + "院长:" + comment);
        app.setReviewTime(LocalDateTime.now());
        appRepo.save(app);
        return enrichApplications(List.of(app)).get(0);
    }

    public List<Map<String, Object>> listMyApplications(Long studentId, String type) {
        List<StudentApplication> list;
        if (type != null) list = appRepo.findByApplicationTypeOrderBySubmitTimeDesc(type);
        else list = appRepo.findByStudentIdOrderBySubmitTimeDesc(studentId);
        return enrichApplications(list);
    }

    private List<Map<String, Object>> enrichApplications(List<StudentApplication> list) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (StudentApplication app : list) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", app.getId()); m.put("studentId", app.getStudentId());
            m.put("applicationType", app.getApplicationType());
            m.put("type", app.getApplicationType());
            m.put("title", app.getTitle()); m.put("content", app.getContent());
            m.put("semester", app.getSemester()); m.put("status", app.getStatus());
            m.put("reviewComment", app.getReviewComment());
            m.put("submitTime", app.getSubmitTime()); m.put("reviewTime", app.getReviewTime());
            m.put("reviewerId", app.getReviewerId());
            userRepo.findById(app.getStudentId()).ifPresent(u -> {
                m.put("studentName", u.getName());
                m.put("studentNo", u.getIdentityNumber());
            });
            userProfileRepo.findById(app.getStudentId()).ifPresent(p -> {
                m.put("department", p.getDepartment());
                m.put("major", p.getMajor());
                m.put("className", p.getClassName());
                m.put("grade", p.getGrade());
            });
            result.add(m);
        }
        return result;
    }

    // ==================== 竞赛报名 S-14 ====================
    public List<CompetitionRegistration> listMyCompetitions(Long studentId) {
        return compRepo.findByStudentIdOrderByRegistrationTimeDesc(studentId);
    }

    @Transactional
    public CompetitionRegistration registerCompetition(CompetitionRegistration c) {
        c.setRegistrationTime(LocalDateTime.now());
        c.setStatus("PENDING");
        return compRepo.save(c);
    }

    @Transactional
    public CompetitionRegistration updateCompetitionStatus(Long id, String status) {
        CompetitionRegistration c = compRepo.findById(id).orElseThrow();
        c.setStatus(status);
        return compRepo.save(c);
    }

    // ==================== 心理咨询预约 S-14 ====================
    public List<ConsultationAppointment> listMyConsultations(Long studentId) {
        return consultRepo.findByStudentIdOrderByAppointmentDateDesc(studentId);
    }

    @Transactional
    public ConsultationAppointment bookConsultation(ConsultationAppointment c) {
        c.setStatus("PENDING");
        return consultRepo.save(c);
    }

    @Transactional
    public ConsultationAppointment updateConsultation(Long id, String status, String feedback) {
        ConsultationAppointment c = consultRepo.findById(id).orElseThrow();
        c.setStatus(status);
        if (feedback != null) c.setFeedback(feedback);
        return consultRepo.save(c);
    }

    // ==================== 实践项目预约 S-14 ====================
    public List<PracticeProjectRegistration> listMyPractices(Long studentId) {
        return practiceRepo.findByStudentIdOrderByRegistrationTimeDesc(studentId);
    }

    @Transactional
    public PracticeProjectRegistration registerPractice(PracticeProjectRegistration p) {
        p.setRegistrationTime(LocalDateTime.now());
        p.setStatus("PENDING");
        return practiceRepo.save(p);
    }

    @Transactional
    public PracticeProjectRegistration updatePracticeStatus(Long id, String status) {
        PracticeProjectRegistration p = practiceRepo.findById(id).orElseThrow();
        p.setStatus(status);
        return practiceRepo.save(p);
    }
}
