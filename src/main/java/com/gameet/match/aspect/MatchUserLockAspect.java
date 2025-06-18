package com.gameet.match.aspect;

import com.gameet.global.exception.CustomException;
import com.gameet.global.exception.ErrorCode;
import com.gameet.match.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class MatchUserLockAspect {

    private final MatchRepository matchRepository;

    @Around("@annotation(com.gameet.match.annotation.MatchUserLockable) && args(userId,..)")
    public Object aroundAcquireMatchUserLock(ProceedingJoinPoint point, Long userId) throws Throwable {
        int retryCount = 0;
        while (!matchRepository.tryLock(userId)) {
            if (++retryCount > 10) {
                throw new CustomException(ErrorCode.MATCH_LOCK_ACQUISITION_FAILED);
            }
            Thread.sleep(100);
        }

        try {
            if (!matchRepository.isMatchUserExists(userId)) {
                throw new CustomException(ErrorCode.ALREADY_MATCHED);
            }
            return point.proceed();
        } finally {
            matchRepository.releaseLock(userId);
        }
    }
}
