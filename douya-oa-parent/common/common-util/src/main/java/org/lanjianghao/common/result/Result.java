package org.lanjianghao.common.result;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    private Result() {

    }

    public static <T> Result<T> build(T body, ResultCodeEnum resultCodeEnum) {
        Result<T> result = new Result<>();
        if (body != null) {
            result.setData(body);
        }
        result.setCode(resultCodeEnum.getCode());
        result.setMessage(resultCodeEnum.getMessage());
        return result;
    }

    public static<T> Result<T> ok() {
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    public static<T> Result<T> ok(T data) {
        return Result.build(data, ResultCodeEnum.SUCCESS);
    }

    public static<T> Result<T> fail() {
        return Result.build(null, ResultCodeEnum.SUCCESS);
    }

    public static<T> Result<T> fail(T data) {
        return Result.build(data, ResultCodeEnum.SUCCESS);
    }

    public Result<T> message(String msg){
        this.setMessage(msg);
        return this;
    }

    public Result<T> code(Integer code){
        this.setCode(code);
        return this;
    }
}
