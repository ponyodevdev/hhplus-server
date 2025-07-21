## 콘서트 예약 서비스 API 명세서

### 1. 대기열 토큰 발급 API

- **URL:** `/api/queue/token`
- **Method:** `POST`
- **Headers:**
    - `Content-Type: application/json`
- **Request Body:**
```json
{
  "concertId": 1
}
```

- Response:

  - Status Code: 201 Created

```json
{
  "waitingQueue": {
    "id": 1001,
    "concertId": 1,
    "uuid": "4a3e1d30-f9e3-11ed-be56-0242ac120002",
    "status": "WAITING",
    "expiredAt": "2025-07-17T18:10:00+09:00",
    "createdAt": "2025-07-17T18:00:00+09:00",
    "updatedAt": "2025-07-17T18:00:00+09:00"
  }
}



```

### 2. 대기열 상태 조회 API

- **URL:** `/api/waiting-queues/{uuid}`
- **Method:** `GET`
- **Headers:**
    - `Content-Type: application/json`
- **Authorization:** `Bearer {queue-token}`
- **Response:**
  - Status Code: 200 OK

```json

{
  "queueNumber": 5,
  "estimatedWaitTimeSeconds": 120,
  "status": "WAITING"
}
```


### 3. 예약 가능한 공연 일정 조회 API
**URL:** `/api/concerts/{concertId}/options`
- **Method:** `GET`
- **Headers:**
    - `Content-Type: application/json`
- **Authorization:** `Bearer {queue-token}`
- **Response:**
    - Status Code: 200 OK

```json
[
  {
    "optionId": 11,
    "concertId": 1,
    "concertDateTime": "2025-08-01T20:00:00",
    "price": 50000
  },
  {
    "optionId": 12,
    "concertId": 1,
    "concertDateTime": "2025-08-02T20:00:00",
    "price": 60000
  }
]
```

### 4. 공연 좌석 정보 조회 API

**URL:** `/api/concert-options/{optionId}/seats`
- **Method:** `GET`
- **Headers:**
    - `Content-Type: application/json`
- **Authorization:** `Bearer {queue-token}`
- **Response:**
    - Status Code: 200 OK

```json
[
  {
    "seatId": 101,
    "optionId": 11,
    "seatLabel": "A1",
    "seatStatus": "AVAILABLE"
  },
  {
    "seatId": 102,
    "optionId": 11,
    "seatLabel": "A2",
    "seatStatus": "RESERVED"
  }
]
```

### 5. 좌석 예약 API

**URL:** `/api/reservations`
- **Method:** `POST`
- **Headers:**
    - `Content-Type: application/json`
- **Authorization:** `Bearer {x-queue-token}`
- **Headers:**
    - `Content-Type: application/json`
- **Request Body:**
```json
{
  "optionId": 11,
  "seatId": 102
}
```

- **Response**:

    - Status Code: 201 Created

```json
{
  "reservationId": "a9e18f31-dfa0-4e3b-8c72-5bd1bc149ec7",
  "expiresAt": "2025-07-17T18:10:00+09:00"
}
```

### 6. 결제 요청 API
**URL:** `/api/payments`
- **Method:** `POST`
- **Headers:**
    - `Content-Type: application/json`
- **Authorization:** `Bearer {queue-token}`
- **Headers:**
    - `Content-Type: application/json`
- **Request Body:**
```json
{
  "reservationId": "a9e18f31-dfa0-4e3b-8c72-5bd1bc149ec7",
  "amount": 50000
}
```
- **Response**:

    - Status Code: 200 OK

```json
{
  "paymentId": "f6bb72a9-22cf-4eec-a869-38f91ec2d2d7",
  "amount": 50000,
  "paymentDate": "2025-07-17T18:06:00+09:00"
}
```

### 7. 잔액 조회 API
**URL:** `/api/users/{userId}/balance`
- **Method:** `GET`
  - **Headers:**
      - `Content-Type: application/json`
  - **Response:**
      - Status Code: 200 OK

```json
{
  "balance": 100000
}
```


### 8. 잔액 충전 API
**URL:** `/api/v1/users/{userId}/balance/charge`
- **Method:** `POST`
  - **Headers:**
      - `Content-Type: application/json`
  - **Request Body:**
```json
{
  "amount": 30000
}
```

- **Response**:

    - Status Code: 200 OK

```json

{
  "balance": 130000
}
```