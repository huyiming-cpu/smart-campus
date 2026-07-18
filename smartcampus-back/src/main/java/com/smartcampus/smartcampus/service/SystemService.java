package com.smartcampus.smartcampus.service;

import com.smartcampus.smartcampus.dto.Response;
import com.smartcampus.smartcampus.entity.*;
import com.smartcampus.smartcampus.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SystemService {

    private final SystemRepository systemRepository;
    private final LoginLogRepository loginLogRepository;
    private final FixedAssetRepository fixedAssetRepository;
    private final LabRepository labRepository;
    private final IdentityRegistryRepository identityRegistryRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    // ==================== A-02 仪表盘 ====================
    public Response.Dashboard getDashboard() {
        Response.Dashboard d = new Response.Dashboard();
        d.setTodayActiveUsers((int) loginLogRepository.countTodayActiveUsers());
        d.setTotalUsers(userRepository.count());
        d.setPendingRegistrations(userRepository.countByStatus("PENDING"));
        d.setServerStatus("RUNNING");
        return d;
    }

    // ==================== A-17 系统参数配置 ====================
    public List<SysConfig> listConfigs(String configType) {
        if (configType != null && !configType.isEmpty()) return systemRepository.findByConfigType(configType);
        return systemRepository.findAll();
    }
    public String getConfig(String key) {
        return systemRepository.findByConfigKey(key).map(SysConfig::getConfigValue).orElse(null);
    }
    @Transactional
    public SysConfig updateConfig(Long id, String configValue) {
        SysConfig c = systemRepository.findById(id).orElseThrow(() -> new RuntimeException("配置不存在"));
        c.setConfigValue(configValue); return systemRepository.save(c);
    }

    @Transactional
    public void updateConfigByKey(String key, String configValue) {
        int rows = systemRepository.updateValueByKey(key, configValue);
        if (rows == 0) throw new RuntimeException("配置不存在: " + key);
    }

    // ==================== A-15 日志审计 ====================
    public List<LoginLog> listLoginLogs(Long userId, int limit) {
        if (userId != null) return loginLogRepository.findByUserIdOrderByLoginTimeDesc(userId).stream().limit(limit).collect(Collectors.toList());
        return loginLogRepository.findAll().stream().sorted((a,b)->b.getLoginTime().compareTo(a.getLoginTime())).limit(limit).collect(Collectors.toList());
    }

    // ==================== 通讯录 L-03 (enrich with user info) ====================

    public List<Map<String, Object>> getDirectory(String department) {
        List<UserProfile> profiles;
        if (department != null && !department.isEmpty()) profiles = userProfileRepository.findByDepartment(department);
        else profiles = userProfileRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        for (UserProfile p : profiles) {
            userRepository.findById(p.getUserId()).ifPresent(u -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("userId", u.getId()); m.put("name", u.getName());
                m.put("identityNumber", u.getIdentityNumber());
                m.put("phone", u.getPhone()); m.put("email", u.getEmail());
                m.put("gender", u.getGender());
                m.put("department", p.getDepartment()); m.put("title", p.getTitle());
                m.put("grade", p.getGrade()); m.put("className", p.getClassName());
                m.put("major", p.getMajor());
                result.add(m);
            });
        }
        return result;
    }

    // ==================== 固定资产 ====================
    public List<FixedAsset> listAssets(String department) {
        if (department != null && !department.isEmpty()) return fixedAssetRepository.findByDepartment(department);
        return fixedAssetRepository.findAll();
    }
    @Transactional public FixedAsset saveAsset(FixedAsset a) { return fixedAssetRepository.save(a); }
    @Transactional public void deleteAsset(Long id) { fixedAssetRepository.deleteById(id); }

    public Response.AssetStats assetStats(String department) {
        List<FixedAsset> assets;
        if (department != null && !department.isEmpty()) {
            assets = fixedAssetRepository.findByDepartment(department);
        } else {
            assets = fixedAssetRepository.findAll();
        }
        Response.AssetStats stats = new Response.AssetStats();
        stats.setTotalCount(assets.size());
        BigDecimal totalValue = assets.stream().map(a -> a.getPrice() != null ? a.getPrice() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalValue(totalValue);
        Map<String, List<FixedAsset>> byDept = assets.stream().collect(Collectors.groupingBy(a -> a.getDepartment() != null ? a.getDepartment() : "未分类"));
        List<Response.AssetDetail> details = new ArrayList<>();
        byDept.forEach((dept, list) -> {
            Response.AssetDetail d = new Response.AssetDetail();
            d.setDepartment(dept);
            d.setCount(list.size());
            d.setTotalValue(list.stream().map(a -> a.getPrice() != null ? a.getPrice() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add));
            List<Response.AssetItem> items = list.stream().map(a -> {
                Response.AssetItem item = new Response.AssetItem();
                item.setId(a.getId()); item.setAssetName(a.getAssetName());
                item.setAssetNo(a.getAssetNo()); item.setAssetType(a.getAssetType());
                item.setPrice(a.getPrice());
                item.setPurchaseDate(a.getPurchaseDate() != null ? a.getPurchaseDate().toString() : "");
                item.setStatus(a.getStatus()); item.setRemark(a.getRemark());
                return item;
            }).collect(Collectors.toList());
            d.setItems(items);
            details.add(d);
        });
        stats.setDetails(details);
        return stats;
    }

    // ==================== 实验室 ====================
    public List<Lab> listLabs(String department, String status) {
        if (status != null) return labRepository.findByStatus(status);
        if (department != null) return labRepository.findByDepartment(department);
        return labRepository.findAll();
    }
    @Transactional public Lab saveLab(Lab l) { return labRepository.save(l); }
    @Transactional public void deleteLab(Long id) { labRepository.deleteById(id); }

    // ==================== L-08 新生报到统计 ====================
    public List<Response.StatCard> enrollmentStats(String department) {
        List<IdentityRegistry> list = identityRegistryRepository.findAll();
        if (department != null && !department.isEmpty()) list = list.stream().filter(r -> department.equals(r.getDepartment())).collect(Collectors.toList());
        long registered = list.stream().filter(r -> r.getStatus() == 1).count();
        List<Response.StatCard> stats = new ArrayList<>();
        Response.StatCard total = new Response.StatCard();
        total.setLabel("录取总数"); total.setValue(String.valueOf(list.size()));
        total.setExtra("已报到 " + registered + " / 报到率 " + (list.isEmpty()?0:String.format("%.1f%%",registered*100.0/list.size())));
        stats.add(total);
        list.stream().collect(Collectors.groupingBy(r->r.getDepartment()!=null?r.getDepartment():"未知",Collectors.counting()))
            .forEach((dept,count)->{Response.StatCard c=new Response.StatCard();c.setLabel(dept);c.setValue(String.valueOf(count));stats.add(c);});
        return stats;
    }

    // ==================== L-11 在校生总量统计 ====================
    public List<Response.DeptStudents> studentCountStats(String department) {
        // 只查学生
        List<UserProfile> allProfiles = userProfileRepository.findAll();
        List<User> allUsers = userRepository.findAll();
        // 构建 userId → User 映射
        Map<Long, User> userMap = allUsers.stream().collect(Collectors.toMap(User::getId, u -> u));
        // 过滤出学生（有 grade 字段的是学生）
        List<UserProfile> studentProfiles = allProfiles.stream()
            .filter(p -> p.getGrade() != null)
            .collect(Collectors.toList());
        if (department != null && !department.isEmpty())
            studentProfiles = studentProfiles.stream().filter(p -> department.equals(p.getDepartment())).collect(Collectors.toList());

        // 按部门分组
        Map<String, List<UserProfile>> byDept = studentProfiles.stream()
            .collect(Collectors.groupingBy(p -> p.getDepartment() != null ? p.getDepartment() : "未分类"));

        List<Response.DeptStudents> result = new ArrayList<>();
        byDept.forEach((dept, profiles) -> {
            Response.DeptStudents ds = new Response.DeptStudents();
            ds.setDepartment(dept);
            ds.setTotalCount(profiles.size());
            List<Response.StudentInfo> students = new ArrayList<>();
            for (UserProfile p : profiles) {
                Response.StudentInfo si = new Response.StudentInfo();
                User u = userMap.get(p.getUserId());
                si.setName(u != null ? u.getName() : "");
                si.setStudentNo(u != null ? u.getIdentityNumber() : "");
                si.setGrade(p.getGrade());
                si.setClassName(p.getClassName());
                si.setMajor(p.getMajor());
                students.add(si);
            }
            students.sort((a, b) -> {
                int g = compareStr(b.getGrade(), a.getGrade());
                if (g != 0) return g;
                return compareStr(a.getStudentNo(), b.getStudentNo());
            });
            ds.setStudents(students);
            result.add(ds);
        });
        result.sort((a, b) -> compareStr(a.getDepartment(), b.getDepartment()));
        return result;
    }

    private int compareStr(String a, String b) {
        if (a == null && b == null) return 0;
        if (a == null) return -1;
        if (b == null) return 1;
        return a.compareTo(b);
    }

    private Response.StatCard statCard(String l, String v, String e) { Response.StatCard c=new Response.StatCard();c.setLabel(l);c.setValue(v);c.setExtra(e);return c; }

    // ==================== L-09 注册缴费统计 ====================
    private final PaymentRecordRepository paymentRepo;
    public Response.PaymentStats paymentStats(String department) {
        Response.PaymentStats s = new Response.PaymentStats();
        List<PaymentRecord> all = paymentRepo.findAll();
        // 按部门过滤（通过学生ID查学生所属部门）
        if (department != null) {
            all = all.stream().filter(p -> {
                return userProfileRepository.findById(p.getStudentId()).map(up -> department.equals(up.getDepartment())).orElse(false);
            }).collect(Collectors.toList());
        }
        s.setTotalStudents(all.size());
        s.setRegisteredCount((int)all.stream().filter(p -> !"UNPAID".equals(p.getPaymentStatus())).count());
        s.setPaidCount((int)all.stream().filter(p -> "PAID".equals(p.getPaymentStatus())).count());
        s.setUnpaidCount((int)all.stream().filter(p -> "UNPAID".equals(p.getPaymentStatus())).count());
        BigDecimal total = all.stream().map(p -> p.getTotalAmount()!=null?p.getTotalAmount():BigDecimal.ZERO).reduce(BigDecimal.ZERO,BigDecimal::add);
        BigDecimal paid = all.stream().map(p -> p.getPaidAmount()!=null?p.getPaidAmount():BigDecimal.ZERO).reduce(BigDecimal.ZERO,BigDecimal::add);
        s.setTotalAmount(total);
        s.setPaidAmount(paid);
        return s;
    }

    /** 获取领导所属部门 */
    public String getDepartment(Long userId) {
        return userProfileRepository.findById(userId).map(UserProfile::getDepartment).orElse("校办");
    }
}
