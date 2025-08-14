package kr.hhplus.be.server.application.support;

import java.util.UUID;

public final class LockKeys {
    public static String seat(long concertId, long seatId) { return "seat:" + concertId + ":" + seatId; }
    public static String userPoints(UUID userId) { return "user:points:" + userId; }
    public static String queueToken(UUID userId) { return "queue:token:" + userId; }
}