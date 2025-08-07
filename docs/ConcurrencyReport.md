
#  동시성 문제 분석 및 해결 방안 보고서

## 1. 문제 식별

본 서비스는 좌석 예매, 포인트 충전/사용, 토큰 발급 등 사용자 요청이 **실시간으로 처리되어야 하는 기능들을 포함하고 있다. 이러한 기능은 여러 사용자가 동시에 접근할 수 있으며, 이로 인해 데이터 정합성 문제가 발생할 수 있다.

대표적인 예시는 다음과 같다:

- 여러 사용자가 동일한 좌석을 동시에 예매하는 경우
- 동일 사용자의 포인트를 여러 요청이 동시에 사용하는 경우
- 동일 유저에 대해 중복된 토큰을 발급받거나, 만료 상태를 무시하고 상태 확인을 시도하는 경우
- 잔여 좌석 수 또는 수량 감소 시 충돌이 발생하는 경우
- 중복 API 요청이 발생하여 상태가 꼬이는 문제

---

## 2. 문제 분석

### 2-1. 좌석 예매 동시성 문제

```java
public void assignTo(UUID userId, LocalDateTime now) {
    if (this.ownerId != null && !isExpired(now)) {
        throw new IllegalStateException("이미 다른 사용자가 좌석을 점유했습니다.");
    }
    this.ownerId = userId;
    this.expiresAt = now.plusMinutes(5);
}
```

- 동시에 조건 검사를 통과한 요청이 모두 값을 갱신할 수 있어, 저장 시점 충돌을 제어할 수 없음
- MySQL InnoDB의 REPEATABLE READ 격리 수준은 phantom read는 방지하지만, 조건 기반 병행 갱신은 막지 못함

### 2-2. 포인트 사용/충전 충돌

- 동일한 사용자가 여러 요청으로 동시에 포인트를 사용하는 경우
- 잔액보다 많은 결제가 가능하거나, 포인트가 중복 차감될 수 있음
- `@Version` 없이 처리 시 정합성 문제 발생

### 2-3. 중복 토큰 발급 문제

- 한 사용자가 동시에 여러 번 토큰을 요청하면 중복된 토큰이 발급될 수 있음
- 이후 상태 확인 및 만료 처리 로직에서 오류 발생 가능

### 2-4. 잔여 좌석 수 감소 경합

- 두 트랜잭션이 동시에 수량 감소 처리 시, 초과 예약 발생 가능
- 특히 수량이 1일 경우 경쟁 조건이 명확히 드러남

### 2-5. 중복 API 요청

- 클라이언트의 재요청, 네트워크 지연 등으로 동일 요청이 여러 번 처리될 수 있음
- 포인트 중복 차감, 예약 중복 등의 문제 발생 가능

---

## 3. 해결 방안

### 3-1. 비관적 락 (Pessimistic Locking)

- 좌석 예매처럼 충돌 가능성이 높은 경우 row-level lock 적용

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT s FROM Seat s WHERE s.seatId = :seatId")
Optional<Seat> findSeatForUpdate(@Param("seatId") Long seatId);
```

### 3-2. 낙관적 락 (Optimistic Locking)

- 포인트처럼 충돌 가능성은 낮지만 정합성이 중요한 경우

```java
@Version
private Long version;
```

### 3-3. 유니크 제약 + 선행 조회

- 토큰 발급 시, 사용자 ID에 유니크 인덱스 설정
- `if exists` 로 기존 토큰 확인 후 생성 여부 결정

### 3-4. 수량 감소 조건 업데이트

```sql
UPDATE inventory 
SET count = count - 1 
WHERE count > 0;
```

### 3-5. Idempotency Key 적용

- 클라이언트 요청에 고유 UUID 키 부여
- 서버는 동일 키 요청은 한 번만 처리

### 3-6. 캐시 동기화

- Cache Aside 패턴 적용
- TTL 짧게 유지하거나 캐시 삭제 후 즉시 갱신

---

## 4. 실제 적용 방안

| 기능             | 적용 전략                                         |
|------------------|--------------------------------------------------|
| 좌석 예매        | 비관적 락 + 트랜잭션 범위 내 갱신                 |
| 포인트 사용/충전 | 낙관적 락 (`@Version`) 기반 동시성 제어          |
| 토큰 발급        | 유니크 제약 + 선행 조회 (`if exists`)           |
| 수량 감소        | 조건부 `UPDATE` 쿼리 (`WHERE count > 0`) 적용   |
| 중복 요청        | Idempotency Key + 요청 이력 캐싱                 |

---

## 5. 테스트를 통한 검증

통합 테스트를 통해 다음 상황을 검증:

- **동일 좌석에 대해 두 사용자가 동시에 예매** 시도 → 1명 성공, 나머지는 예외 발생
- **포인트 중복 사용 시도** → 하나만 성공, 나머지는 `ObjectOptimisticLockingFailureException` 발생
- **중복 토큰 발급 시도** → 첫 요청만 처리, 이후는 기존 토큰 반환 또는 무시

### 예시 테스트 코드

```java
assertThatThrownBy(() -> reservationUseCase.reserveSeat(seatId, secondUser, now))
    .isInstanceOf(IllegalStateException.class)
    .hasMessageContaining("이미 예약된 좌석입니다");

assertThat(successCount).isEqualTo(1); // 낙관적 락 성공 1명
```

---

## 6. 결론

- 실시간성과 정확한 상태 관리가 중요한 서비스에서는 기능별로 적절한 트랜잭션 전략과 락 전략이 필요함
- 좌석 예매는 비관적 락, 포인트 충전/사용은 낙관적 락으로 제어
- 중복 요청 방지, 수량 감소 조건 처리, 유니크 제약 활용 등을 통해 데이터 정합성 확보
- 통합 테스트로 실제 충돌 시나리오를 검증하여 안정성 강화

