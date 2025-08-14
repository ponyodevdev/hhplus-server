package kr.hhplus.be.server.application.port.in;

import kr.hhplus.be.server.application.cache.SeatAvailabilityCache;
import kr.hhplus.be.server.application.port.in.aop.DistributedLock;
import kr.hhplus.be.server.application.port.out.SeatPort;
import kr.hhplus.be.server.domain.model.Seat;
import kr.hhplus.be.server.dto.concert.SeatInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatUseCaseImpl implements SeatUseCase {

    private final SeatPort seatPort;
    private final Clock clock;
    private final SeatAvailabilityCache availability;

    @Override
    @DistributedLock(
            key = "'seat:' + #seatId",
            waitTime = 5,
            leaseTime = -1,
            fair = true
    )
    public void assignSeat(Long seatId, UUID userId) {
        Seat seat = seatPort.findById(seatId)
                .orElseThrow(() -> new NoSuchElementException("해당 좌석이 존재하지 않습니다."));
        seat.assignTo(userId, LocalDateTime.now(clock));
        seatPort.save(seat);

        availability.decrementAfterCommit(seat.getOptionId());
    }

    @Override
    @DistributedLock(key = "'seat:' + #p0", waitTime = 1, leaseTime = 3) // #p0 = seatId
    public void cancelSeat(Long seatId) {
        LocalDateTime now = LocalDateTime.now(clock);

        Seat seat = seatPort.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("seat not found: " + seatId));

        // 실제로 점유 중이었는지 체크(캐시 +1은 '해제될 때만')
        boolean wasOccupied = seat.isOccupied(now);
        Long optionId = seat.getOptionId();

        seat.cancel();
        seatPort.save(seat);

        if (wasOccupied) {
            // DB 커밋 성공 후에만 카운터 +1
            availability.incrementAfterCommit(optionId);
        }
    }




}
