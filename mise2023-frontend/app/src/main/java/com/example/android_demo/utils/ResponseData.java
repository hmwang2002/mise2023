package com.example.android_demo.utils;

import java.util.Map;

public class ResponseData<T> {
    private String code;
    private String message;
    private T data;

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
