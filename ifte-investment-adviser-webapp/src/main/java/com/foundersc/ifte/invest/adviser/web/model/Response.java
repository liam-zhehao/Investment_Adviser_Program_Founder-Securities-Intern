package com.foundersc.ifte.invest.adviser.web.model;

import com.foundersc.ifte.invest.adviser.web.exception.BizException;
import com.foundersc.ifte.invest.adviser.web.util.MdcUtil;
import lombok.Data;

import java.io.Serializable;

/**
 * web响应包装
 */
@Data
public class Response<T> implements Serializable {
    private static final long serialVersionUID = -4417715614021482064L;

    /**
     * 返回码 ""success""表示成功， "error"表示失败
     */
    private String status;

    /**
     * 失败提示
     */
    private String message;

    /**
     * 错误编码
     */
    private Integer code;

    /**
     * 数据
     */
    private T info;

    /**
     * 接口耗时
     */
    private long cost;
    /**
     * 日志traceId，可以根据这个跟踪整个链路
     */
    private String traceId;

    private static final String SUCCESS = "success";
    private static final String ERROR = "error";

    public Response() {
    }

    public Response(String status) {
        this(status, null);
    }

    public Response(String status, String message) {
        this(status, message, 200);
    }

    public Response(String status, String message, Integer code) {
        this.status = status;
        this.message = message;
        this.code = code;
        this.traceId = MdcUtil.getTraceId();
    }

    public static Response ok() {
        Response response = new Response(SUCCESS);
        return response;
    }

    public static Response ok(Object data) {
        Response response = new Response(SUCCESS);
        response.setInfo(data);
        return response;
    }

    public static Response failed(String errMsg) {
        Response response = new Response(ERROR, errMsg);
        return response;
    }

    public static Response failed(Integer errorCode, String errMsg) {
        Response response = new Response(ERROR, errMsg, errorCode);
        return response;
    }

    public static Response failed(BizException error) {
        int intErrorCode;
        try {
            intErrorCode = Integer.parseInt(error.getCode());
        } catch (NumberFormatException e) {
            intErrorCode = 500;
        }
        return new Response(ERROR, error.getMsg(), intErrorCode);
    }
}
