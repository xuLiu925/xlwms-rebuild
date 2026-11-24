package com.xlwms.rebuild.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** 业务异常统一包成信封返回，HTTP 状态仍为 200，前端按 code 判断。 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BizException.class)
    public R<Void> biz(BizException e) {
        return R.fail(500, e.getMessage());
    }
}
