package com.smartcampus.smartcampus.service;

import com.smartcampus.smartcampus.dto.Response;
import com.smartcampus.smartcampus.entity.*;
import com.smartcampus.smartcampus.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final CardTransactionRepository transactionRepository;
    private final PaymentRecordRepository paymentRecordRepository;

    // ==================== 学生充值 ====================
    @Transactional
    public Card recharge(Long userId, BigDecimal amount) {
        Card card = cardRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("未找到校园卡，请联系管理员办理"));
        card.setBalance(card.getBalance().add(amount));
        cardRepository.save(card);
        // 记录充值交易
        CardTransaction tx = new CardTransaction();
        tx.setUserId(userId); tx.setCardNo(card.getCardNo());
        tx.setTransactionType("RECHARGE"); tx.setAmount(amount);
        tx.setBalance(card.getBalance()); tx.setMerchant("在线充值");
        tx.setTransactionTime(LocalDateTime.now());
        tx.setTransactionNo("R" + System.currentTimeMillis());
        tx.setSyncStatus(1);
        transactionRepository.save(tx);
        return card;
    }

    // ==================== S-12 学生一卡通信息 ====================

    public Response.CardInfo getMyCard(Long userId) {
        Card card = cardRepository.findByUserId(userId).orElse(null);
        List<CardTransaction> transactions = transactionRepository
                .findTop5ByUserIdOrderByTransactionTimeDesc(userId);
        Response.CardInfo info = new Response.CardInfo();
        if (card != null) {
            info.setCardNo(card.getCardNo());
            info.setBalance(card.getBalance());
            info.setCardStatus(card.getCardStatus());
        }
        info.setTransactions(transactions.stream().map(tx -> {
            Response.TxItem item = new Response.TxItem();
            item.setTransactionType(tx.getTransactionType());
            item.setAmount(tx.getAmount());
            item.setBalance(tx.getBalance());
            item.setMerchant(tx.getMerchant());
            item.setTransactionTime(tx.getTransactionTime());
            return item;
        }).collect(Collectors.toList()));
        return info;
    }

    // ==================== S-13 学费缴费查询 ====================

    public List<PaymentRecord> getMyPayments(Long userId) {
        return paymentRecordRepository.findByStudentIdOrderBySemesterDesc(userId);
    }

    // ==================== A-13 一卡通管理（管理员） ====================

    public List<Card> listAllCards() {
        return cardRepository.findAll();
    }

    @Transactional
    public CardTransaction addTransaction(Long userId, String cardNo, String type,
                                           BigDecimal amount, String merchant) {
        Card card = cardRepository.findByCardNo(cardNo)
                .orElseThrow(() -> new RuntimeException("一卡通不存在"));
        BigDecimal balanceAfter;
        if ("CONSUME".equals(type)) {
            balanceAfter = card.getBalance().subtract(amount);
        } else {
            balanceAfter = card.getBalance().add(amount);
        }
        card.setBalance(balanceAfter);
        cardRepository.save(card);

        CardTransaction tx = new CardTransaction();
        tx.setUserId(userId);
        tx.setCardNo(cardNo);
        tx.setTransactionType(type);
        tx.setAmount(amount);
        tx.setBalance(balanceAfter);
        tx.setMerchant(merchant);
        tx.setTransactionTime(LocalDateTime.now());
        tx.setTransactionNo("TXN" + System.currentTimeMillis());
        tx.setSyncStatus(1);
        return transactionRepository.save(tx);
    }

    /** 模拟同步一卡通消费数据 */
    @Transactional
    public String syncCardData() {
        return "同步完成，共处理 12 条一卡通消费记录";
    }

    // ==================== A-14 缴费管理（管理员） ====================

    public List<PaymentRecord> listAllPayments(String semester, String status) {
        if (semester != null) return paymentRecordRepository.findBySemesterOrderByPaymentStatusAsc(semester);
        if (status != null) return paymentRecordRepository.findByPaymentStatus(status);
        return paymentRecordRepository.findAll();
    }

    @Transactional
    public PaymentRecord savePayment(PaymentRecord record) {
        if (record.getPaidAmount() == null) record.setPaidAmount(BigDecimal.ZERO);
        if (record.getPaymentStatus() == null) record.setPaymentStatus("UNPAID");
        if ("PAID".equals(record.getPaymentStatus()) && record.getPaymentTime() == null)
            record.setPaymentTime(LocalDateTime.now());
        return paymentRecordRepository.save(record);
    }

    @Transactional
    public void deletePayment(Long id) {
        paymentRecordRepository.deleteById(id);
    }
}
