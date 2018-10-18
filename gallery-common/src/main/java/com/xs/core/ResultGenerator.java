package com.xs.core;

import java.io.Serializable;

/**
 * 响应结果生成工具
 */
public class ResultGenerator implements Serializable {

    private static final long serialVersionUID = 100002L;

    private static final String DEFAULT_SUCCESS_MESSAGE = "SUCCESS";

    public static ResponseBean genSuccessResult() {
        return new ResponseBean()
                .setCode(ResultCode.SUCCESS)
                .setMsg(DEFAULT_SUCCESS_MESSAGE);
    }

    public static ResponseBean genSuccessResult(String message, Object data) {
        return new ResponseBean()
                .setCode(ResultCode.SUCCESS)
                .setMsg(message)
                .setData(data);
    }

    public static ResponseBean genSuccessResult(Object data) {
        return new ResponseBean()
                .setCode(ResultCode.SUCCESS)
                .setMsg(DEFAULT_SUCCESS_MESSAGE)
                .setData(data);
    }

    public static ResponseBean genFailResult(String message) {
        return new ResponseBean()
                .setCode(ResultCode.FAIL)
                .setMsg(message);
    }

    public static ResponseBean exceptionResult(String message) {
        return new ResponseBean()
                .setCode(ResultCode.INTERNAL_SERVER_ERROR)
                .setMsg(message);
    }

}
