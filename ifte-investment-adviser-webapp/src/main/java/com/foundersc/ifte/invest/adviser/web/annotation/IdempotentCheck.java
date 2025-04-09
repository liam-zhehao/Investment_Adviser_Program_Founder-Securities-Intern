package com.foundersc.ifte.invest.adviser.web.annotation;

import com.foundersc.ifte.invest.adviser.web.enums.IdemCheckKeyEnum;
import com.foundersc.ifte.invest.adviser.web.enums.IdemCheckTypeEnum;

import java.lang.annotation.*;

/**
 * 幂等性校验注解
 *
 * @author wangfuwei
 * @date 2022/11/4
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface IdempotentCheck {
    /**
     * 业务类型
     *
     * @return
     */
    IdemCheckTypeEnum type();

    /**
     * 幂等性唯一key
     *
     * @return
     */
    IdemCheckKeyEnum checkKey();
}
