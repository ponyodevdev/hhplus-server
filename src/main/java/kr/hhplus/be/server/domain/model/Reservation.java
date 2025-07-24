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
