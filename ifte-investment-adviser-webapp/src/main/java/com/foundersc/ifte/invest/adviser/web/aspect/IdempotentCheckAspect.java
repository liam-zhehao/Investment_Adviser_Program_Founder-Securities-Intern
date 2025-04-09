package com.foundersc.ifte.invest.adviser.web.aspect;

import cn.hutool.core.util.CharUtil;
import com.foundersc.ifte.invest.adviser.web.annotation.IdempotentCheck;
import com.foundersc.ifte.invest.adviser.web.exception.BizErrorCodeEnum;
import com.foundersc.ifte.invest.adviser.web.exception.BizException;
import com.foundersc.ifte.invest.adviser.web.service.IdempotentService;
import com.foundersc.ifte.invest.adviser.web.util.ContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.UUID;

/**
 * 幂等性校验切面
 *
 * @author wangfuwei
 * @date 2022/11/4
 */
@Aspect
@Component
@Slf4j
public class IdempotentCheckAspect {

    @Autowired
    @Qualifier("redisLockIdempotentService")
    private IdempotentService idempotentService;

    private final static String IDEM_REDIS_PREFIX = "idem";

    /**
     * 校验
     *
     * @param joinPoint
     * @return
     */
    @Around("@annotation(com.foundersc.ifte.invest.adviser.web.annotation.IdempotentCheck)")
    public Object check(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取连接点的方法签名对象
        Signature signature = joinPoint.getSignature();
        if (!(signature instanceof MethodSignature)) {
            throw new BizException(BizErrorCodeEnum.PARAM_ERROR);
        }
        MethodSignature methodSignature = (MethodSignature) signature;
        Object target = joinPoint.getTarget();
        // 获取到当前执行的方法
        Method method = target.getClass().getDeclaredMethod(methodSignature.getName(), methodSignature.getParameterTypes());
        //获取方法的注解
        IdempotentCheck annotation = method.getAnnotation(IdempotentCheck.class);
        if (annotation != null) {
            String requestId = UUID.randomUUID().toString();
            String uniqKey = getUniqKey(annotation);
            try {
                if (!idempotentService.check(uniqKey, requestId, Duration.ofSeconds(annotation.type().getExpireSeconds()))) {
                    throw new BizException(BizErrorCodeEnum.REPEAT_SUBMIT);
                }
                return joinPoint.proceed();
            } finally {
                idempotentService.reset(uniqKey, requestId);
            }
        } else {
            return joinPoint.proceed();
        }
    }

    /**
     * 幂等性校验的key
     *
     * @param annotation
     * @return
     */
    private String getUniqKey(IdempotentCheck annotation) {
        String type = annotation.type().getCode();
        String key;
        switch (annotation.checkKey()) {
            case MOBILE:
                key = ContextHolder.getMobileLoginInfo().getMobile();
                break;
            default:
                key = ContextHolder.getSimpleAccount().getClientId();
        }
        return StringUtils.joinWith(String.valueOf(CharUtil.COLON), IDEM_REDIS_PREFIX, type, key);
    }
}
