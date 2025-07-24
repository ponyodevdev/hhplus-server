package kr.hhplus.be.server.application.port.out;

import kr.hhplus.be.server.domain.model.ConcertOption;

import java.util.List;

public interface ConcertOptionPort {
    List<ConcertOption> findAll(); // 모든 공연 일정 엔티티 조회
}
