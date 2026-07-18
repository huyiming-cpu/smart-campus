package com.smartcampus.smartcampus.repository;

import com.smartcampus.smartcampus.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findByUserId(Long userId);
    Optional<Card> findByCardNo(String cardNo);
}
