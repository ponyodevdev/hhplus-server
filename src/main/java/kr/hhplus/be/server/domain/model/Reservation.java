package kr.hhplus.be.server.domain.model;

import jakarta.persistence.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reservation")
public class Reservation {

    public enum Status {
        RESERVED, CONFIRMED, EXPIRED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long seatId;

    @Column(nullable = false, columnDefinition = "BINARY(16)")
    private UUID userId;

    @Column(nullable = false)
    private LocalDateTime reservedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Transient
    private static final Duration HOLD_DURATION = Duration.ofMinutes(5);

    protected Reservation() {
        // JPA 기본 생성자
    }

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

    public Long getId() {
        return id;
    }
}
