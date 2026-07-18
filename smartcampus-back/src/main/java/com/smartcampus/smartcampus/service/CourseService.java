package com.smartcampus.smartcampus.service;

import com.smartcampus.smartcampus.dto.Response;
import com.smartcampus.smartcampus.entity.*;
import com.smartcampus.smartcampus.repository.*;
import com.smartcampus.smartcampus.repository.UserRepository;
import com.smartcampus.smartcampus.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepo;
    private final CourseScheduleRepository scheduleRepo;
    private final StudentCourseRepository studentCourseRepo;
    private final StudentRetakeRepository retakeRepo;
    private final ScoreChangeLogRepository scoreLogRepo;
    private final UserRepository userRepo;
    private final UserProfileRepository userProfileRepo;

    // ==================== S-05 学生课表 ====================
    public List<CourseSchedule> getStudentSchedule(Long studentId, String semester) {
        if (semester == null) semester = "2025-2026-2";
        List<StudentCourse> enrolled = studentCourseRepo.findByStudentIdAndSemester(studentId, semester)
                .stream().filter(sc -> !"DROPPED".equals(sc.getStatus())).collect(Collectors.toList());
        List<Long> scheduleIds = enrolled.stream().map(StudentCourse::getScheduleId).collect(Collectors.toList());
        if (scheduleIds.isEmpty()) return Collections.emptyList();
        List<CourseSchedule> schedules = scheduleRepo.findAllById(scheduleIds);
        enrichSchedules(schedules);
        schedules.sort(Comparator.comparingInt(CourseSchedule::getWeekday)
                .thenComparingInt(CourseSchedule::getSectionStart));
        return schedules;
    }

    /** 填充课程名和教师名 */
    private void enrichSchedules(List<CourseSchedule> schedules) {
        for (CourseSchedule cs : schedules) {
            courseRepo.findById(cs.getCourseId()).ifPresent(c -> cs.setCourseName(c.getCourseName()));
            userRepo.findById(cs.getTeacherId()).ifPresent(t -> cs.setTeacherName(t.getName()));
        }
    }

    // ==================== S-06 课程情况总览 ====================
    public Map<String, Object> getCourseOverview(Long studentId) {
        List<StudentCourse> all = studentCourseRepo.findAllByStudentId(studentId)
                .stream().filter(sc -> !"DROPPED".equals(sc.getStatus())).collect(Collectors.toList());
        long passed = all.stream().filter(sc -> sc.getScore() != null && sc.getScore().compareTo(new BigDecimal("60")) >= 0).count();
        long failed = all.stream().filter(sc -> sc.getScore() != null && sc.getScore().compareTo(new BigDecimal("60")) < 0).count();
        double avg = all.stream().filter(sc -> sc.getScore() != null).mapToDouble(sc -> sc.getScore().doubleValue()).average().orElse(0);
        Map<String, Object> overview = new LinkedHashMap<>();
        overview.put("totalCourses", all.size());
        overview.put("passed", (int) passed);
        overview.put("failed", (int) failed);
        overview.put("avgScore", Math.round(avg * 10) / 10.0);
        overview.put("gpa", null); // simplified
        return overview;
    }

    // ==================== S-07 成绩查询（带课程名） ====================
    public List<Map<String, Object>> getMyScores(Long studentId, String semester) {
        List<StudentCourse> list;
        if (semester != null && !semester.isEmpty()) list = studentCourseRepo.findByStudentIdAndSemester(studentId, semester);
        else list = studentCourseRepo.findAllByStudentId(studentId);
        // 过滤掉已退课的
        list = list.stream().filter(sc -> !"DROPPED".equals(sc.getStatus())).collect(Collectors.toList());
        List<Map<String, Object>> result = new ArrayList<>();
        for (StudentCourse sc : list) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", sc.getId()); m.put("score", sc.getScore());
            m.put("gradeLevel", sc.getGradeLevel()); m.put("examType", sc.getExamType());
            m.put("isRetake", sc.getIsRetake()); m.put("isMakeup", sc.getIsMakeup());
            m.put("semester", sc.getSemester());
            // 填充课程名
            scheduleRepo.findById(sc.getScheduleId()).ifPresent(cs -> {
                m.put("scheduleId", cs.getId());
                m.put("className", cs.getClassName());
                m.put("classroom", cs.getClassroom());
                courseRepo.findById(cs.getCourseId()).ifPresent(c -> {
                    m.put("courseName", c.getCourseName());
                    m.put("courseCode", c.getCourseCode());
                });
                userRepo.findById(cs.getTeacherId()).ifPresent(t -> m.put("teacherName", t.getName()));
            });
            result.add(m);
        }
        return result;
    }

    // ==================== S-10 补考/重修报名 ====================
    public List<Map<String, Object>> getMyRetakes(Long studentId) {
        List<StudentRetake> list = retakeRepo.findByStudentIdOrderByApplicationTimeDesc(studentId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (StudentRetake r : list) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", r.getId()); m.put("studentId", r.getStudentId());
            m.put("courseId", r.getCourseId()); m.put("semester", r.getSemester());
            m.put("type", r.getType()); m.put("originalScore", r.getOriginalScore());
            m.put("status", r.getStatus()); m.put("applicationTime", r.getApplicationTime());
            m.put("paymentStatus", r.getPaymentStatus());
            courseRepo.findById(r.getCourseId()).ifPresent(c -> m.put("courseName", c.getCourseName()));
            result.add(m);
        }
        return result;
    }

    @Transactional
    public Map<String, Object> applyRetake(Long studentId, Map<String, Object> body) {
        StudentRetake retake = new StudentRetake();
        retake.setStudentId(studentId);
        retake.setCourseId(Long.valueOf(body.get("courseId").toString()));
        retake.setSemester((String) body.getOrDefault("semester", "2025-2026-2"));
        retake.setType((String) body.getOrDefault("type", "MAKEUP"));
        retake.setOriginalScore(body.get("originalScore") != null ? new BigDecimal(body.get("originalScore").toString()) : null);
        retake.setApplicationTime(LocalDateTime.now());
        retake.setStatus("PENDING");
        retake.setPaymentStatus(0);
        retakeRepo.save(retake);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", retake.getId());
        result.put("status", "PENDING");
        return result;
    }

    @Transactional
    public StudentRetake approveRetake(Long id, String status) {
        StudentRetake r = retakeRepo.findById(id).orElseThrow();
        r.setStatus(status);
        if ("APPROVED".equals(status)) r.setApprovalTime(LocalDateTime.now());
        return retakeRepo.save(r);
    }

    // ==================== T-04 教师任课信息 ====================
    public List<CourseSchedule> getTeacherSchedule(Long teacherId, String semester) {
        if (semester == null) semester = "2025-2026-2";
        List<CourseSchedule> schedules = scheduleRepo.findByTeacherIdAndSemester(teacherId, semester);
        enrichSchedules(schedules);
        return schedules;
    }

    /** T-11 班级名单（带学生姓名/学号/班级） */
    public List<Map<String, Object>> getClassRoster(Long scheduleId) {
        List<StudentCourse> enrolled = studentCourseRepo.findByScheduleId(scheduleId);
        List<Map<String, Object>> roster = new ArrayList<>();
        for (StudentCourse sc : enrolled) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", sc.getId());
            item.put("studentCourseId", sc.getId());
            item.put("studentId", sc.getStudentId());
            item.put("score", sc.getScore());
            item.put("gradeLevel", sc.getGradeLevel());
            item.put("examType", sc.getExamType());
            item.put("scheduleId", sc.getScheduleId());
            // 填学生信息
            userRepo.findById(sc.getStudentId()).ifPresent(u -> {
                item.put("studentName", u.getName());
                item.put("studentNo", u.getIdentityNumber());
            });
            userProfileRepo.findById(sc.getStudentId()).ifPresent(p -> {
                item.put("className", p.getClassName() != null ? p.getClassName() : (p.getDepartment() + p.getMajor()));
                item.put("department", p.getDepartment());
                item.put("major", p.getMajor());
            });
            roster.add(item);
        }
        return roster;
    }

    // ==================== T-05 成绩录入 ====================
    @Transactional
    public StudentCourse enterScore(Long studentCourseId, BigDecimal score,
                                     String gradeLevel, String examType, Long operatorId, String operatorName) {
        StudentCourse sc = studentCourseRepo.findById(studentCourseId).orElseThrow();
        if (sc.getScore() != null) {
            ScoreChangeLog log = new ScoreChangeLog();
            log.setStudentCourseId(studentCourseId);
            log.setOldScore(sc.getScore());
            log.setNewScore(score);
            log.setChangeReason("成绩修改");
            log.setOperatorId(operatorId);
            log.setOperatorName(operatorName);
            scoreLogRepo.save(log);
        }
        sc.setScore(score); sc.setGradeLevel(gradeLevel); sc.setExamType(examType);
        return studentCourseRepo.save(sc);
    }

    // ==================== A-09 课程管理（管理员） ====================
    public List<Course> listAllCourses(String department, String courseType) {
        if (department != null) return courseRepo.findByDepartment(department);
        if (courseType != null) return courseRepo.findByCourseType(courseType);
        return courseRepo.findAll();
    }

    @Transactional
    public Course saveCourse(Course c) { return courseRepo.save(c); }

    @Transactional
    public Course updateCourse(Long id, Course c) {
        Course e = courseRepo.findById(id).orElseThrow();
        e.setCourseName(c.getCourseName()); e.setCredit(c.getCredit());
        e.setHours(c.getHours()); e.setCourseType(c.getCourseType());
        e.setDepartment(c.getDepartment()); e.setDescription(c.getDescription());
        return courseRepo.save(e);
    }

    @Transactional
    public void deleteCourse(Long id) { courseRepo.deleteById(id); }

    // ==================== A-10 教师任课分配 ====================
    public List<CourseSchedule> listAllSchedules(String semester) {
        List<CourseSchedule> list;
        if (semester != null) list = scheduleRepo.findBySemesterOrderByWeekdayAsc(semester);
        else list = scheduleRepo.findAll();
        enrichSchedules(list);
        return list;
    }

    @Transactional
    public CourseSchedule saveSchedule(CourseSchedule s) { return scheduleRepo.save(s); }

    @Transactional
    public void deleteSchedule(Long id) { scheduleRepo.deleteById(id); }

    // ==================== A-11 学生选课管理 ====================
    public List<StudentCourse> listStudentCourses(Long studentId, String semester) {
        if (studentId != null) return studentCourseRepo.findByStudentIdAndSemester(studentId, semester);
        return studentCourseRepo.findAll();
    }

    @Transactional
    public StudentCourse enrollStudent(Long studentId, Long scheduleId, String semester) {
        // 检查是否已有记录（含DROPPED）
        List<StudentCourse> existing = studentCourseRepo.findByStudentIdAndSemester(studentId, semester);
        for (StudentCourse sc : existing) {
            if (sc.getScheduleId().equals(scheduleId)) {
                if ("DROPPED".equals(sc.getStatus())) {
                    // 重新激活
                    sc.setStatus("ENROLLED"); sc.setSelectTime(LocalDateTime.now()); sc.setDropTime(null);
                    return studentCourseRepo.save(sc);
                }
                throw new RuntimeException("已选过该课程，请勿重复选课");
            }
        }
        StudentCourse sc = new StudentCourse();
        sc.setStudentId(studentId); sc.setScheduleId(scheduleId);
        sc.setSemester(semester); sc.setStatus("ENROLLED");
        sc.setSelectTime(LocalDateTime.now());
        return studentCourseRepo.save(sc);
    }

    @Transactional
    public void dropStudent(Long id) {
        // 直接删除记录，方便重新选课
        studentCourseRepo.deleteById(id);
    }

    /** T-13 教师查看所授课程选课情况 */
    public List<Map<String, Object>> getTeacherEnrollment(Long teacherId, String semester) {
        List<CourseSchedule> schedules = scheduleRepo.findByTeacherIdAndSemester(teacherId, semester);
        List<Map<String, Object>> result = new ArrayList<>();
        for (CourseSchedule s : schedules) {
            // 补充课程信息
            Course course = s.getCourseId() != null ? courseRepo.findById(s.getCourseId()).orElse(null) : null;
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("scheduleId", s.getId());
            m.put("courseName", s.getCourseName() != null ? s.getCourseName() : (course != null ? course.getCourseName() : ""));
            m.put("courseCode", course != null ? course.getCourseCode() : "");
            m.put("className", s.getClassName());
            m.put("weekday", s.getWeekday());
            m.put("sectionStart", s.getSectionStart());
            m.put("sectionEnd", s.getSectionEnd());
            m.put("classroom", s.getClassroom());
            List<StudentCourse> enrollments = studentCourseRepo.findByScheduleId(s.getId());
            long enrolledCount = enrollments.stream().filter(e -> !"DROPPED".equals(e.getStatus())).count();
            m.put("enrolledCount", (int) enrolledCount);
            m.put("maxStudents", s.getMaxStudents());
            List<Map<String, Object>> students = enrollments.stream()
                    .filter(e -> !"DROPPED".equals(e.getStatus()))
                    .map(e -> {
                        Map<String, Object> sm = new LinkedHashMap<>();
                        sm.put("studentCourseId", e.getId());
                        sm.put("studentId", e.getStudentId());
                        // 查询学生姓名
                        String studentName = "", studentNo = "", className = "";
                        if (e.getStudentId() != null) {
                            User u = userRepo.findById(e.getStudentId()).orElse(null);
                            if (u != null) {
                                studentName = u.getName();
                                studentNo = u.getIdentityNumber();
                            }
                            UserProfile up = userProfileRepo.findById(e.getStudentId()).orElse(null);
                            if (up != null) className = up.getClassName();
                        }
                        sm.put("studentName", studentName);
                        sm.put("studentNo", studentNo);
                        sm.put("className", className);
                        sm.put("status", e.getStatus());
                        sm.put("score", e.getScore());
                        sm.put("gradeLevel", e.getGradeLevel());
                        return sm;
                    }).collect(Collectors.toList());
            m.put("students", students);
            result.add(m);
        }
        return result;
    }
}
