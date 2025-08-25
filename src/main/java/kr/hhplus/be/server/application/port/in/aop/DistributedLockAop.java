package kr.hhplus.be.server.application.port.in.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DistributedLockAop {
    private final RedissonClient redissonClient;



    @Around("@annotation(distributedLock)")
    public Object lock(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        String key = "seat:" + joinPoint.getArgs()[0]; // seatId 기준
        RLock lock = redissonClient.getLock(key);

        boolean locked = false;
        try {
            locked = lock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), TimeUnit.SECONDS);
            if (!locked) throw new IllegalStateException("Lock not acquired: " + key);

            log.info("LOCK ACQUIRED: {}", key);
            Object result = joinPoint.proceed();
            log.info("TX EXECUTED: {}", key);
            return result;

        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("LOCK RELEASED: {}", key);
            }
        }
    }
}