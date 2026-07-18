-- ============================================
-- 补齐6学院完整数据：院长、辅导员、学生
-- 2026-07-18
-- ============================================

-- 1. 补充角色表（如果有缺失）
INSERT IGNORE INTO role (id, role_code, role_name, role_desc, sort_order, status) VALUES
(1, 'STUDENT', '学生', '在校学生', 1, 1),
(2, 'TEACHER', '教师', '授课教师', 2, 1),
(3, 'LEADER',  '领导', '院系领导/校领导', 3, 1),
(4, 'ADMIN',   '管理员', '系统管理员', 4, 1);

-- 2. ============ 理学院：院长+辅导员 ============
-- 院长 吴志强
INSERT INTO user (identity_number, password_hash, name, gender, phone, email, status, info_completeness) VALUES
('2023005', '123456', '吴志强', 'M', '13901002001', 'wuzhiqiang@edu.cn', 'NORMAL', 1);
INSERT INTO user_role (user_id, role_id) SELECT id, 3 FROM user WHERE identity_number='2023005';
INSERT INTO user_profile (user_id, department, title, major) SELECT id, '理学院', '教授', NULL FROM user WHERE identity_number='2023005';
-- 赵文博(id=17)已是理学院教师 → 设为辅导员
INSERT IGNORE INTO card (user_id, card_no, balance, card_status, issue_date, expire_date, daily_limit) SELECT id, CONCAT(identity_number,'01'), 500.00, 'NORMAL', '2024-09-01', '2027-07-01', 200.00 FROM user WHERE identity_number IN ('2023005');

-- 3. ============ 经管学院：院长+辅导员 ============
-- 院长 陈雅琳
INSERT INTO user (identity_number, password_hash, name, gender, phone, email, status, info_completeness) VALUES
('2023006', '123456', '陈雅琳', 'F', '13901002002', 'chenyalin@edu.cn', 'NORMAL', 1);
INSERT INTO user_role (user_id, role_id) SELECT id, 3 FROM user WHERE identity_number='2023006';
INSERT INTO user_profile (user_id, department, title, major) SELECT id, '经管学院', '教授', NULL FROM user WHERE identity_number='2023006';
-- 王建国(id=16)已是经管学院教师 → 设为辅导员
INSERT IGNORE INTO card (user_id, card_no, balance, card_status, issue_date, expire_date, daily_limit) SELECT id, CONCAT(identity_number,'01'), 500.00, 'NORMAL', '2024-09-01', '2027-07-01', 200.00 FROM user WHERE identity_number IN ('2023006');

-- 4. ============ 外国语学院：院长+辅导员+学生 ============
-- 院长 林晓薇
INSERT INTO user (identity_number, password_hash, name, gender, phone, email, status, info_completeness) VALUES
('2023007', '123456', '林晓薇', 'F', '13901002003', 'linxiaowei@edu.cn', 'NORMAL', 1);
INSERT INTO user_role (user_id, role_id) SELECT id, 3 FROM user WHERE identity_number='2023007';
INSERT INTO user_profile (user_id, department, title, major) SELECT id, '外国语学院', '教授', NULL FROM user WHERE identity_number='2023007';
-- 辅导员 钱慧
INSERT INTO user (identity_number, password_hash, name, gender, phone, email, status, info_completeness) VALUES
('2023008', '123456', '钱慧', 'F', '13901002004', 'qianhui@edu.cn', 'NORMAL', 1);
INSERT INTO user_role (user_id, role_id) SELECT id, 2 FROM user WHERE identity_number='2023008';
INSERT INTO user_profile (user_id, department, title, major) SELECT id, '外国语学院', '辅导员', NULL FROM user WHERE identity_number='2023008';
-- 学生 x3
INSERT INTO user (identity_number, password_hash, name, gender, phone, email, status, info_completeness) VALUES
('2023112010', '123456', '张悦然', 'F', '13901002101', 'zhangyueran@stu.edu.cn', 'NORMAL', 1),
('2023112011', '123456', '黄思远', 'M', '13901002102', 'huangsiyuan@stu.edu.cn', 'NORMAL', 1),
('2024112010', '123456', '郑雨晴', 'F', '13901002103', 'zhengyuqing@stu.edu.cn', 'NORMAL', 1);
INSERT INTO user_role (user_id, role_id) SELECT id, 1 FROM user WHERE identity_number IN ('2023112010','2023112011','2024112010');
INSERT INTO user_profile (user_id, department, major, grade, class_name) SELECT id, '外国语学院', '英语', '2023级', '英语2301' FROM user WHERE identity_number='2023112010';
INSERT INTO user_profile (user_id, department, major, grade, class_name) SELECT id, '外国语学院', '日语', '2023级', '日语2301' FROM user WHERE identity_number='2023112011';
INSERT INTO user_profile (user_id, department, major, grade, class_name) SELECT id, '外国语学院', '英语', '2024级', '英语2401' FROM user WHERE identity_number='2024112010';

