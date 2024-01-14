package com.example.android_demo.home;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.android_demo.Constants.constant;
import com.example.android_demo.MyApplication;
import com.example.android_demo.bean.CommunityVO;
import com.example.android_demo.bean.Message;
import com.example.android_demo.bean.MessageBean;
import com.example.android_demo.bean.User;
import com.example.android_demo.utils.PostData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * @author SummCoder
 * @date 2023/12/16 21:54
 */
public class MessageViewModel extends ViewModel {
    private MutableLiveData<List<MessageBean.Application>> applicationsLiveData = new MutableLiveData<>();
    private CommunityVO communityVO;

    private ObjectMapper objectMapper = new ObjectMapper();

    public LiveData<List<MessageBean.Application>> getApplicationsLiveData() {
        return applicationsLiveData;
    }

    public void fetchData() {
        Thread thread = new Thread(() -> {
            try {
                // 创建HTTP客户端
                OkHttpClient client = new OkHttpClient()
                        .newBuilder()
                        .connectTimeout(60000, TimeUnit.MILLISECONDS)
                        .readTimeout(60000, TimeUnit.MILLISECONDS)
                        .build();
                // 创建HTTP请求
                MyApplication application = MyApplication.getInstance();
                Request request = new Request.Builder()
                        .url(constant.IP_ADDRESS + "/user/getApplicationByAdminId?adminID=" + Objects.requireNonNull(application.infoMap.get("loginId")))
                        .addHeader("satoken", Objects.requireNonNull(application.infoMap.get("satoken")))
                        .build();
                // 执行发送的指令，获得返回结果
                try {
                    // 执行发送的指令，获得返回结果
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseData);
                        System.out.println(jsonResponse);
                        JSONObject data = jsonResponse.getJSONObject("data");
                        List<MessageBean.Application> list = new ArrayList<>();
                        Iterator<String> keys = data.keys();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            JSONArray value = data.getJSONArray(key);
                            String regex = "CommunityVO\\(communityID=(\\d+), isPublic=(\\w+), createTime=([^,]+), name=([^\\)]+)\\)";
                            Pattern pattern = Pattern.compile(regex);
                            Matcher matcher = pattern.matcher(key);
                            if (matcher.matches()) {
                                long communityID = Long.parseLong(matcher.group(1));
                                boolean isPublic = Boolean.parseBoolean(matcher.group(2));
                                String createTime = matcher.group(3).trim();
                                String name = matcher.group(4);
                                communityVO = new CommunityVO(communityID, isPublic, createTime, name);
                            } else {
                                System.out.println("Invalid input format");
                            }
                            for (int i = 0; i < value.length(); i++) {
                                MessageBean.Application map = new MessageBean.Application();
                                map.setCommunityVO(communityVO);
                                JSONObject userObject = value.getJSONObject(i);
                                User user = objectMapper.readValue(userObject.toString(), User.class);
                                map.setUser(user);
                                list.add(map);
                            }
                        }
                        System.out.println(list);
                        applicationsLiveData.postValue(list);
                    } else {
                        // 处理请求失败的情况
                        System.out.println("error");
                    }
                } catch (Exception e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                    applicationsLiveData.postValue(new ArrayList<>()); // 设置空数据以触发观察者
                }
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
                applicationsLiveData.postValue(new ArrayList<>()); // 设置空数据以触发观察者
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
