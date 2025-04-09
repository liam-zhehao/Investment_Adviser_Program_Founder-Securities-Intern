package com.foundersc.ifte.invest.adviser.web.config.support;

import com.foundersc.ifte.invest.adviser.web.util.EnvUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * 非local环境才注入
 */
@Slf4j
public class NotLocalCondition implements Condition {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        boolean matched = !EnvUtil.isLocal(conditionContext.getEnvironment());
        log.info("NotLocalCondition match result: {}, env:{}", matched, EnvUtil.getEnv());
        return matched;
    }
}