-- 5. ============ 电子信息学院：院长+辅导员+学生 ============
-- 院长 孙建国
INSERT INTO user (identity_number, password_hash, name, gender, phone, email, status, info_completeness) VALUES
('2023009', '123456', '孙建国', 'M', '13901002005', 'sunjianguo@edu.cn', 'NORMAL', 1);
INSERT INTO user_role (user_id, role_id) SELECT id, 3 FROM user WHERE identity_number='2023009';
INSERT INTO user_profile (user_id, department, title, major) SELECT id, '电子信息学院', '教授', NULL FROM user WHERE identity_number='2023009';
-- 辅导员 周明辉
INSERT INTO user (identity_number, password_hash, name, gender, phone, email, status, info_completeness) VALUES
('2023010', '123456', '周明辉', 'M', '13901002006', 'zhouminghui@edu.cn', 'NORMAL', 1);
INSERT INTO user_role (user_id, role_id) SELECT id, 2 FROM user WHERE identity_number='2023010';
INSERT INTO user_profile (user_id, department, title, major) SELECT id, '电子信息学院', '辅导员', NULL FROM user WHERE identity_number='2023010';
-- 学生 x3
INSERT INTO user (identity_number, password_hash, name, gender, phone, email, status, info_completeness) VALUES
('2023112012', '123456', '马晓东', 'M', '13901002104', 'maxiaodong@stu.edu.cn', 'NORMAL', 1),
('2023112013', '123456', '罗嘉文', 'M', '13901002105', 'luojiawen@stu.edu.cn', 'NORMAL', 1),
('2024112011', '123456', '何小燕', 'F', '13901002106', 'hexiaoyan@stu.edu.cn', 'NORMAL', 1);
INSERT INTO user_role (user_id, role_id) SELECT id, 1 FROM user WHERE identity_number IN ('2023112012','2023112013','2024112011');
INSERT INTO user_profile (user_id, department, major, grade, class_name) SELECT id, '电子信息学院', '电子信息工程', '2023级', '电信2301' FROM user WHERE identity_number='2023112012';
INSERT INTO user_profile (user_id, department, major, grade, class_name) SELECT id, '电子信息学院', '通信工程', '2023级', '通信2301' FROM user WHERE identity_number='2023112013';
INSERT INTO user_profile (user_id, department, major, grade, class_name) SELECT id, '电子信息学院', '电子信息工程', '2024级', '电信2401' FROM user WHERE identity_number='2024112011';

-- 6. ============ 医学院：院长+辅导员+学生 ============
-- 院长 李博文
INSERT INTO user (identity_number, password_hash, name, gender, phone, email, status, info_completeness) VALUES
('2023011', '123456', '李博文', 'M', '13901002007', 'libowen@edu.cn', 'NORMAL', 1);
INSERT INTO user_role (user_id, role_id) SELECT id, 3 FROM user WHERE identity_number='2023011';
INSERT INTO user_profile (user_id, department, title, major) SELECT id, '医学院', '教授', NULL FROM user WHERE identity_number='2023011';
-- 辅导员 刘芳菲
INSERT INTO user (identity_number, password_hash, name, gender, phone, email, status, info_completeness) VALUES
('2023012', '123456', '刘芳菲', 'F', '13901002008', 'liufangfei@edu.cn', 'NORMAL', 1);
INSERT INTO user_role (user_id, role_id) SELECT id, 2 FROM user WHERE identity_number='2023012';
INSERT INTO user_profile (user_id, department, title, major) SELECT id, '医学院', '辅导员', NULL FROM user WHERE identity_number='2023012';
-- 学生 x3
INSERT INTO user (identity_number, password_hash, name, gender, phone, email, status, info_completeness) VALUES
('2023112014', '123456', '高思琪', 'F', '13901002107', 'gaosiqi@stu.edu.cn', 'NORMAL', 1),
('2023112015', '123456', '唐文博', 'M', '13901002108', 'tangwenbo@stu.edu.cn', 'NORMAL', 1),
('2024112012', '123456', '沈佳怡', 'F', '13901002109', 'shenjiayi@stu.edu.cn', 'NORMAL', 1);
INSERT INTO user_role (user_id, role_id) SELECT id, 1 FROM user WHERE identity_number IN ('2023112014','2023112015','2024112012');
INSERT INTO user_profile (user_id, department, major, grade, class_name) SELECT id, '医学院', '临床医学', '2023级', '临床2301' FROM user WHERE identity_number='2023112014';
INSERT INTO user_profile (user_id, department, major, grade, class_name) SELECT id, '医学院', '护理学', '2023级', '护理2301' FROM user WHERE identity_number='2023112015';
INSERT INTO user_profile (user_id, department, major, grade, class_name) SELECT id, '医学院', '临床医学', '2024级', '临床2401' FROM user WHERE identity_number='2024112012';

