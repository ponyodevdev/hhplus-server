package kr.hhplus.be.server.domain.model;

public class PointHistory {

    private final long userId;
    private final long amount;
    private final TransactionType type;
    private final long timestamp; // milliseconds

    public PointHistory(long userId, long amount, TransactionType type, long timestamp) {
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.timestamp = timestamp;
    }

    // getter methods
    public long userId() {
        return userId;
    }

    public long amount() {
        return amount;
    }

    public TransactionType type() {
        return type;
    }

    public long timestamp() {
        return timestamp;
    }
}
