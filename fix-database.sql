-- ============================================================
-- 智慧校园数据库补全 v2
-- ============================================================
USE smart_campus;

-- ============================================================
-- 1. 权限表
-- ============================================================
INSERT INTO permission (id, parent_id, permission_name, permission_code, permission_type, url, sort_order, status, created_time) VALUES
(1,  0, '仪表盘',      'DASHBOARD',     'MENU', '/dashboard',          1, 1, NOW()),
(2,  0, '用户管理',    'USER_MANAGE',   'MENU', '/users',              2, 1, NOW()),
(3,  0, '角色权限',    'ROLE_MANAGE',   'MENU', '/roles',              3, 1, NOW()),
(4,  0, '注册审核',    'REGISTER_AUDIT','MENU', '/register-audit',     4, 1, NOW()),
(5,  0, '公告管理',    'ANNOUNCEMENT',  'MENU', '/announcements',      5, 1, NOW()),
(6,  0, '新闻管理',    'NEWS_MANAGE',   'MENU', '/news',               6, 1, NOW()),
(7,  0, '课程管理',    'COURSE_MANAGE', 'MENU', '/courses',            7, 1, NOW()),
(8,  0, '排课管理',    'SCHEDULE_MANAGE','MENU', '/schedules',         8, 1, NOW()),
(9,  0, '成绩录入',    'SCORE_ENTER',   'MENU', '/scores-enter',       9, 1, NOW()),
(10, 0, '成绩查看',    'SCORE_VIEW',    'MENU', '/scores',            10, 1, NOW()),
(11, 0, '课表查看',    'SCHEDULE_VIEW', 'MENU', '/schedule',          11, 1, NOW()),
(12, 0, '考试管理',    'EXAM_MANAGE',   'MENU', '/exams',             12, 1, NOW()),
(13, 0, '教学评价',    'EVALUATION',    'MENU', '/evaluation',        13, 1, NOW()),
(14, 0, '一卡通管理',  'CARD_MANAGE',   'MENU', '/cards',             14, 1, NOW()),
(15, 0, '一卡通查看',  'CARD_VIEW',     'MENU', '/card',              15, 1, NOW()),
(16, 0, '缴费管理',    'PAYMENT_MANAGE','MENU', '/payments',          16, 1, NOW()),
(17, 0, '科研管理',    'RESEARCH',      'MENU', '/research',          17, 1, NOW()),
(18, 0, '毕设管理',    'GRADUATION',    'MENU', '/graduation',        18, 1, NOW()),
(19, 0, '公文提交',    'DOC_SUBMIT',    'MENU', '/documents',         19, 1, NOW()),
(20, 0, '公文审批',    'DOC_APPROVE',   'MENU', '/documents-approve', 20, 1, NOW()),
(21, 0, '工作计划',    'WORKPLAN',      'MENU', '/work-plans',        21, 1, NOW()),
(22, 0, '统计查看',    'STATS_VIEW',    'MENU', '/stats',             22, 1, NOW()),
(23, 0, '固定资产',    'ASSET_MANAGE',  'MENU', '/assets',            23, 1, NOW()),
(24, 0, '实验室管理',  'LAB_MANAGE',    'MENU', '/labs',              24, 1, NOW()),
(25, 0, '日志审计',    'LOG_AUDIT',     'MENU', '/logs',              25, 1, NOW()),
(26, 0, '系统配置',    'CONFIG_MANAGE', 'MENU', '/config',            26, 1, NOW()),
(27, 0, 'AI知识库',    'AI_KNOWLEDGE',  'MENU', '/ai-knowledge',      27, 1, NOW()),
(28, 0, '个人信息',    'PROFILE_EDIT',  'MENU', '/profile',           28, 1, NOW()),
(29, 0, '通讯录',      'DIRECTORY',     'MENU', '/directory',         29, 1, NOW()),
(30, 0, '学生申请',    'APPLICATION',   'MENU', '/applications',      30, 1, NOW()),
(31, 0, '申请审核',    'APPLY_REVIEW',  'MENU', '/applications-review',31, 1, NOW());

-- ============================================================
-- 2. 角色-权限关联
-- ============================================================
-- ADMIN(4): 所有31个权限
INSERT INTO role_permission (role_id, permission_id, created_time)
SELECT 4, id, NOW() FROM permission;

-- STUDENT(1): 基础权限
INSERT INTO role_permission (role_id, permission_id, created_time) VALUES
(1,1,NOW()),(1,10,NOW()),(1,11,NOW()),(1,13,NOW()),(1,15,NOW()),(1,28,NOW()),(1,30,NOW());

