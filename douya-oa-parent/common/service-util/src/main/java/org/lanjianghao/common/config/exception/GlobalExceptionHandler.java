package org.lanjianghao.common.config.exception;

import org.lanjianghao.common.result.Result;
import org.lanjianghao.common.result.ResultCodeEnum;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result<?> error(Exception e) {
        e.printStackTrace();
        return Result.fail().message("系统内部错误");
    }

    @ExceptionHandler(ArithmeticException.class)
    public Result<?> error(ArithmeticException e) {
        e.printStackTrace();
        return Result.fail().message("系统内部错误（运算异常）");
    }

    @ExceptionHandler(BusinessException.class)
    public Result<?> error(BusinessException e) {
        return Result.fail().code(e.getCode()).message(e.getMsg());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Result<?> error(AccessDeniedException e) {
        return Result.build(null, ResultCodeEnum.PERMISSION);
    }
}
