-- ============================================
-- 为所有师生补全一卡通 + 消费记录
-- ============================================

-- 1. 为所有没有卡的用户创建一卡通
INSERT INTO card (user_id, card_no, balance, card_status, issue_date, expire_date, daily_limit)
SELECT u.id, CONCAT(u.identity_number, '01'), 300.00, 'NORMAL', '2024-09-01', '2027-07-01', 200.00
FROM user u
JOIN user_role ur ON u.id = ur.user_id
JOIN role r ON ur.role_id = r.id
WHERE u.id NOT IN (SELECT user_id FROM card)
  AND u.status = 'NORMAL';

-- 2. 赵美婷老师余额多一些
UPDATE card SET balance = 1560.50 WHERE card_no LIKE '2023112618%';

-- 3. 领导余额多一些
UPDATE card SET balance = 2380.00 WHERE card_no LIKE '2023112580%';
UPDATE card SET balance = 3600.00 WHERE card_no LIKE 'admin002%';

-- 4. 为没交易的卡添加5笔消费记录
INSERT INTO card_transaction (user_id, card_no, transaction_type, amount, balance, merchant, transaction_time, transaction_no, sync_status)
SELECT c.user_id, c.card_no, 'RECHARGE', 500.00, 500.00, '支付宝充值', DATE_SUB(NOW(), INTERVAL 7 DAY), CONCAT('RC', c.user_id, '01'), 1 FROM card c WHERE c.user_id NOT IN (SELECT DISTINCT user_id FROM card_transaction);

INSERT INTO card_transaction (user_id, card_no, transaction_type, amount, balance, merchant, transaction_time, transaction_no, sync_status)
SELECT c.user_id, c.card_no, 'CONSUME', 12.50, 487.50, '第一食堂一楼', DATE_SUB(NOW(), INTERVAL 6 DAY), CONCAT('RC', c.user_id, '02'), 1 FROM card c WHERE c.user_id NOT IN (1,7);

INSERT INTO card_transaction (user_id, card_no, transaction_type, amount, balance, merchant, transaction_time, transaction_no, sync_status)
SELECT c.user_id, c.card_no, 'CONSUME', 8.00, 479.50, '第二食堂二楼', DATE_SUB(NOW(), INTERVAL 5 DAY), CONCAT('RC', c.user_id, '03'), 1 FROM card c WHERE c.user_id NOT IN (1,7);

INSERT INTO card_transaction (user_id, card_no, transaction_type, amount, balance, merchant, transaction_time, transaction_no, sync_status)
SELECT c.user_id, c.card_no, 'CONSUME', 15.00, 464.50, '校园超市A区', DATE_SUB(NOW(), INTERVAL 3 DAY), CONCAT('RC', c.user_id, '04'), 1 FROM card c WHERE c.user_id NOT IN (1,7);

INSERT INTO card_transaction (user_id, card_no, transaction_type, amount, balance, merchant, transaction_time, transaction_no, sync_status)
SELECT c.user_id, c.card_no, 'CONSUME', 4.50, 460.00, '图书馆打印室', DATE_SUB(NOW(), INTERVAL 1 DAY), CONCAT('RC', c.user_id, '05'), 1 FROM card c WHERE c.user_id NOT IN (1,7);

-- 5. 赵美婷额外一笔教材消费
INSERT INTO card_transaction (user_id, card_no, transaction_type, amount, balance, merchant, transaction_time, transaction_no, sync_status)
SELECT user_id, card_no, 'CONSUME', 128.00, 1432.50, '校园教材中心', DATE_SUB(NOW(), INTERVAL 2 DAY), CONCAT('RC', user_id, '06'), 1
FROM card WHERE card_no LIKE '2023112618%';

-- 6. 更新余额=300+充值-消费
UPDATE card c SET c.balance = (
  SELECT 300.00
    - COALESCE((SELECT SUM(t.amount) FROM card_transaction t WHERE t.user_id = c.user_id AND t.transaction_type = 'CONSUME'), 0)
    + COALESCE((SELECT SUM(t.amount) FROM card_transaction t WHERE t.user_id = c.user_id AND t.transaction_type = 'RECHARGE'), 0)
) WHERE c.user_id NOT IN (1, 7);

-- 7. 恢复特殊余额
UPDATE card SET balance = 1560.50 WHERE card_no LIKE '2023112618%';
UPDATE card SET balance = 2380.00 WHERE card_no LIKE '2023112580%';
UPDATE card SET balance = 3600.00 WHERE card_no LIKE 'admin002%';