-- TEACHER(2): 教学+科研权限
INSERT INTO role_permission (role_id, permission_id, created_time) VALUES
(2,1,NOW()),(2,9,NOW()),(2,10,NOW()),(2,11,NOW()),(2,13,NOW()),
(2,17,NOW()),(2,18,NOW()),(2,19,NOW()),(2,28,NOW()),(2,31,NOW());

-- LEADER(3): 管理+统计权限
INSERT INTO role_permission (role_id, permission_id, created_time) VALUES
(3,1,NOW()),(3,10,NOW()),(3,11,NOW()),(3,13,NOW()),(3,17,NOW()),
(3,20,NOW()),(3,21,NOW()),(3,22,NOW()),(3,23,NOW()),(3,28,NOW()),(3,29,NOW());

-- ============================================================
-- 3. 密码历史
-- ============================================================
INSERT INTO password_history (user_id, password_hash, created_time) VALUES
(1, '$2a$10$qt.pr.Z3Xq9LuykYhuvZZei0Hf3cuZhOlH3TyWaMDVgwy4aUukgKu', '2025-09-01 10:00:00'),
(1, '$2a$10$pXH7vN/JLXa3cr/OtTYhOCavShiG20NXsdKJurFT4pBFucZ4AXya', '2026-02-20 10:00:00'),
(2, '$2a$10$pXH7vN/JLXa3cr/OtTYhOCavShiG20NXsdKJurFT4pBFucZ4AXya', '2025-09-01 10:00:00'),
(4, '$2a$10$BIC2Pufr3CFnUCzZ6GMOH.k0HyBL/be1p7kkY/Vawl41qCf/tkYsG', '2025-09-01 10:00:00');

-- ============================================================
-- 4. 补充更多学生选课（已存在的 skip）
-- ============================================================
INSERT IGNORE INTO student_course (student_id, schedule_id, semester, status, score, gpa, grade_level, exam_type, is_retake, is_makeup, select_time) VALUES
(5, 1, '2025-2026-2', 'ENROLLED', 82.0, 3.3, 'B', 'FINAL', 0, 0, '2026-02-21 09:00:00'),
(5, 5, '2025-2026-2', 'ENROLLED', 95.0, 4.0, 'A', 'FINAL', 0, 0, '2026-02-21 09:00:00'),
(5, 9, '2025-2026-2', 'ENROLLED', 70.0, 2.0, 'C', 'FINAL', 0, 0, '2026-02-21 09:00:00'),
(6, 2, '2025-2026-2', 'ENROLLED', 89.0, 3.7, 'B', 'FINAL', 0, 0, '2026-02-21 09:00:00'),
(6, 3, '2025-2026-2', 'ENROLLED', 81.0, 3.3, 'B', 'FINAL', 0, 0, '2026-02-21 09:00:00'),
(6, 6, '2025-2026-2', 'ENROLLED', 78.0, 3.0, 'C', 'FINAL', 0, 0, '2026-02-21 09:00:00'),
(6, 12, '2025-2026-2', 'ENROLLED', NULL, NULL, NULL, NULL, 0, 0, '2026-02-21 09:00:00'),
(9, 1, '2025-2026-2', 'ENROLLED', 75.0, 2.5, 'C', 'FINAL', 0, 0, '2026-02-22 09:00:00'),
(9, 5, '2025-2026-2', 'ENROLLED', 88.0, 3.7, 'B', 'FINAL', 0, 0, '2026-02-22 09:00:00'),
(10, 1, '2025-2026-2', 'ENROLLED', 90.0, 4.0, 'A', 'FINAL', 0, 0, '2026-02-22 09:00:00'),
(10, 5, '2025-2026-2', 'ENROLLED', 84.0, 3.3, 'B', 'FINAL', 0, 0, '2026-02-22 09:00:00');

