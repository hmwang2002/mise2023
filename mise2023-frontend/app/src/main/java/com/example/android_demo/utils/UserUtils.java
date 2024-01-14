package com.example.android_demo.utils;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;

import com.example.android_demo.Constants.constant;
import com.example.android_demo.MyApplication;
import com.example.android_demo.bean.Message;
import com.example.android_demo.MyApplication;
import com.google.gson.Gson;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UserUtils {
    private static boolean isLoggedIn = false;
    public static String userId;
    private static String message;

    public static String token;
    public static MyApplication application;

    public static boolean isLoggedIn() {
        return isLoggedIn;
    }

    public static Message login(String username, String password) {
        if(username.equals("admin") && password.equals("admin")){
            isLoggedIn = true;
            return new Message(true, "登录成功");
        }
        // 进行登录验证的逻辑，例如与服务器通信验证用户名和密码
        Thread thread = new Thread(() -> {
            try {
                // 创建HTTP客户端
                OkHttpClient client = new OkHttpClient()
                        .newBuilder()
                        .connectTimeout(60000, TimeUnit.MILLISECONDS)
                        .readTimeout(60000, TimeUnit.MILLISECONDS)
                        .build();
                // 创建HTTP请求

                Request request = new Request.Builder()
                        .url(constant.IP_ADDRESS + "/user/login?userName=" + username + "&password=" + password)
                        .build();
                // 执行发送的指令，获得返回结果
                Response response = client.newCall(request).execute();
                if (response.code() == 200) {
                    String reData=response.body().string();
                    System.out.println("redata"+reData);
                    Gson gson = new Gson();
                    ResponseData<Map<String, String>> rdata= gson.fromJson(reData, ResponseData.class);
                    System.out.println("rdata getData是："+rdata.getData());
                    if(rdata.getCode().equals("999")){
                        isLoggedIn = false;
                        message = rdata.getMessage();
                    } else {
                        token = rdata.getData().get("tokenValue");
                        application = MyApplication.getInstance();
                        application.infoMap.put("satoken", token);
                        application.infoMap.put("loginId", rdata.getData().get("loginId"));
                        userId=rdata.getData().get("loginId");
                        message = rdata.getMessage();
                        isLoggedIn = true;
                    }
                } else {
                    isLoggedIn = false;
                    message = "登录失败！请检查网络状况";
                }
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
                isLoggedIn = false;
                message = "登录失败！请检查网络状况";
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //初始化数据
        return new Message(isLoggedIn, message);
    }

    public static void logout() {
        Thread thread = new Thread(() -> {
            try {
                // 创建HTTP客户端
                OkHttpClient client = new OkHttpClient()
                        .newBuilder()
                        .connectTimeout(60000, TimeUnit.MILLISECONDS)
                        .readTimeout(60000, TimeUnit.MILLISECONDS)
                        .build();
                // 创建HTTP请求
                Request request = new Request.Builder()
                        .url(constant.IP_ADDRESS + "/user/logout")
                        .addHeader("satoken", Objects.requireNonNull(application.infoMap.get("satoken")))
                        .build();
                // 执行发送的指令，获得返回结果
                Response response = client.newCall(request).execute();
                String reData=response.body().string();
                Gson gson = new Gson();
                ResponseData rdata= gson.fromJson(reData, ResponseData.class);
                if (rdata.getCode().equals("200")) {
                    isLoggedIn = false;
                    application.infoMap.remove("satoken");
                    application.infoMap.remove("loginId");
                }
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

