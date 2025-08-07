package kr.hhplus.be.server.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "seat")
public class Seat {

    @Id
    private Long seatId;

    private Long optionId;

    private String seatLabel;

    @Column(columnDefinition = "BINARY(16)")
    private UUID ownerId;

    private LocalDateTime expiresAt;

    protected Seat() {
        // JPA 기본 생성자
    }

    public Seat(Long seatId, Long optionId, String seatLabel) {
        this.seatId = seatId;
        this.optionId = optionId;
        this.seatLabel = seatLabel;
    }

    public void assignTo(UUID userId, LocalDateTime now) {
        if (this.ownerId != null && !isExpired(now)) {
            throw new IllegalStateException("이미 다른 사용자가 좌석을 점유했습니다.");
        }
        this.ownerId = userId;
        this.expiresAt = now.plusMinutes(5);
    }

    public boolean isExpired(LocalDateTime now) {
        return expiresAt != null && now.isBefore(expiresAt) == false;
    }

    public boolean isOccupied(LocalDateTime now) {
        return ownerId != null && !isExpired(now);
    }

    public Long getSeatId() {
        return seatId;
    }

    public Long getOptionId() {
        return optionId;
    }

    public String getSeatLabel() {
        return seatLabel;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
}