-- ============================================================
-- 5. 补充更多消费记录
-- ============================================================
INSERT INTO card_transaction (user_id, card_no, transaction_type, amount, balance, merchant, transaction_time, transaction_no, sync_status) VALUES
(1, '202311258801', 'CONSUME', 25.00, 231.50, '第二食堂一楼2号窗口', '2026-07-12 12:05:00', 'TXN-B01', 1),
(1, '202311258801', 'CONSUME', 6.50,  225.00, '校园超市B区',       '2026-07-11 18:00:00', 'TXN-B02', 1),
(1, '202311258801', 'CONSUME', 18.00, 207.00, '第一食堂三楼',       '2026-07-10 12:00:00', 'TXN-B03', 1),
(1, '202311258801', 'CONSUME', 12.00, 195.00, '图书馆咖啡吧',       '2026-07-09 15:00:00', 'TXN-B04', 1),
(7, '202311200301', 'CONSUME', 35.00, 1045.00, '第二食堂一楼',      '2026-07-14 12:00:00', 'TXN-B05', 1),
(7, '202311200301', 'CONSUME', 8.00,  1037.00, '校园超市',          '2026-07-13 16:00:00', 'TXN-B06', 1),
(7, '202311200301', 'CONSUME', 22.00, 1015.00, '第一食堂三楼',       '2026-07-12 18:00:00', 'TXN-B07', 1),
(8, '202311200401', 'RECHARGE', 200.00, 245.30, '支付宝充值',       '2026-07-14 08:00:00', 'TXN-B08', 1),
(8, '202311200401', 'CONSUME', 15.00, 230.30, '第一食堂二楼',       '2026-07-14 12:00:00', 'TXN-B09', 1),
(8, '202311200401', 'CONSUME', 28.00, 202.30, '第二食堂特色窗口',   '2026-07-13 17:00:00', 'TXN-B10', 1);

-- ============================================================
-- 6. 更新 card 余额
-- ============================================================
UPDATE card SET balance = 195.00 WHERE user_id = 1;
UPDATE card SET balance = 1015.00 WHERE user_id = 7;
UPDATE card SET balance = 202.30 WHERE user_id = 8;

-- ============================================================
-- 7. 补充缴费
-- ============================================================
INSERT INTO payment_record (student_id, semester, payment_type, total_amount, paid_amount, payment_status, payment_time, payment_method, payment_no, remark) VALUES
(5, '2025-2026-1', 'TUITION', 5000.00, 5000.00, 'PAID', '2025-09-01 10:00:00', 'ALIPAY', 'PAY-L01', '李明远'),
(5, '2025-2026-2', 'TUITION', 5000.00, 5000.00, 'PAID', '2026-02-20 10:00:00', 'WECHAT', 'PAY-L02', '李明远'),
(6, '2025-2026-1', 'TUITION', 5000.00, 5000.00, 'PAID', '2025-09-01 11:00:00', 'ALIPAY', 'PAY-W01', NULL),
(6, '2025-2026-2', 'TUITION', 5000.00, 2000.00, 'PARTIAL', '2026-02-20 11:00:00', 'WECHAT', 'PAY-W02', NULL),
(9, '2025-2026-1', 'TUITION', 5000.00, 5000.00, 'PAID', '2025-09-03 09:00:00', 'ALIPAY', 'PAY-C01', NULL),
(9, '2025-2026-2', 'TUITION', 5000.00, 0.00, 'UNPAID', NULL, NULL, NULL, NULL),
(10,'2025-2026-1', 'TUITION', 5000.00, 5000.00, 'PAID', '2025-09-02 14:00:00', 'ALIPAY', 'PAY-Z01', NULL),
(10,'2025-2026-2', 'TUITION', 5000.00, 5000.00, 'PAID', '2026-02-21 09:00:00', 'WECHAT', 'PAY-Z02', NULL),
(1, '2025-2026-1', 'DORM', 1200.00, 1200.00, 'PAID', '2025-09-01 10:00:00', 'ALIPAY', 'DORM-01', NULL),
(5, '2025-2026-1', 'DORM', 1200.00, 1200.00, 'PAID', '2025-09-01 10:00:00', 'ALIPAY', 'DORM-02', NULL),
(7, '2025-2026-1', 'DORM', 1200.00, 1200.00, 'PAID', '2025-09-02 09:00:00', 'WECHAT', 'DORM-03', NULL),
(7, '2025-2026-2', 'DORM', 1200.00, 0.00, 'UNPAID', NULL, NULL, NULL, NULL);

-- ============================================================
-- 8. 补充更多评教
-- ============================================================
INSERT INTO teaching_evaluation (student_id, teacher_id, course_id, semester, dimension1_score, dimension2_score, dimension3_score, dimension4_score, overall_score, comment, is_anonymous, status) VALUES
(5, 14, 1, '2025-2026-2', 5, 5, 5, 4, 4.75, '张老师课堂氛围活跃，讲解清晰！', 1, 'SUBMITTED'),
(6, 15, 2, '2025-2026-2', 4, 5, 4, 4, 4.25, '李老师很负责，课后答疑很耐心', 1, 'SUBMITTED'),
(5, 15, 4, '2025-2026-2', 4, 4, 3, 4, 3.75, '希望增加更多实践案例', 1, 'SUBMITTED'),
(7, 17, 7, '2025-2026-2', 5, 4, 4, 5, 4.50, '高数虽难但赵老师讲得很好', 0, 'SUBMITTED'),
(6, 14, 1, '2025-2026-2', 5, 5, 4, 5, 4.75, '很实用！', 1, 'SUBMITTED');

