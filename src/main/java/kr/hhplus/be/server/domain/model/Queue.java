package kr.hhplus.be.server.domain.model;

import jakarta.persistence.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "queue")
public class Queue {

    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID tokenId;

    @Column(nullable = false, columnDefinition = "BINARY(16)")
    private UUID userId;

    @Column(nullable = false)
    private LocalDateTime issuedAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    protected Queue() {
        // JPA 기본 생성자
    }

    public Queue(UUID tokenId, UUID userId, LocalDateTime issuedAt, LocalDateTime expiresAt) {
        this.tokenId = tokenId;
        this.userId = userId;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
    }

    public Queue(UUID tokenId, UUID userId, LocalDateTime issuedAt, Duration ttl) {
        this(tokenId, userId, issuedAt, issuedAt.plus(ttl));
    }

    public boolean isExpired(LocalDateTime now) {
        return now.isAfter(expiresAt);
    }

    public Duration remainingWaitTime(LocalDateTime now) {
        return Duration.between(now, expiresAt).isNegative() ? Duration.ZERO : Duration.between(now, expiresAt);
    }



    public UUID getTokenId() {
        return tokenId;
    }

    public UUID getUserId() {
        return userId;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
}
