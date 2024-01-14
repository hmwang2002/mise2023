package com.example.android_demo.home;

import static com.example.android_demo.utils.UserUtils.application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.android_demo.Constants.constant;
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
 * @date 2023/12/16 16:51
 */
public class PostViewModel extends ViewModel {
    private MutableLiveData<List<PostData.Post>> postsLiveData = new MutableLiveData<>();

    public LiveData<List<PostData.Post>> getPostsLiveData() {
        return postsLiveData;
    }

    public void fetchData() {
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
                        .url(constant.IP_ADDRESS + "/user/posts")
                        .addHeader("satoken", Objects.requireNonNull(application.infoMap.get("satoken")))
                        .build();
                // 执行发送的指令，获得返回结果
                Response response = client.newCall(request).execute();
                String reData = response.body().string();
                System.out.println("res" + reData);
                Gson gson = new Gson();
                PostData rdata = gson.fromJson(reData, PostData.class);
                if (rdata.getCode().equals("200")) {
                    System.out.println("获取用户发布过的帖子成功");
                    postsLiveData.postValue(rdata.getData());
                } else {
                    System.out.println("获取用户发布过的帖子失败");
                    postsLiveData.postValue(new ArrayList<>()); // 设置空数据以触发观察者
                }
            } catch (Exception e) {
                // 处理异常，例如记录日志
                e.printStackTrace();
                postsLiveData.postValue(new ArrayList<>()); // 设置空数据以触发观察者
            }
        }).start();
    }
}
