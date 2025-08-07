package kr.hhplus.be.server.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "concert_option")
public class ConcertOption {

    @Id
    private Long id;

    @Column(nullable = false)
    private Long concertId;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private int price;

    protected ConcertOption() {
        // JPA 기본 생성자
    }

    public ConcertOption(Long id, Long concertId, LocalDateTime startTime, int price) {
        if (startTime == null) {
            throw new IllegalArgumentException("공연 시작 시간은 필수입니다.");
        }
        if (price < 0) {
            throw new IllegalArgumentException("가격은 음수일 수 없습니다.");
        }

        this.id = id;
        this.concertId = concertId;
        this.startTime = startTime;
        this.price = price;
    }

    public boolean isReservable(LocalDateTime now) {
        return now.isBefore(startTime);
    }

    public Long getId() {
        return id;
    }

    public Long getConcertId() {
        return concertId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public int getPrice() {
        return price;
    }
}
