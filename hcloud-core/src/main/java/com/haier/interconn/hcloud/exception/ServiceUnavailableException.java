package com.haier.interconn.hcloud.exception;


import com.haier.interconn.hcloud.mvc.CommonErrorCode;
import com.haier.interconn.hcloud.mvc.ErrorCode;

/**
 * Author: bandd
 * Mailto:bandd@haier.com
 * On: 2017-08-08  15:24
 */
public class ServiceUnavailableException extends AppBusinessException {

    private static final ErrorCode ERROR_CODE = CommonErrorCode.SERVICE_UNAVAILABLE;

    public ServiceUnavailableException(String message) {
        super(ERROR_CODE.getCode(), ERROR_CODE.getStatus(), " 远程服务不可用: " + message);
    }

}