-- ============================================================
-- 9. 补充竞赛报名
-- ============================================================
INSERT INTO competition_registration (competition_name, student_id, team_name, teammates, advisor, status, registration_time) VALUES
('全国大学生数学建模竞赛', 5, '数学建模A组', '李明远,王思雨,陈子涵', '赵文博', 'APPROVED', '2026-05-15 10:00:00'),
('蓝桥杯全国软件大赛', 6, '蓝桥先锋', '王思雨', '张明华', 'PENDING', '2026-06-20 14:00:00'),
('服务外包创新创业大赛', 9, '创新之星', '陈子涵,赵俊杰', '李芳', 'APPROVED', '2026-04-10 09:00:00');

-- ============================================================
-- 10. 补充心理咨询
-- ============================================================
INSERT INTO consultation_appointment (student_id, consultant_name, appointment_date, appointment_time, topic, status, feedback) VALUES
(6, '陈心怡（心理咨询师）', '2026-07-20', '10:00-10:50', '考试焦虑缓解', 'CONFIRMED', NULL),
(7, '陈心怡（心理咨询师）', '2026-06-20', '15:00-15:50', '人际关系困扰', 'COMPLETED', '咨询效果良好'),
(9, '王老师（心理咨询师）', '2026-07-22', '14:00-14:50', '学业规划迷茫', 'PENDING', NULL);

-- ============================================================
-- 11. 补充实践项目
-- ============================================================
INSERT INTO practice_project_registration (project_name, student_id, teacher_id, project_date, duration, lab_location, status, registration_time) VALUES
('Java Web开发实训', 5, 14, '2026-08-01', 10, '实验楼A302', 'APPROVED', '2026-07-01 09:00:00'),
('深度学习项目实践', 6, 15, '2026-08-05', 7, 'AI与大数据实验室', 'PENDING', '2026-07-03 14:00:00'),
('嵌入式系统设计', 10, 14, '2026-08-10', 5, '机器人创新实验室', 'APPROVED', '2026-07-02 10:00:00');

-- ============================================================
-- 12. 补充科研
-- ============================================================
INSERT INTO scientific_research (teacher_id, project_name, project_code, project_type, fund_amount, start_date, end_date, status, members, achievements) VALUES
(15, '基于微服务的智慧校园架构研究', 'PRJ-2026-001', '校级', 50000.00, '2026-01-01', '2026-12-31', 'IN_PROGRESS', '李芳', '已完成架构设计'),
(16, '高校数字化转型策略研究', 'PRJ-2026-002', '省级', 120000.00, '2026-03-01', '2027-06-01', 'IN_PROGRESS', '王建国,孙晓萌', NULL);

-- ============================================================
-- 13. 补充公文
-- ============================================================
INSERT INTO document (title, content, document_type, submitter_id, submitter_name, department, status, approver_id, approver_name, approval_comment, submit_time, approval_time) VALUES
('理学院采购数学建模软件申请', '校领导：\n为支持数学建模竞赛备赛，申请采购MATLAB软件授权20套，预算约8万元。', 'APPLY', 17, '赵文博', '理学院', 'APPROVED', 3, '徐娅', '同意，按规定执行。', '2026-05-15 10:00:00', '2026-05-20 15:00:00'),
('计算机学院暑期实训请示', '教务处：\n计算机学院计划2026年暑期7月20日至8月15日开展企业实训，涉及2023级软件工程120名学生。', 'APPLY', 14, '张明华', '计算机学院', 'PENDING', NULL, NULL, NULL, '2026-07-01 09:00:00', NULL);

-- ============================================================
-- 14. 补充更多工作计划
-- ============================================================
INSERT INTO work_plan (user_id, user_name, title, content, plan_date, plan_type, department, priority, status, finish_time) VALUES
(3, '徐娅', '教学督导检查', '对计算机学院、经管学院进行教学督导检查', '2026-07-22', 'WORK', '校办', 'HIGH', 'PENDING', NULL),
(14, '张明华', '课程大纲修订', '修订《数据结构》和《数据库原理》教学大纲', '2026-07-25', 'WORK', '计算机学院', 'MEDIUM', 'PENDING', NULL),
(15, '李芳', '期末成绩分析报告', '分析2025-2026-2学期所授课程成绩', '2026-07-18', 'WORK', '计算机学院', 'HIGH', 'IN_PROGRESS', NULL);

SELECT '数据库补全完成！' AS result;
