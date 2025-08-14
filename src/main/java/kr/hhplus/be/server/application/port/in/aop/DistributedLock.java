package kr.hhplus.be.server.application.port.in.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {
    String key();
    long waitTime() default 2;
    long leaseTime() default -1;    // -1이면 Redisson Watchdog이 자동 연장
    TimeUnit timeUnit() default TimeUnit.SECONDS;
    boolean fair() default false;   // FIFO 순차 보장 여부
}

