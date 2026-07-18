package com.smartcampus.smartcampus.controller;

import com.smartcampus.smartcampus.common.Result;
import com.smartcampus.smartcampus.dto.Request;
import com.smartcampus.smartcampus.dto.Response;
import com.smartcampus.smartcampus.entity.Announcement;
import com.smartcampus.smartcampus.entity.News;
import com.smartcampus.smartcampus.security.JwtUtil;
import com.smartcampus.smartcampus.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;
    private final JwtUtil jwtUtil;

    // ==================== 公告 - 公开查看 ====================

    /** S-02 / T-03: 学生/教师/领导 按角色查看公告列表 */
    @GetMapping("/notice/announcements")
    public Result<List<Response.AnnouncementItem>> listAnnouncements(
            @RequestHeader("Authorization") String token) {
        String roleCode = jwtUtil.getRoleCodeFromToken(token);
        return Result.success(noticeService.listAnnouncements(roleCode));
    }

    /** 公告详情（点击+1阅读量） */
    @GetMapping("/notice/announcements/{id}")
    public Result<Response.AnnouncementDetail> getAnnouncement(@PathVariable Long id) {
        return Result.success(noticeService.getAnnouncement(id));
    }

    // ==================== 公告管理 - 管理员 A-07 ====================

    @GetMapping("/admin/announcements")
    public Result<List<Response.AnnouncementItem>> listAllAnnouncements(
            @RequestParam(required = false) String keyword) {
        return Result.success(noticeService.listAllAnnouncements(keyword));
    }

    @PostMapping("/admin/announcements")
    public Result<Announcement> createAnnouncement(@RequestBody Request.AnnouncementForm form) {
        return Result.success(noticeService.createAnnouncement(form));
    }

    @PutMapping("/admin/announcements/{id}")
    public Result<Announcement> updateAnnouncement(@PathVariable Long id,
                                                    @RequestBody Request.AnnouncementForm form) {
        return Result.success(noticeService.updateAnnouncement(id, form));
    }

    @DeleteMapping("/admin/announcements/{id}")
    public Result<Void> deleteAnnouncement(@PathVariable Long id) {
        noticeService.deleteAnnouncement(id);
        return Result.success();
    }

    /** 置顶/取消置顶 */
    @PutMapping("/admin/announcements/{id}/top")
    public Result<Void> toggleTop(@PathVariable Long id) {
        noticeService.toggleTop(id);
        return Result.success();
    }

    // ==================== 新闻 - 公开查看 S-04 ====================

    @GetMapping("/notice/news")
    public Result<List<Response.NewsItem>> listNews(
            @RequestParam(required = false) String category) {
        return Result.success(noticeService.listNews(category));
    }

    @GetMapping("/notice/news/{id}")
    public Result<Response.NewsDetail> getNews(@PathVariable Long id) {
        return Result.success(noticeService.getNews(id));
    }

    // ==================== 新闻管理 - 管理员 A-08 ====================

    @PostMapping("/admin/news")
    public Result<News> createNews(@RequestBody Request.NewsForm form) {
        return Result.success(noticeService.createNews(form));
    }

    @PutMapping("/admin/news/{id}")
    public Result<News> updateNews(@PathVariable Long id,
                                    @RequestBody Request.NewsForm form) {
        return Result.success(noticeService.updateNews(id, form));
    }

    @DeleteMapping("/admin/news/{id}")
    public Result<Void> deleteNews(@PathVariable Long id) {
        noticeService.deleteNews(id);
        return Result.success();
    }
}
