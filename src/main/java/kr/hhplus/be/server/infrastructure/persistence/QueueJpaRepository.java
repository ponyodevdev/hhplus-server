package kr.hhplus.be.server.infrastructure.persistence;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.model.Queue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface QueueJpaRepository extends JpaRepository<Queue, UUID> {


    @Query("SELECT q FROM Queue q WHERE q.userId = :userId AND q.expiresAt > :now ORDER BY q.expiresAt DESC")
    Optional<Queue> findValidTokenByUserId(@Param("userId") UUID userId, @Param("now") LocalDateTime now);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT q FROM Queue q WHERE q.userId = :userId")
    Optional<Queue> findByUserIdWithLock(@Param("userId") UUID userId);
}