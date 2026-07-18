package com.smartcampus.smartcampus.service;

import com.smartcampus.smartcampus.dto.Response;
import com.smartcampus.smartcampus.entity.*;
import com.smartcampus.smartcampus.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamArrangementRepository examRepo;
    private final LevelExamRepository levelExamRepo;
    private final LevelExamRegistrationRepository levelRegRepo;
    private final TeachingEvaluationRepository evalRepo;
    private final UserRepository userRepo;
    private final UserProfileRepository userProfileRepo;
    private final CourseRepository courseRepo;

    // ==================== S-08 考试安排查询 ====================
    public List<ExamArrangement> getExamArrangements(String semester) {
        if (semester == null) semester = "2025-2026-2";
        return examRepo.findBySemesterOrderByExamDateAsc(semester);
    }

    /** T-09 教师监考查询 */
    public List<ExamArrangement> getSupervisorExams(Long supervisorId, String semester) {
        if (semester == null) semester = "2025-2026-2";
        return examRepo.findBySupervisorIdAndSemester(supervisorId, semester);
    }

    private final CourseScheduleRepository scheduleRepo;
    private final StudentCourseRepository studentCourseRepo;

    // ==================== S-09 网上评教 ====================
    /** 返回学生本学期所有待评教课程（含已评教记录） */
    public List<Map<String, Object>> getMyEvaluations(Long studentId, String semester) {
        if (semester == null) semester = "2025-2026-2";
        List<StudentCourse> enrolled = studentCourseRepo.findByStudentIdAndSemester(studentId, semester);
        List<TeachingEvaluation> submitted = evalRepo.findByStudentIdAndSemester(studentId, semester);
        Set<Long> evaluatedCourseIds = submitted.stream().map(TeachingEvaluation::getCourseId).collect(java.util.stream.Collectors.toSet());

        List<Map<String, Object>> result = new ArrayList<>();
        Set<Long> seenCourses = new HashSet<>();
        for (StudentCourse sc : enrolled) {
            scheduleRepo.findById(sc.getScheduleId()).ifPresent(cs -> {
                courseRepo.findById(cs.getCourseId()).ifPresent(course -> {
                    if (seenCourses.add(course.getId())) {
                        Map<String, Object> m = new LinkedHashMap<>();
                        m.put("id", course.getId());
                        m.put("courseId", course.getId());
                        m.put("courseName", course.getCourseName());
                        m.put("scheduleId", cs.getId());
                        m.put("teacherId", cs.getTeacherId());
                        userRepo.findById(cs.getTeacherId()).ifPresent(t -> m.put("teacherName", t.getName()));
                        m.put("evaluated", evaluatedCourseIds.contains(course.getId()));
                        // 如果已评教，填充分数
                        submitted.stream().filter(e -> e.getCourseId().equals(course.getId())).findFirst().ifPresent(e -> {
                            Map<String, Integer> scores = new LinkedHashMap<>();
                            scores.put("教学态度", e.getDimension1Score());
                            scores.put("教学内容", e.getDimension2Score());
                            scores.put("教学方法", e.getDimension3Score());
                            scores.put("教学效果", e.getDimension4Score());
                            m.put("scores", scores);
                            m.put("comment", e.getComment());
                            m.put("overallScore", e.getOverallScore());
                        });
                        result.add(m);
                    }
                });
            });
        }
        return result;
    }

    @Transactional
    public Map<String, Object> submitEvaluation(Long studentId, Map<String, Object> body) {
        TeachingEvaluation eval = new TeachingEvaluation();
        eval.setStudentId(studentId);
        eval.setCourseId(Long.valueOf(body.get("courseId").toString()));
        eval.setTeacherId(Long.valueOf(body.get("teacherId").toString()));
        eval.setSemester("2025-2026-2");
        eval.setStatus("SUBMITTED");
        eval.setIsAnonymous(1);
        // scores
        @SuppressWarnings("unchecked")
        Map<String, Object> scores = (Map<String, Object>) body.get("scores");
        if (scores != null) {
            eval.setDimension1Score(toInt(scores.get("教学态度")));
            eval.setDimension2Score(toInt(scores.get("教学内容")));
            eval.setDimension3Score(toInt(scores.get("教学方法")));
            eval.setDimension4Score(toInt(scores.get("教学效果")));
        }
        eval.setComment((String) body.get("comment"));
        // overall
        double avg = (eval.getDimension1Score() + eval.getDimension2Score() + eval.getDimension3Score() + eval.getDimension4Score()) / 4.0;
        eval.setOverallScore(new java.math.BigDecimal(String.format("%.2f", avg)));
        evalRepo.save(eval);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", eval.getId());
        result.put("status", eval.getStatus());
        return result;
    }

    private Integer toInt(Object v) {
        if (v == null) return 3;
        if (v instanceof Integer) return (Integer) v;
        return Integer.parseInt(v.toString());
    }

    /** T-06 教师查看评教结果（带学生名+课程名） */
    public List<Map<String, Object>> getTeacherEvaluations(Long teacherId, String semester) {
        if (semester == null) semester = "2025-2026-2";
        List<TeachingEvaluation> list = evalRepo.findByTeacherIdAndSemester(teacherId, semester);
        List<Map<String, Object>> result = new ArrayList<>();
        for (TeachingEvaluation e : list) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", e.getId()); m.put("studentId", e.getStudentId());
            m.put("teacherId", e.getTeacherId()); m.put("courseId", e.getCourseId());
            m.put("semester", e.getSemester());
            m.put("teachingAttitude", e.getDimension1Score()!=null?e.getDimension1Score():0);
            m.put("teachingContent", e.getDimension2Score()!=null?e.getDimension2Score():0);
            m.put("teachingMethod", e.getDimension3Score()!=null?e.getDimension3Score():0);
            m.put("teachingEffect", e.getDimension4Score()!=null?e.getDimension4Score():0);
            m.put("overallScore", e.getOverallScore());
            m.put("comment", e.getComment());
            m.put("isAnonymous", e.getIsAnonymous());
            userRepo.findById(e.getStudentId()).ifPresent(u -> m.put("studentName", u.getName()));
            courseRepo.findById(e.getCourseId()).ifPresent(c -> m.put("courseName", c.getCourseName()));
            result.add(m);
        }
        return result;
    }

    /** L-12 评教统计 */
    public List<Response.EvalStat> evaluationStats(String department) {
        List<Object[]> raw = evalRepo.statsByTeacher();
        List<Response.EvalStat> result = new ArrayList<>();
        for (Object[] row : raw) {
            Long teacherId = (Long) row[0];
            double avg = row[1] != null ? ((Number) row[1]).doubleValue() : 0;
            String name = "", dept = "";
            if (teacherId != null) {
                User u = userRepo.findById(teacherId).orElse(null);
                if (u != null) name = u.getName();
                UserProfile up = userProfileRepo.findById(teacherId).orElse(null);
                if (up != null) dept = up.getDepartment();
            }
            if (department != null && !department.isEmpty() && !department.equals(dept)) continue;
            Response.EvalStat s = new Response.EvalStat();
            s.setTeacherName(name);
            s.setDepartment(dept);
            s.setAvgScore(Math.round(avg * 10) / 10.0);
            result.add(s);
        }
        result.sort((a, b) -> Double.compare(b.getAvgScore(), a.getAvgScore()));
        return result;
    }

    // ==================== S-11 等级考试报名 ====================
    public List<LevelExam> listLevelExams(String status) {
        if (status != null) return levelExamRepo.findByStatus(status);
        return levelExamRepo.findAll();
    }

    @Transactional
    public LevelExamRegistration registerLevelExam(Long examId, Long studentId) {
        LevelExamRegistration reg = new LevelExamRegistration();
        reg.setExamId(examId); reg.setStudentId(studentId);
        reg.setRegistrationTime(LocalDateTime.now());
        reg.setPaymentStatus(0);
        return levelRegRepo.save(reg);
    }

    public List<Map<String, Object>> getMyExamRegistrations(Long studentId) {
        List<LevelExamRegistration> list = levelRegRepo.findByStudentId(studentId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (LevelExamRegistration reg : list) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", reg.getId()); m.put("examId", reg.getExamId());
            m.put("studentId", reg.getStudentId());
            m.put("registrationTime", reg.getRegistrationTime());
            m.put("paymentStatus", reg.getPaymentStatus());
            m.put("examStatus", reg.getExamStatus());
            m.put("score", reg.getScore()); m.put("isPassed", reg.getIsPassed());
            levelExamRepo.findById(reg.getExamId()).ifPresent(e -> {
                m.put("examName", e.getExamName());
                m.put("examDate", e.getExamDate());
                m.put("fee", e.getFee());
            });
            result.add(m);
        }
        return result;
    }

    // ==================== A-12 管理员考试管理 ====================
    @Transactional
    public ExamArrangement saveExamArrangement(ExamArrangement e) { return examRepo.save(e); }

    @Transactional
    public ExamArrangement updateExamArrangement(Long id, ExamArrangement e) {
        ExamArrangement existing = examRepo.findById(id).orElseThrow();
        existing.setExamName(e.getExamName()); existing.setExamDate(e.getExamDate());
        existing.setExamTimeStart(e.getExamTimeStart()); existing.setExamTimeEnd(e.getExamTimeEnd());
        existing.setLocation(e.getLocation()); existing.setSupervisorId(e.getSupervisorId());
        existing.setSupervisorName(e.getSupervisorName()); existing.setStatus(e.getStatus());
        return examRepo.save(existing);
    }

    @Transactional
    public void deleteExamArrangement(Long id) { examRepo.deleteById(id); }

    @Transactional
    public LevelExam saveLevelExam(LevelExam e) { return levelExamRepo.save(e); }

    @Transactional
    public void deleteLevelExam(Long id) { levelExamRepo.deleteById(id); }
}
