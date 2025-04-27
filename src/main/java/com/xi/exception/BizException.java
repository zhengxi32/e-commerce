package com.xi.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BizException extends RuntimeException {
    // 异常码
    private String errorCode;
    // 错误信息
    private String errorMessage;

    public BizException(BaseException baseException) {
        this.errorCode = baseException.getErrorCode();
        this.errorMessage = baseException.getErrorMessage();
    }
}
