package kr.hhplus.be.server.domain.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public class Reservation {

    public enum Status {
        RESERVED, CONFIRMED, EXPIRED
    }

    private final Long seatId;
    private final UUID userId;
    private final LocalDateTime reservedAt;
    private Status status;

    private static final Duration HOLD_DURATION = Duration.ofMinutes(5);

    public Reservation(Long seatId, UUID userId, LocalDateTime reservedAt) {
        this.seatId = seatId;
        this.userId = userId;
        this.reservedAt = reservedAt;
        this.status = Status.RESERVED;
    }

    public boolean isExpired(LocalDateTime now) {
        return now.isAfter(getExpiresAt());
    }

    public boolean updateStatus(LocalDateTime now) {
        if (this.status == Status.RESERVED && isExpired(now)) {
            this.status = Status.EXPIRED;
            return true;
        }
        return false;
    }

    public LocalDateTime getExpiresAt() {
        return reservedAt.plus(HOLD_DURATION);
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getReservedAt() {
        return reservedAt;
    }

    public Long getSeatId() {
        return seatId;
    }

    public UUID getUserId() {
        return userId;
    }
}
