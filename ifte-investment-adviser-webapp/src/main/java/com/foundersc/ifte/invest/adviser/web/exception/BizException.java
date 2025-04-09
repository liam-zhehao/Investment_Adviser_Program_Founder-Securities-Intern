package com.foundersc.ifte.invest.adviser.web.exception;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * 业务异常
 */
@Data
public class BizException extends RuntimeException {

    /**
     * 错误编码，后期扩展
     */
    protected String code;

    /**
     * 错误描述
     */
    protected String msg;


    public BizException(BizErrorCodeEnum bizErrorCodeEnum, String msg, Throwable cause) {
        super(msg, cause);
        this.code = bizErrorCodeEnum != null ? bizErrorCodeEnum.getCode() : BizErrorCodeEnum.UNKNOWN.getCode();
        this.msg = msg;
        if (StringUtils.isBlank(msg)) {
            if (cause != null) {
                this.msg = cause.getMessage();
            } else {
                this.msg = bizErrorCodeEnum.getDesc();
            }
        }
    }

    public BizException(String msg, Throwable cause) {
        this(BizErrorCodeEnum.UNKNOWN, msg, cause);
    }

    public BizException(BizErrorCodeEnum bizErrorCodeEnum, String msg) {
        this(bizErrorCodeEnum, msg, null);
    }

    public BizException(BizErrorCodeEnum bizErrorCodeEnum, Throwable cause) {
        this(bizErrorCodeEnum, null, cause);
    }

    public BizException(BizErrorCodeEnum bizErrorCodeEnum) {
        this(bizErrorCodeEnum, null, null);
    }

    public BizException(String msg) {
        this(msg, null);
    }
}
