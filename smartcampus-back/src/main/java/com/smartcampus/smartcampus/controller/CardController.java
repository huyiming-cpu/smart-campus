package com.smartcampus.smartcampus.controller;

import com.smartcampus.smartcampus.common.Result;
import com.smartcampus.smartcampus.dto.Response;
import com.smartcampus.smartcampus.entity.Card;
import com.smartcampus.smartcampus.entity.CardTransaction;
import com.smartcampus.smartcampus.entity.PaymentRecord;
import com.smartcampus.smartcampus.security.JwtUtil;
import com.smartcampus.smartcampus.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;
    private final JwtUtil jwtUtil;

    // ==================== 学生充值 ====================
    @PostMapping("/student/card/recharge")
    public Result<Card> recharge(@RequestHeader("Authorization") String token,
                                  @RequestBody Map<String, Object> body) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        BigDecimal amount = new BigDecimal(body.get("amount").toString());
        return Result.success(cardService.recharge(userId, amount));
    }

    // ==================== 学生 S-12 / S-13 ====================

    @GetMapping("/student/card")
    public Result<Response.CardInfo> getMyCard(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        return Result.success(cardService.getMyCard(userId));
    }

    @GetMapping("/student/payments")
    public Result<List<PaymentRecord>> getMyPayments(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        return Result.success(cardService.getMyPayments(userId));
    }

    // ==================== 教师一卡通 T-12 ====================
    @GetMapping("/teacher/card")
    public Result<Response.CardInfo> getTeacherCard(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        return Result.success(cardService.getMyCard(userId));
    }

    // ==================== 管理员 A-13 一卡通管理 ====================

    @GetMapping("/admin/cards")
    public Result<List<Card>> listAllCards() {
        return Result.success(cardService.listAllCards());
    }

    @PostMapping("/admin/cards/transaction")
    public Result<CardTransaction> addTransaction(@RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        String cardNo = (String) body.get("cardNo");
        String type = (String) body.get("type");
        BigDecimal amount = new BigDecimal(body.get("amount").toString());
        String merchant = (String) body.getOrDefault("merchant", "管理端录入");
        return Result.success(cardService.addTransaction(userId, cardNo, type, amount, merchant));
    }

    @PostMapping("/admin/cards/sync")
    public Result<String> syncCardData() {
        return Result.success(cardService.syncCardData());
    }

    // ==================== 管理员 A-14 缴费管理 ====================

    @GetMapping("/admin/payments")
    public Result<List<PaymentRecord>> listAllPayments(
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) String status) {
        return Result.success(cardService.listAllPayments(semester, status));
    }

    @PostMapping("/admin/payments")
    public Result<PaymentRecord> savePayment(@RequestBody PaymentRecord record) {
        return Result.success(cardService.savePayment(record));
    }

    @DeleteMapping("/admin/payments/{id}")
    public Result<Void> deletePayment(@PathVariable Long id) {
        cardService.deletePayment(id);
        return Result.success();
    }
}
