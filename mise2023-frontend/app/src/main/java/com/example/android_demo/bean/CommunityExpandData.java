package com.example.android_demo.bean;

import com.example.android_demo.utils.PostData;

import java.io.Serializable;
import java.util.List;

/**
 * @author SummCoder
 * @date 2023/12/17 23:12
 */
public class CommunityExpandData {
    private String code;
    private String message;
    private List<CommunityVO> data;

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public List<CommunityVO> getData() {
        return data;
    }

}
