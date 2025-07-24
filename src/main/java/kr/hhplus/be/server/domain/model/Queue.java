package kr.hhplus.be.server.domain.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public class Queue {

    private final UUID tokenId;
    private final UUID userId;
    private final LocalDateTime issuedAt;
    private final LocalDateTime expiresAt;


    public Queue(UUID tokenId, UUID userId, LocalDateTime issuedAt, LocalDateTime expiresAt) {
        this.tokenId = tokenId;
        this.userId = userId;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
    }

    public Queue(UUID tokenId, UUID userId, LocalDateTime issuedAt, Duration ttl) {
        this(tokenId, userId, issuedAt, issuedAt.plus(ttl));
    }

    public boolean isExpired(LocalDateTime now){
        return now.isAfter(expiresAt);
    }

    public Duration remainingWaitTime(LocalDateTime now){
        return Duration.between(now, expiresAt).isNegative() ? Duration.ZERO : Duration.between(now, expiresAt);
    }

    public LocalDateTime getExpiresAt(){
        return expiresAt;
    }

    public LocalDateTime getIssuedAt(){
        return issuedAt;
    }

    public UUID getTokenId() {
        return tokenId;
    }

    public UUID getUserId() {
        return userId;
    }
}
