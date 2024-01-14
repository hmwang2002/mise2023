package com.example.android_demo.bean;

import java.util.List;

/**
 * @author SummCoder
 * @date 2023/12/26 23:32
 */
public class MemberData {
    private String code;
    private String message;
    private List<User> data;

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public List<User> getData() {
        return data;
    }
}
