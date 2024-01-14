package com.example.android_demo.home;

import static com.example.android_demo.utils.UserUtils.application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.android_demo.Constants.constant;
import com.example.android_demo.bean.CommunityExpandData;
import com.example.android_demo.bean.CommunityVO;
import com.example.android_demo.utils.PostData;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author SummCoder
 * @date 2023/12/17 12:01
 */
public class JoinedViewModel extends ViewModel {
    private MutableLiveData<List<CommunityVO>> communityCreatedLiveData = new MutableLiveData<>();

    private MutableLiveData<List<CommunityVO>> communityManagedLiveData = new MutableLiveData<>();

    private MutableLiveData<List<CommunityVO>> communityJoinedLiveData = new MutableLiveData<>();

    public LiveData<List<CommunityVO>> getCommunityCreatedLiveData() {
        return communityCreatedLiveData;
    }

    public LiveData<List<CommunityVO>> getCommunityManagedLiveData() {
        return communityManagedLiveData;
    }

    public LiveData<List<CommunityVO>> getCommunityJoinedLiveData() {
        return communityJoinedLiveData;
    }

    public void fetchCreatedData() {
        new Thread(() -> {
            try {
                // 创建HTTP客户端
                OkHttpClient client = new OkHttpClient()
                        .newBuilder()
                        .connectTimeout(60000, TimeUnit.MILLISECONDS)
                        .readTimeout(60000, TimeUnit.MILLISECONDS)
                        .build();
                // 创建HTTP请求
                Request request = new Request.Builder()
                        .url(constant.IP_ADDRESS + "/community/getCreatedCommunity?userID=" + Objects.requireNonNull(application.infoMap.get("loginId")))
                        .addHeader("satoken", Objects.requireNonNull(application.infoMap.get("satoken")))
                        .build();
                // 执行发送的指令，获得返回结果
                Response response = client.newCall(request).execute();
                String reData = response.body().string();
                System.out.println("res" + reData);
                Gson gson = new Gson();
                CommunityExpandData rdata = gson.fromJson(reData, CommunityExpandData.class);
                if (rdata.getCode().equals("200")) {
                    System.out.println("获取用户创建的社区成功");
                    communityCreatedLiveData.postValue(rdata.getData());
                } else {
                    System.out.println("获取用户创建的社区失败");
                    communityCreatedLiveData.postValue(new ArrayList<>()); // 设置空数据以触发观察者
                }
            } catch (Exception e) {
                // 处理异常，例如记录日志
                e.printStackTrace();
                communityCreatedLiveData.postValue(new ArrayList<>()); // 设置空数据以触发观察者
            }
        }).start();
    }

    public void fetchManagedData() {
        new Thread(() -> {
            try {
                // 创建HTTP客户端
                OkHttpClient client = new OkHttpClient()
                        .newBuilder()
                        .connectTimeout(60000, TimeUnit.MILLISECONDS)
                        .readTimeout(60000, TimeUnit.MILLISECONDS)
                        .build();
                // 创建HTTP请求
                Request request = new Request.Builder()
                        .url(constant.IP_ADDRESS + "/community/getManagedCommunity?userID=" + Objects.requireNonNull(application.infoMap.get("loginId")))
                        .addHeader("satoken", Objects.requireNonNull(application.infoMap.get("satoken")))
                        .build();
                // 执行发送的指令，获得返回结果
                Response response = client.newCall(request).execute();
                String reData = response.body().string();
                System.out.println("res" + reData);
                Gson gson = new Gson();
                CommunityExpandData rdata = gson.fromJson(reData, CommunityExpandData.class);
                if (rdata.getCode().equals("200")) {
                    System.out.println("获取用户管理的社区成功");
                    communityManagedLiveData.postValue(rdata.getData());
                } else {
                    System.out.println("获取用户管理的社区失败");
                    communityManagedLiveData.postValue(new ArrayList<>()); // 设置空数据以触发观察者
                }
            } catch (Exception e) {
                // 处理异常，例如记录日志
                e.printStackTrace();
                communityManagedLiveData.postValue(new ArrayList<>()); // 设置空数据以触发观察者
            }
        }).start();
    }

    public void fetchJoinedData() {
        new Thread(() -> {
            try {
                // 创建HTTP客户端
                OkHttpClient client = new OkHttpClient()
                        .newBuilder()
                        .connectTimeout(60000, TimeUnit.MILLISECONDS)
                        .readTimeout(60000, TimeUnit.MILLISECONDS)
                        .build();
                // 创建HTTP请求
                Request request = new Request.Builder()
                        .url(constant.IP_ADDRESS + "/community/getJoinedCommunity?userID=" + Objects.requireNonNull(application.infoMap.get("loginId")))
                        .addHeader("satoken", Objects.requireNonNull(application.infoMap.get("satoken")))
                        .build();
                // 执行发送的指令，获得返回结果
                Response response = client.newCall(request).execute();
                String reData = response.body().string();
                System.out.println("res" + reData);
                Gson gson = new Gson();
                CommunityExpandData rdata = gson.fromJson(reData, CommunityExpandData.class);
                if (rdata.getCode().equals("200")) {
                    System.out.println("获取用户加入的社区成功");
                    communityJoinedLiveData.postValue(rdata.getData());
                } else {
                    System.out.println("获取用户加入的社区失败");
                    communityJoinedLiveData.postValue(new ArrayList<>()); // 设置空数据以触发观察者
                }
            } catch (Exception e) {
                // 处理异常，例如记录日志
                e.printStackTrace();
                communityJoinedLiveData.postValue(new ArrayList<>()); // 设置空数据以触发观察者
            }
        }).start();
    }


}
