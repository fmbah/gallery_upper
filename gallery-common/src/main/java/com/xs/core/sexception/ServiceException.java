package com.xs.core.sexception;

/**
 * @Auther: Fmbah
 * @Date: 18-10-10 下午4:26
 * @Description: 业务层异常
 */
public class ServiceException extends RuntimeException {
    public ServiceException() {
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
