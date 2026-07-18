package com.smartcampus.smartcampus.repository;

import com.smartcampus.smartcampus.entity.PaymentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PaymentRecordRepository extends JpaRepository<PaymentRecord, Long> {
    List<PaymentRecord> findByStudentIdOrderBySemesterDesc(Long studentId);
    List<PaymentRecord> findBySemesterOrderByPaymentStatusAsc(String semester);
    List<PaymentRecord> findByPaymentStatus(String paymentStatus);
}
