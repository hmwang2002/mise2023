package com.example.android_demo.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author SummCoder
 * @date 2023/12/26 23:24
 */
public class OwnerData {
    private String code;
    private String message;
    private User data;

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public User getData() {
        return data;
    }
}
