package com.smartcampus.smartcampus.service;

import com.smartcampus.smartcampus.dto.Request;
import com.smartcampus.smartcampus.dto.Response;
import com.smartcampus.smartcampus.entity.Announcement;
import com.smartcampus.smartcampus.entity.News;
import com.smartcampus.smartcampus.repository.NoticeRepository;
import com.smartcampus.smartcampus.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final NewsRepository newsRepository;

    // ==================== 公告 ====================

    /** 按角色查已发布公告（学生/教师/领导都能看） */
    public List<Response.AnnouncementItem> listAnnouncements(String roleCode) {
        return noticeRepository.findPublishedByRole(roleCode).stream()
                .map(this::toAnnounceItem).collect(Collectors.toList());
    }

    /** 公告详情 */
    public Response.AnnouncementDetail getAnnouncement(Long id) {
        Announcement a = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("公告不存在"));
        a.setViewCount((a.getViewCount() == null ? 0 : a.getViewCount()) + 1);
        noticeRepository.save(a);
        return toAnnounceDetail(a);
    }

    /** 管理员查所有公告(含草稿/已撤回) */
    public List<Response.AnnouncementItem> listAllAnnouncements(String keyword) {
        List<Announcement> list;
        if (keyword != null && !keyword.isEmpty()) {
            list = noticeRepository.searchPublished(keyword);
        } else {
            list = noticeRepository.findAll();
        }
        list.sort((a, b) -> {
            int topCompare = Integer.compare(b.getIsTop() == null ? 0 : b.getIsTop(),
                                              a.getIsTop() == null ? 0 : a.getIsTop());
            if (topCompare != 0) return topCompare;
            return b.getCreatedTime().compareTo(a.getCreatedTime());
        });
        return list.stream().map(this::toAnnounceItem).collect(Collectors.toList());
    }

    @Transactional
    public Announcement createAnnouncement(Request.AnnouncementForm form) {
        Announcement a = new Announcement();
        fillAnnouncement(a, form);
        a.setViewCount(0);
        a.setPublisherId(form.getPublisherId());
        a.setPublisherName(form.getPublisherName());
        return noticeRepository.save(a);
    }

    @Transactional
    public Announcement updateAnnouncement(Long id, Request.AnnouncementForm form) {
        Announcement a = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("公告不存在"));
        fillAnnouncement(a, form);
        return noticeRepository.save(a);
    }

    @Transactional
    public void deleteAnnouncement(Long id) {
        noticeRepository.deleteById(id);
    }

    @Transactional
    public void toggleTop(Long id) {
        Announcement a = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("公告不存在"));
        a.setIsTop(a.getIsTop() != null && a.getIsTop() == 1 ? 0 : 1);
        noticeRepository.save(a);
    }

    private void fillAnnouncement(Announcement a, Request.AnnouncementForm form) {
        a.setTitle(form.getTitle());
        a.setContent(form.getContent());
        a.setTargetRoles(form.getTargetRoles());
        a.setIsTop(form.getIsTop() != null ? form.getIsTop() : 0);
        a.setIsUrgent(form.getIsUrgent() != null ? form.getIsUrgent() : 0);
        a.setStartTime(form.getStartTime());
        a.setEndTime(form.getEndTime());
        a.setStatus(form.getStatus() != null ? form.getStatus() : "PUBLISHED");
    }

    // ==================== 新闻 ====================

    /** 查已发布新闻（支持分类筛选） */
    public List<Response.NewsItem> listNews(String category) {
        List<News> list;
        if (category != null && !category.isEmpty()) {
            list = newsRepository.findByStatusAndCategoryOrderByPublishTimeDesc("PUBLISHED", category);
        } else {
            list = newsRepository.findByStatusOrderByPublishTimeDesc("PUBLISHED");
        }
        return list.stream().map(this::toNewsItem).collect(Collectors.toList());
    }

    /** 新闻详情 */
    public Response.NewsDetail getNews(Long id) {
        News n = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("新闻不存在"));
        n.setViewCount((n.getViewCount() == null ? 0 : n.getViewCount()) + 1);
        newsRepository.save(n);
        return toNewsDetail(n);
    }

    @Transactional
    public News createNews(Request.NewsForm form) {
        News n = new News();
        fillNews(n, form);
        n.setViewCount(0);
        n.setLikeCount(0);
        return newsRepository.save(n);
    }

    @Transactional
    public News updateNews(Long id, Request.NewsForm form) {
        News n = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("新闻不存在"));
        fillNews(n, form);
        return newsRepository.save(n);
    }

    @Transactional
    public void deleteNews(Long id) {
        newsRepository.deleteById(id);
    }

    private void fillNews(News n, Request.NewsForm form) {
        n.setTitle(form.getTitle());
        n.setSummary(form.getSummary());
        n.setContent(form.getContent());
        n.setCoverImage(form.getCoverImage());
        n.setCategory(form.getCategory());
        n.setSource(form.getSource());
        n.setPublisherId(form.getPublisherId());
        n.setPublisherName(form.getPublisherName());
        n.setStatus(form.getStatus() != null ? form.getStatus() : "PUBLISHED");
        n.setPublishTime(form.getPublishTime() != null ? form.getPublishTime() : LocalDateTime.now());
    }

    // ==================== DTO 转换 ====================

    private Response.AnnouncementItem toAnnounceItem(Announcement a) {
        Response.AnnouncementItem item = new Response.AnnouncementItem();
        item.setId(a.getId()); item.setTitle(a.getTitle());
        item.setIsTop(a.getIsTop()); item.setIsUrgent(a.getIsUrgent());
        item.setPublisherName(a.getPublisherName());
        item.setStartTime(a.getStartTime()); item.setEndTime(a.getEndTime());
        item.setViewCount(a.getViewCount()); item.setStatus(a.getStatus());
        item.setCreatedTime(a.getCreatedTime());
        return item;
    }

    private Response.AnnouncementDetail toAnnounceDetail(Announcement a) {
        Response.AnnouncementDetail d = new Response.AnnouncementDetail();
        d.setId(a.getId()); d.setTitle(a.getTitle()); d.setContent(a.getContent());
        d.setIsTop(a.getIsTop()); d.setIsUrgent(a.getIsUrgent());
        d.setPublisherName(a.getPublisherName()); d.setTargetRoles(a.getTargetRoles());
        d.setStartTime(a.getStartTime()); d.setEndTime(a.getEndTime());
        d.setViewCount(a.getViewCount()); d.setStatus(a.getStatus());
        d.setCreatedTime(a.getCreatedTime());
        return d;
    }

    private Response.NewsItem toNewsItem(News n) {
        Response.NewsItem item = new Response.NewsItem();
        item.setId(n.getId()); item.setTitle(n.getTitle()); item.setSummary(n.getSummary());
        item.setCoverImage(n.getCoverImage()); item.setCategory(n.getCategory());
        item.setSource(n.getSource()); item.setViewCount(n.getViewCount());
        item.setLikeCount(n.getLikeCount()); item.setPublishTime(n.getPublishTime());
        return item;
    }

    private Response.NewsDetail toNewsDetail(News n) {
        Response.NewsDetail d = new Response.NewsDetail();
        d.setId(n.getId()); d.setTitle(n.getTitle()); d.setSummary(n.getSummary());
        d.setContent(n.getContent()); d.setCoverImage(n.getCoverImage());
        d.setCategory(n.getCategory()); d.setSource(n.getSource());
        d.setPublisherName(n.getPublisherName()); d.setViewCount(n.getViewCount());
        d.setLikeCount(n.getLikeCount()); d.setPublishTime(n.getPublishTime());
        return d;
    }
}
