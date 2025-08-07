package kr.hhplus.be.server.application.service;

import kr.hhplus.be.server.domain.model.PointHistory;
import kr.hhplus.be.server.domain.model.TransactionType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Component
public class PointExpirationDomainService {

    public long calculateExpiredAmount(List<PointHistory> histories, LocalDateTime now) {
        return histories.stream()
                .filter(h -> h.getType() == TransactionType.CHARGE)
                .filter(h -> isExpired(h.getTimestamp(), now))
                .mapToLong(PointHistory::getAmount)
                .sum();
    }

    private boolean isExpired(long timestampMillis, LocalDateTime now) {
        LocalDateTime timestamp = LocalDateTime.ofEpochSecond(timestampMillis / 1000, 0, ZoneOffset.ofHours(9));
        return timestamp.isBefore(now.minusYears(1));
    }
}