-- 7. ============ 计算机学院：徐娅=院长(id=3已有), 赵美婷=辅导员(id=2已有) ============
-- 计算机学院已经完整，无需添加

-- 8. 补充一卡通（给新用户）
INSERT IGNORE INTO card (user_id, card_no, balance, card_status, issue_date, expire_date, daily_limit)
SELECT u.id, CONCAT(u.identity_number,'01'), 300.00, 'NORMAL', '2024-09-01', '2027-07-01', 200.00
FROM user u WHERE u.id NOT IN (SELECT user_id FROM card) AND u.status='NORMAL';

-- 9. 为一卡通加交易记录
INSERT IGNORE INTO card_transaction (user_id, card_no, transaction_type, amount, balance, merchant, transaction_time, transaction_no, sync_status)
SELECT c.user_id, c.card_no, 'RECHARGE', 500.00, 500.00, '支付宝充值', DATE_SUB(NOW(), INTERVAL 5 DAY), CONCAT('TX', c.user_id, '01'), 1
FROM card c WHERE c.user_id NOT IN (SELECT DISTINCT user_id FROM card_transaction);

INSERT IGNORE INTO card_transaction (user_id, card_no, transaction_type, amount, balance, merchant, transaction_time, transaction_no, sync_status)
SELECT c.user_id, c.card_no, 'CONSUME', 8.50, 491.50, '第一食堂', DATE_SUB(NOW(), INTERVAL 3 DAY), CONCAT('TX', c.user_id, '02'), 1
FROM card c WHERE c.user_id NOT IN (1,7);

INSERT IGNORE INTO card_transaction (user_id, card_no, transaction_type, amount, balance, merchant, transaction_time, transaction_no, sync_status)
SELECT c.user_id, c.card_no, 'CONSUME', 15.00, 476.50, '第二食堂', DATE_SUB(NOW(), INTERVAL 1 DAY), CONCAT('TX', c.user_id, '03'), 1
FROM card c WHERE c.user_id NOT IN (1,7);

-- 10. 加一些固定资产数据（让统计有东西看）
INSERT IGNORE INTO fixed_asset (asset_name, asset_no, asset_type, department, price, purchase_date, status, remark) VALUES
('高性能计算服务器', 'FA001', '设备', '计算机学院', 85000.00, '2025-06-01', 'NORMAL', '深度学习训练用GPU服务器'),
('多媒体投影系统', 'FA002', '设备', '外国语学院', 32000.00, '2025-03-15', 'NORMAL', '语言实验室投影设备'),
('数字示波器', 'FA003', '设备', '电子信息学院', 18000.00, '2025-09-01', 'NORMAL', '电子实验用'),
('医学影像工作站', 'FA004', '设备', '医学院', 120000.00, '2025-01-10', 'NORMAL', 'CT/MRI图像处理'),
('经济数据分析平台', 'FA005', '软件', '经管学院', 45000.00, '2025-05-20', 'NORMAL', '企业级经济数据分析'),
('物理实验仪器套装', 'FA006', '设备', '理学院', 55000.00, '2024-11-01', 'NORMAL', '大学物理实验用'),
('办公电脑', 'FA007', '设备', '计算机学院', 6000.00, '2026-03-01', 'NORMAL', '教师办公用'),
('空调系统', 'FA008', '设备', '经管学院', 25000.00, '2025-07-15', 'NORMAL', '教学楼中央空调');

-- 11. 加缴费数据
INSERT IGNORE INTO payment_record (student_id, semester, payment_type, total_amount, paid_amount, payment_status, payment_time)
SELECT u.id, '2025-2026-2', 'TUITION', 5800.00, 5800.00, 'PAID', NOW()
FROM user u JOIN user_role ur ON u.id=ur.user_id WHERE ur.role_id=1 AND u.id NOT IN (SELECT student_id FROM payment_record);

-- 12. 更新密码为明文（方便测试）
UPDATE user SET password_hash = '123456' WHERE password_hash LIKE '$2a$%';
