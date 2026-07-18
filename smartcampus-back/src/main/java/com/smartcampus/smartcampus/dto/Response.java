package com.smartcampus.smartcampus.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 所有响应DTO统一放在这里
 */
public class Response {

    // ========== 1. 登录响应 ==========
    @Data
    public static class Login {
        private String token;
        private Long userId;
        private String identityNumber;
        private String name;
        private String roleCode;
        private String roleName;
    }

    // ========== 2. 个人信息响应（user + user_profile 全部字段） ==========
    @Data
    public static class UserProfile {
        private Long id;
        private String identityNumber;
        private String name;
        private String gender;
        private String phone;
        private String email;
        private String avatarUrl;
        // user_profile
        private String department;
        private String major;
        private String grade;
        private String className;
        private String title;
        private String address;
        private String emergencyContact;
        private String emergencyPhone;
        private LocalDateTime enrollmentDate;
        private LocalDateTime graduationDate;
    }

    // ========== 3. 公告列表项 ==========
    @Data
    public static class AnnouncementItem {
        private Long id;
        private String title;
        private Integer isTop;
        private Integer isUrgent;
        private String publisherName;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Integer viewCount;
        private String status;
        private LocalDateTime createdTime;
    }

    // ========== 4. 公告详情 ==========
    @Data
    public static class AnnouncementDetail {
        private Long id;
        private String title;
        private String content;
        private Integer isTop;
        private Integer isUrgent;
        private String publisherName;
        private String targetRoles;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Integer viewCount;
        private String status;
        private LocalDateTime createdTime;
    }

    // ========== 5. 新闻列表项 ==========
    @Data
    public static class NewsItem {
        private Long id;
        private String title;
        private String summary;
        private String coverImage;
        private String category;
        private String source;
        private Integer viewCount;
        private Integer likeCount;
        private LocalDateTime publishTime;
    }

    // ========== 6. 新闻详情 ==========
    @Data
    public static class NewsDetail {
        private Long id;
        private String title;
        private String summary;
        private String content;
        private String coverImage;
        private String category;
        private String source;
        private String publisherName;
        private Integer viewCount;
        private Integer likeCount;
        private LocalDateTime publishTime;
    }

    // ========== 7. 仪表盘 ==========
    @Data
    public static class Dashboard {
        private int todayActiveUsers;
        private long totalUsers;
        private long pendingRegistrations;
        private String serverStatus;
        private String uptime;
    }

    // ========== 8. 统计卡片 ==========
    @Data
    public static class StatCard {
        private String label;
        private String value;
        private String extra;
    }

    // ========== 9. 键值对 ==========
    @Data
    public static class KV {
        private String key;
        private String value;
    }

    // ========== 资产统计 ==========
    @Data
    public static class AssetStats {
        private int totalCount;
        private BigDecimal totalValue;
        private List<AssetDetail> details;
    }
    @Data
    public static class AssetDetail {
        private String department;
        private int count;
        private BigDecimal totalValue;
        private List<AssetItem> items;
    }
    @Data
    public static class AssetItem {
        private Long id;
        private String assetName;
        private String assetNo;
        private String assetType;
        private BigDecimal price;
        private String purchaseDate;
        private String status;
        private String remark;
    }

    // ========== 在校生统计 ==========
    @Data
    public static class StudentCountItem {
        private String department;
        private String grade;
        private int count;
    }

    @Data
    public static class DeptStudents {
        private String department;
        private int totalCount;
        private List<StudentInfo> students;
    }

    @Data
    public static class StudentInfo {
        private String name;
        private String studentNo;
        private String grade;
        private String className;
        private String major;
    }

    // ========== 评教统计 ==========
    @Data
    public static class EvalStat {
        private String teacherName;
        private String department;
        private double avgScore;
    }

    // ========== 10. 缴费统计 ==========
    @Data
    public static class PaymentStats {
        private int totalStudents;
        private int registeredCount;
        private int paidCount;
        private int unpaidCount;
        private BigDecimal totalAmount;
        private BigDecimal paidAmount;
    }

    // ========== 11. 一卡通信息 ==========
    @Data
    public static class CardInfo {
        private String cardNo;
        private BigDecimal balance;
        private String cardStatus;
        private List<TxItem> transactions;
    }

    // ========== 12. 消费记录项 ==========
    @Data
    public static class TxItem {
        private String transactionType;
        private BigDecimal amount;
        private BigDecimal balance;
        private String merchant;
        private java.time.LocalDateTime transactionTime;
    }
}
