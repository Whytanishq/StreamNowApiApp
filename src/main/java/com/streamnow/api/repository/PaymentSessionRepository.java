package com.streamnow.api.repository;

import com.streamnow.api.entity.PaymentSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PaymentSessionRepository extends JpaRepository<PaymentSession, Long> {
    Optional<PaymentSession> findBySessionId(String sessionId);

    @Transactional
    @Modifying
    @Query("UPDATE PaymentSession ps SET ps.status = 'expired' WHERE ps.status = 'pending' AND ps.expiresAt < :cutoff")
    void updateExpiredSessions(LocalDateTime cutoff);
}