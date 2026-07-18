package com.smartcampus.smartcampus.service;

import com.smartcampus.smartcampus.dto.Request;
import com.smartcampus.smartcampus.entity.Document;
import com.smartcampus.smartcampus.entity.WorkPlan;
import com.smartcampus.smartcampus.repository.DocumentRepository;
import com.smartcampus.smartcampus.repository.WorkPlanRepository;
import com.smartcampus.smartcampus.repository.UserRepository;
import com.smartcampus.smartcampus.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final WorkPlanRepository workPlanRepository;

    // ==================== 公文 Document (T-10 / L-05) ====================

    /** 教师：查看自己提交的公文 */
    public List<Document> listMyDocuments(Long userId) {
        return documentRepository.findBySubmitterIdOrderBySubmitTimeDesc(userId);
    }

    /** 领导：查看待审批的公文 */
    public List<Document> listPendingDocuments(Long approverId) {
        return documentRepository.findByStatusOrderBySubmitTimeDesc("PENDING");
    }

    /** 领导：查看已处理的公文 */
    public List<Document> listApprovedDocuments(Long approverId) {
        return documentRepository.findByApproverIdOrderBySubmitTimeDesc(approverId);
    }

    /** 教师：提交公文 */
    @Transactional
    public Document submitDocument(Request.DocumentForm form) {
        Document d = new Document();
        d.setTitle(form.getTitle());
        d.setContent(form.getContent());
        d.setDocumentType(form.getDocumentType());
        d.setSubmitterId(form.getSubmitterId());
        d.setSubmitterName(form.getSubmitterName());
        d.setDepartment(form.getDepartment());
        d.setStatus("PENDING");
        d.setSubmitTime(LocalDateTime.now());
        return documentRepository.save(d);
    }

    /** 领导：审批公文 */
    @Transactional
    public Document approveDocument(Long id, Long approverId, String approverName,
                                     String status, String comment) {
        Document d = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("公文不存在"));
        d.setStatus(status); // APPROVED / REJECTED
        d.setApproverId(approverId);
        d.setApproverName(approverName);
        d.setApprovalComment(comment);
        d.setApprovalTime(LocalDateTime.now());
        return documentRepository.save(d);
    }

    /** 教师：删除自己的公文(仅PENDING状态可删) */
    @Transactional
    public void deleteDocument(Long id, Long userId) {
        Document d = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("公文不存在"));
        if (!d.getSubmitterId().equals(userId))
            throw new RuntimeException("只能删除自己的公文");
        if (!"PENDING".equals(d.getStatus()))
            throw new RuntimeException("只能删除待审批的公文");
        documentRepository.deleteById(id);
    }

    // ==================== 工作计划 WorkPlan (L-04) ====================

    private final UserRepository userRepo;
    private final UserProfileRepository profileRepo;

    public List<Map<String, Object>> listWorkPlans(Long userId, String department) {
        List<WorkPlan> list;
        if (userId != null) list = workPlanRepository.findByUserIdOrderByPlanDateDesc(userId);
        else if (department != null) list = workPlanRepository.findByDepartmentOrderByPlanDateDesc(department);
        else list = workPlanRepository.findAll();
        // 排序: PENDING > IN_PROGRESS > COMPLETED
        Map<String, Integer> order = Map.of("PENDING",0,"IN_PROGRESS",1,"COMPLETED",2);
        list.sort((a,b)->{
            int oa=order.getOrDefault(a.getStatus(),9), ob=order.getOrDefault(b.getStatus(),9);
            if(oa!=ob)return oa-ob;
            return b.getPlanDate().compareTo(a.getPlanDate());
        });
        return list.stream().map(w->{
            Map<String,Object> m=new LinkedHashMap<>();
            m.put("id",w.getId());m.put("userId",w.getUserId());m.put("userName",w.getUserName());
            m.put("title",w.getTitle());m.put("content",w.getContent());
            m.put("planDate",w.getPlanDate());m.put("planType",w.getPlanType());
            m.put("department",w.getDepartment());m.put("priority",w.getPriority());
            m.put("status",w.getStatus());m.put("finishTime",w.getFinishTime());
            return m;
        }).collect(java.util.stream.Collectors.toList());
    }

    /** 公文列表（按部门过滤，dept=null查全部） */
    public List<Map<String, Object>> listLeaderDocuments(String type, String department) {
        List<Document> list;
        if ("approved".equals(type)) {
            if (department != null) list = documentRepository.findByDepartmentOrderBySubmitTimeDesc(department).stream().filter(d -> "APPROVED".equals(d.getStatus()) || "REJECTED".equals(d.getStatus())).collect(Collectors.toList());
            else list = documentRepository.findAll().stream().filter(d -> "APPROVED".equals(d.getStatus()) || "REJECTED".equals(d.getStatus())).collect(Collectors.toList());
        } else {
            if (department != null) list = documentRepository.findByDepartmentOrderBySubmitTimeDesc(department).stream().filter(d -> "PENDING".equals(d.getStatus())).collect(Collectors.toList());
            else list = documentRepository.findByStatusOrderBySubmitTimeDesc("PENDING");
        }
        return list.stream().map(d->{
            Map<String,Object> m=new LinkedHashMap<>();
            m.put("id",d.getId());m.put("title",d.getTitle());m.put("content",d.getContent());
            m.put("documentType",d.getDocumentType());m.put("submitterId",d.getSubmitterId());
            m.put("submitterName",d.getSubmitterName());m.put("department",d.getDepartment());
            m.put("status",d.getStatus());m.put("approverName",d.getApproverName());
            m.put("approvalComment",d.getApprovalComment());
            m.put("submitTime",d.getSubmitTime());m.put("approvalTime",d.getApprovalTime());
            return m;
        }).collect(Collectors.toList());
    }

    /** 教师：查看自己的工作计划 */
    public List<WorkPlan> listMyWorkPlans(Long userId) {
        return workPlanRepository.findByUserIdOrderByPlanDateDesc(userId);
    }

    @Transactional
    public WorkPlan saveWorkPlan(WorkPlan plan) {
        return workPlanRepository.save(plan);
    }

    @Transactional
    public WorkPlan updateWorkPlan(Long id, WorkPlan plan) {
        WorkPlan existing = workPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("工作计划不存在"));
        existing.setTitle(plan.getTitle());
        existing.setContent(plan.getContent());
        existing.setPlanDate(plan.getPlanDate());
        existing.setPlanType(plan.getPlanType());
        existing.setPriority(plan.getPriority());
        existing.setStatus(plan.getStatus());
        if ("COMPLETED".equals(plan.getStatus()))
            existing.setFinishTime(LocalDateTime.now());
        return workPlanRepository.save(existing);
    }

    @Transactional
    public void deleteWorkPlan(Long id) {
        workPlanRepository.deleteById(id);
    }
}
