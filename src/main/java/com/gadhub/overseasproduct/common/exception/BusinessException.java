package com.gadhub.overseasproduct.common.exception;

import com.gadhub.overseasproduct.common.constant.ErrorCode;

public class BusinessException extends RuntimeException{
    private Integer code;

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode err) {
        super(err.getMessage());
        this.code = err.getCode();
    }

    public BusinessException(String message) {
        super(message);
        this.code = 500;  // 默认错误码
    }



    public Integer getCode() {
        return code;
    }
}
