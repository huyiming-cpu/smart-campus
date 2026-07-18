package com.smartcampus.smartcampus.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 所有请求DTO统一放在这里
 */
public class Request {

    // ========== 1. 登录 ==========
    @Data
    public static class Login {
        private String identityNumber;
        private String password;
    }

    // ========== 2. 注册 ==========
    @Data
    public static class Register {
        private String identityNumber;
        private String name;
        private String idCardLast6;
        private String password;
        private String phone;
        private String email;
    }

    // ========== 3. 修改密码 ==========
    @Data
    public static class UpdatePassword {
        private String oldPassword;
        private String newPassword;
    }

    // ========== 4. 更新个人信息（只含可改字段） ==========
    @Data
    public static class UpdateProfile {
        private String phone;
        private String email;
        private String avatarUrl;
        private String name;
        private String gender;
        private String major;
        private String title;
        private String address;
        private String emergencyContact;
        private String emergencyPhone;
        private String enrollmentDate;
        private String graduationDate;
    }

    // ========== 5. 公告表单（管理员用） ==========
    @Data
    public static class AnnouncementForm {
        private String title;
        private String content;
        private Long publisherId;
        private String publisherName;
        private String targetRoles;
        private Integer isTop;
        private Integer isUrgent;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String status;
    }

    // ========== 6. 新闻表单（管理员用） ==========
    @Data
    public static class NewsForm {
        private String title;
        private String summary;
        private String content;
        private String coverImage;
        private String category;
        private String source;
        private Long publisherId;
        private String publisherName;
        private String status;
        private LocalDateTime publishTime;
    }

    // ========== 7. 公文提交表单 ==========
    @Data
    public static class DocumentForm {
        private String title;
        private String content;
        private String documentType;
        private Long submitterId;
        private String submitterName;
        private String department;
    }

    // ========== 以后新增的接口都加在这里 ==========
}