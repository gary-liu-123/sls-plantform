package com.example.esign.exception;

public class EsignException extends RuntimeException {

    private final String path;
    private final String responseBody;

    public EsignException(String path, String responseBody) {
        super("电子签接口调用失败, path=" + path + ", response=" + responseBody);
        this.path = path;
        this.responseBody = responseBody;
    }

    public EsignException(String path, int httpCode, String responseBody) {
        super("电子签接口HTTP异常, path=" + path + ", httpCode=" + httpCode + ", response=" + responseBody);
        this.path = path;
        this.responseBody = responseBody;
    }

    public String getPath() {
        return path;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
