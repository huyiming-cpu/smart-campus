package com.smartcampus.smartcampus.service;

import com.smartcampus.smartcampus.dto.Response;
import com.smartcampus.smartcampus.repository.NoticeRepository;
import com.smartcampus.smartcampus.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final NoticeRepository noticeRepository;
    private final NewsRepository newsRepository;

    public List<Response.SearchSuggestion> search(String keyword, String role, Long userId) {
        if (keyword == null || keyword.trim().length() < 2) return List.of();
        String kw = keyword.trim();
        List<Response.SearchSuggestion> results = new ArrayList<>();

        // 1. 功能匹配
        results.addAll(getFunctionMatches(kw, role));

        // 2. 内容搜索
        results.addAll(searchContent(kw, role));


        return results.stream().limit(8).collect(Collectors.toList());
    }

    private List<Response.SearchSuggestion> getFunctionMatches(String kw, String role) {
        List<Response.SearchSuggestion> matches = new ArrayList<>();
        String prefix = "/" + role.toLowerCase();

        Map<String, String[]> mappings;
        if ("STUDENT".equals(role)) {
            mappings = Map.ofEntries(
                    Map.entry("成绩", new String[]{"成绩查询", prefix + "/scores", "查看课程成绩与绩点", "scores"}),
                    Map.entry("课表", new String[]{"课程表", prefix + "/study", "查看本学期课程安排", "calendar"}),
                    Map.entry("考试", new String[]{"考试中心", prefix + "/exams", "考试安排与等级考试报名", "exam"}),
                    Map.entry("一卡通", new String[]{"一卡通缴费", prefix + "/wallet", "校园卡管理与在线缴费", "wallet"}),
                    Map.entry("缴费", new String[]{"一卡通缴费", prefix + "/wallet", "校园卡余额查询与充值", "wallet"}),
                    Map.entry("个人信息", new String[]{"个人信息", prefix + "/profile", "查看与修改个人资料", "profile"}),
                    Map.entry("通知", new String[]{"通知公告", prefix + "/notice", "查看系统与学校通知", "notice"}),
                    Map.entry("新闻", new String[]{"学校新闻", prefix + "/news", "查看学校最新动态", "news"}),
                    Map.entry("实验室", new String[]{"实验室", prefix + "/labs", "实验室信息查询", "lab"}),
                    Map.entry("毕业设计", new String[]{"毕业设计", prefix + "/graduation", "毕设选题与进度管理", "graduation"}),
                    Map.entry("评教", new String[]{"教学评价", prefix + "/study", "课程评教", "evaluation"}),
                    Map.entry("校历", new String[]{"校历查询", prefix + "/calendar", "查看学期校历安排", "calendar"}),
                    Map.entry("补考", new String[]{"补考报名", prefix + "/exams", "补考与重修报名", "exam"}),
                    Map.entry("竞赛", new String[]{"竞赛报名", prefix + "/competitions", "学科竞赛与创新项目", "competition"}),
                    Map.entry("心理咨询", new String[]{"心理咨询", prefix + "/consultation", "在线预约心理咨询", "consultation"})
            );
        } else {
            mappings = Map.ofEntries(
                    Map.entry("课表", new String[]{"课程安排", prefix + "/schedule", "查看教学课表", "calendar"}),
                    Map.entry("成绩", new String[]{"成绩管理", prefix + "/scores", "学生成绩录入与查询", "scores"}),
                    Map.entry("考试", new String[]{"考试管理", prefix + "/exams", "考试安排与监考信息", "exam"}),
                    Map.entry("科研", new String[]{"科研管理", prefix + "/research", "课题申报与论文管理", "research"}),
                    Map.entry("一卡通", new String[]{"一卡通", prefix + "/card", "校园卡管理", "wallet"}),
                    Map.entry("个人信息", new String[]{"个人信息", prefix + "/profile", "查看与修改个人资料", "profile"}),
                    Map.entry("通知", new String[]{"通知公告", prefix + "/announcement", "查看系统通知", "notice"}),
                    Map.entry("新闻", new String[]{"学校新闻", prefix + "/news", "查看学校最新动态", "news"}),
                    Map.entry("毕业设计", new String[]{"毕业设计", prefix + "/graduation", "指导进度与论文审核", "graduation"}),
                    Map.entry("评教", new String[]{"教学评价", prefix + "/evaluation", "查看评教结果", "evaluation"}),
                    Map.entry("校历", new String[]{"校历查询", prefix + "/calendar", "查看学期校历安排", "calendar"}),
                    Map.entry("工作计划", new String[]{"工作计划", prefix + "/work-plans", "学期计划制定与追踪", "plan"}),
                    Map.entry("通讯录", new String[]{"通讯录", prefix + "/directory", "教师通讯录查询", "directory"}),
                    Map.entry("公文", new String[]{"待办公文", prefix + "/documents", "公文流转与审批", "document"})
            );
        }

        for (var entry : mappings.entrySet()) {
            if (kw.contains(entry.getKey()) || entry.getKey().contains(kw)) {
                String[] val = entry.getValue();
                var sug = new Response.SearchSuggestion();
                sug.setType("FUNCTION");
                sug.setTitle(val[0]);
                sug.setUrl(val[1]);
                sug.setDescription(val[2]);
                sug.setIcon(val[3]);
                matches.add(sug);
            }
        }
        return matches;
    }

    private List<Response.SearchSuggestion> searchContent(String kw, String role) {
        List<Response.SearchSuggestion> results = new ArrayList<>();

        // 搜索公告
        noticeRepository.searchPublished(kw).stream().limit(3).forEach(a -> {
            var s = new Response.SearchSuggestion();
            s.setType("CONTENT");
            s.setTitle(a.getTitle());
            s.setDescription("公告通知");
            if ("STUDENT".equals(role)) s.setUrl("/student/notice");
            else s.setUrl("/teacher/announcement");
            s.setIcon("notice");
            results.add(s);
        });

        // 搜索新闻
        newsRepository.searchPublished(kw).stream().limit(3).forEach(n -> {
            var s = new Response.SearchSuggestion();
            s.setType("CONTENT");
            s.setTitle(n.getTitle());
            s.setDescription("学校新闻");
            if ("STUDENT".equals(role)) s.setUrl("/student/news");
            else s.setUrl("/teacher/news");
            s.setIcon("news");
            results.add(s);
        });

        return results;
    }
}
