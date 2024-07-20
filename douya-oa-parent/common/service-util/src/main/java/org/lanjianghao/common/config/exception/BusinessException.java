package org.lanjianghao.common.config.exception;

import lombok.Data;
import lombok.ToString;
import org.lanjianghao.common.result.ResultCodeEnum;

@ToString
@Data
public class BusinessException extends RuntimeException {
    private final Integer code;
    private final String msg;

    public BusinessException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public BusinessException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
        this.msg = resultCodeEnum.getMessage();
    }
}
