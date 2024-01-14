package com.example.android_demo;

import android.app.Application;
import android.content.res.Configuration;

import androidx.annotation.NonNull;

import java.util.HashMap;

/**
 * @author SummCoder
 * @date 2023/11/25 16:52
 */
public class MyApplication extends Application {

    private static MyApplication mApp;
    // 声明一个公共的信息映射对象，当作全局变量使用
    public HashMap<String, String> infoMap = new HashMap<>();

    public static MyApplication getInstance(){
        return mApp;
    }

    // 启动时调用
    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
    }

    // 在App终止时调用
    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    // 在配置改变时调用，例如从竖屏变为横屏
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
