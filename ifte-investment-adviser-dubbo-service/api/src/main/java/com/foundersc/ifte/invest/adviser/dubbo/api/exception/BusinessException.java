package com.foundersc.ifte.invest.adviser.dubbo.api.exception;

import com.foundersc.ifte.invest.adviser.dubbo.api.enums.ErrorCodeEnum;
import lombok.Data;

@Data
public class BusinessException extends RuntimeException {

    /**
     * 错误编码，后期扩展
     */
    protected String code;

    /**
     * 错误描述
     */
    protected String message;


    public BusinessException(ErrorCodeEnum errorCodeEnum, String message, Throwable cause) {
        super(message, cause);
        this.code = errorCodeEnum != null ? errorCodeEnum.getCode() : ErrorCodeEnum.UNKNOWN.getCode();
        this.message = message;
        if ((message == null || message.length() == 0) && errorCodeEnum != null) {
            this.message = errorCodeEnum.getDesc();
        }
    }

    public BusinessException(String message, Throwable cause) {
        this(ErrorCodeEnum.UNKNOWN, message, cause);
    }

    public BusinessException(ErrorCodeEnum errorCodeEnum, String message) {
        this(errorCodeEnum, message, null);
    }

    public BusinessException(ErrorCodeEnum errorCodeEnum, Throwable cause) {
        this(errorCodeEnum, null, cause);
    }

    public BusinessException(ErrorCodeEnum errorCodeEnum) {
        this(errorCodeEnum, null, null);
    }

    public BusinessException(String message) {
        this(message, null);
    }

}
