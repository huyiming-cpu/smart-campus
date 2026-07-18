package com.smartcampus.smartcampus.repository;

import com.smartcampus.smartcampus.entity.CardTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CardTransactionRepository extends JpaRepository<CardTransaction, Long> {
    List<CardTransaction> findByUserIdOrderByTransactionTimeDesc(Long userId);
    List<CardTransaction> findByCardNoOrderByTransactionTimeDesc(String cardNo);
    List<CardTransaction> findTop5ByUserIdOrderByTransactionTimeDesc(Long userId);
}
