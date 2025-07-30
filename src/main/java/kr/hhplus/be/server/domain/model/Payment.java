package kr.hhplus.be.server.domain.model;

public class Payment {

    private long usedPoint;
    private long userId;
    private long currentPoint;

    public Payment(long currentPoint) {
        this.currentPoint = currentPoint;
    }

    public Payment(long userId,long currentPoint) {
        this.userId = userId;
        this.currentPoint = currentPoint;
    }

    public Payment() {

    }


    public void usePoint(long amount, long currentPoint){
        if (amount <= 0) {
            throw new IllegalArgumentException("사용 포인트는 100포인트 이상이어야 합니다.");
        }
        if (amount % 100 != 0){
            throw new IllegalArgumentException("포인트는 100포인트 단위로만 사용할 수 있습니다.");
        }
        if (amount > currentPoint){
            throw new IllegalArgumentException("잔고가 부족합니다.");
        }
        this.usedPoint += amount;
    }

    public long point() {
        return currentPoint;
    }

    public long getUsedPoint() {
        return usedPoint;
    }
}
