package com.haier.interconn.hcloud.exception;
import com.haier.interconn.hcloud.mvc.Error;
/**
 * hystrix会忽略这个异常, 不会触发熔断
 */
public class RemoteCallException extends AppBusinessException {

    private Error originError;

    public RemoteCallException(Error error, int httpStatus) {
        super(error.getCode(), httpStatus, "调用远程服务异常, cause: " + error.getMessage());
        this.originError = error;
    }

    public Error getOriginError() {
        return originError;
    }
}
