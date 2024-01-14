package com.example.android_demo.utils;

import com.google.gson.Gson;

public class ConvertType {
    public static String beanToJson(Object bean) {
        Gson gson = new Gson();
        //System.out.println(jsonStr);
        return gson.toJson(bean);
    }
}
