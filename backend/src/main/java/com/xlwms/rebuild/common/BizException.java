package com.xlwms.rebuild.common;

/** 业务校验失败。由 {@link GlobalExceptionHandler} 转成 {code:500, msg}（真实系统的错误码未知，500 为本项目占位）。 */
public class BizException extends RuntimeException {
    public BizException(String message) {
        super(message);
    }
}
