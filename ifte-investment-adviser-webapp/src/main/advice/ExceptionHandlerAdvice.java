package com.foundersc.ifte.invest.adviser.web.advice;

import com.foundersc.ifte.invest.adviser.dubbo.api.exception.BusinessException;
import com.foundersc.ifte.invest.adviser.web.exception.BizErrorCodeEnum;
import com.foundersc.ifte.invest.adviser.web.exception.BizException;
import com.foundersc.ifte.invest.adviser.web.model.Response;
import com.foundersc.ifte.invest.adviser.web.enums.AlarmErrorEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.RpcException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.nio.file.AccessDeniedException;

/**
 * 统一异常处理
 */
@RestControllerAdvice
@Slf4j
public class ExceptionHandlerAdvice {

    @ExceptionHandler({AccessDeniedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Response badRequestException(AccessDeniedException exception) {
        log.warn("forbidden request", exception);
        return Response.failed(HttpStatus.FORBIDDEN.value(), "禁止请求");
    }

    @ExceptionHandler({MissingServletRequestParameterException.class, HttpMessageNotReadableException.class,
            UnsatisfiedServletRequestParameterException.class, MethodArgumentTypeMismatchException.class,
            MethodArgumentNotValidException.class, IllegalArgumentException.class,
            HttpRequestMethodNotSupportedException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response badRequestException(Exception exception) {
        log.warn("bad request", exception);
        return Response.failed(HttpStatus.BAD_REQUEST.value(), "请求错误");
    }

    @ExceptionHandler(BizException.class)
    @ResponseStatus(HttpStatus.OK)
    public Response bizException(BizException e) {
        // 业务异常时，返回200和状态码
        log.warn("业务异常: {}", AlarmErrorEnum.getAlarmInfo(e), e);
        return Response.failed(getCode(e.getCode()), e.getMsg());
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public Response businessException(BusinessException e) {
        // dubbo服务中的异常
        log.warn("dubbo服务业务异常: {}", AlarmErrorEnum.getAlarmInfo(e), e);
        return Response.failed(getCode(e.getCode()), e.getMessage());
    }

    private int getCode(String errorCode) {
        int intErrorCode;
        try {
            intErrorCode = Integer.parseInt(errorCode);
        } catch (NumberFormatException ex) {
            intErrorCode = 500;
        }
        return intErrorCode;
    }

    @ExceptionHandler(RpcException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response exception(RpcException e) {
        log.error("RpcException: {}", AlarmErrorEnum.getAlarmInfo(BizErrorCodeEnum.RPC_ERROR), e);
        return Response.failed(HttpStatus.INTERNAL_SERVER_ERROR.value(), "远程调用异常");
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response exception(Exception e) {
        log.error("服务内部异常: {}", AlarmErrorEnum.getAlarmInfo(BizErrorCodeEnum.UNKNOWN), e);
        return Response.failed(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务内部异常");
    }
}
