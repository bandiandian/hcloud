package com.haier.interconn.hcloud.mvc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Author: bandd
 * Mailto:bandd@haier.com
 * On: 2017-08-08  15:20
 */
public class Error {


    private String code;

    private String message;

    private String requestUri;

    @JsonCreator
    public Error(@JsonProperty("code") String code,
                 @JsonProperty("requestUri") String requestUri,
                 @JsonProperty(value = "message", defaultValue = "") String message) {
        this.code = code;
        this.requestUri = requestUri;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getRequestUri() {
        return requestUri;
    }

    @Override
    public String toString() {
        return "Error{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", requestUri='" + requestUri + '\'' +
                '}';
    }
}
