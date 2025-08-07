package kr.hhplus.be.server.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "payment")
public class Payment {

    @Id
    private Long userId;

    private long usedPoint;
    private long currentPoint;

    @Version
    private Long version;

    protected Payment() {

    }

    public Payment(Long userId, long currentPoint) {
        this.userId = userId;
        this.currentPoint = currentPoint;
    }

    public Long getVersion() {
        return version;
    }


    public void usePoint(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("사용 포인트는 100포인트 이상이어야 합니다.");
        }
        if (amount % 100 != 0) {
            throw new IllegalArgumentException("포인트는 100포인트 단위로만 사용할 수 있습니다.");
        }
        if (amount > currentPoint) {
            throw new IllegalArgumentException("잔고가 부족합니다.");
        }
        this.usedPoint += amount;
        this.currentPoint -= amount;
    }

    public void updatePoint(long newPoint) {
        this.currentPoint = newPoint;
    }

    public Long getUserId() {
        return userId;
    }

    public long getUsedPoint() {
        return usedPoint;
    }

    public long getCurrentPoint() {
        return currentPoint;
    }

    public long point() {
        return currentPoint;
    }
}