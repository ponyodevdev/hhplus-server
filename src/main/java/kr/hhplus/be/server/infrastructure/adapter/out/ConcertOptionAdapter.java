package kr.hhplus.be.server.infrastructure.adapter.out;

import kr.hhplus.be.server.application.port.out.ConcertOptionPort;
import kr.hhplus.be.server.domain.model.ConcertOption;
import kr.hhplus.be.server.infrastructure.persistence.ConcertOptionJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ConcertOptionAdapter implements ConcertOptionPort {

    private final ConcertOptionJpaRepository concertOptionJpaRepository;

    public ConcertOptionAdapter(ConcertOptionJpaRepository concertOptionJpaRepository) {
        this.concertOptionJpaRepository = concertOptionJpaRepository;
    }

    @Override
    public List<ConcertOption> findAll() {
        return concertOptionJpaRepository.findAll();
    }

    @Override
    public List<ConcertOption> findByConcertId(long concertId){
        return concertOptionJpaRepository.findByConcertId(concertId);
    }
}