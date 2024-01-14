package com.example.android_demo.home;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.android_demo.Constants.constant;
import com.example.android_demo.MyApplication;
import com.example.android_demo.R;
import com.example.android_demo.bean.MessageBean;
import com.example.android_demo.databinding.FragmentMessageBinding;
import com.example.android_demo.utils.PostData;
import com.example.android_demo.utils.TimeUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author SummCoder
 * @date 2023/12/9 17:52
 */
public class MessageFragment extends Fragment {
    private FragmentMessageBinding binding;
    private ListView lv_message;

    private List<Map<String, Object>> list;

    private MessageViewModel messageViewModel;

    public static MyApplication application;
    private TextView tv_none_message;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMessageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        lv_message = root.findViewById(R.id.lv_message);
        tv_none_message = root.findViewById(R.id.tv_none_message);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        reload();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void reload() {
        messageViewModel = new ViewModelProvider(this).get(MessageViewModel.class);
        messageViewModel.getApplicationsLiveData().observe(getViewLifecycleOwner(), this::updateUI);
        messageViewModel.fetchData();
    }

    private void updateUI(List<MessageBean.Application> posts) {
        if(posts == null || posts.isEmpty()){
            tv_none_message.setVisibility(View.VISIBLE);
            lv_message.setVisibility(View.GONE);
            return;
        }else {
            tv_none_message.setVisibility(View.GONE);
            lv_message.setVisibility(View.VISIBLE);
        }
        list = new ArrayList<>();
        for (MessageBean.Application post : posts) {
            Map<String, Object> map = new HashMap<>();
            map.put("communityName", "申请加入社区：" + post.getCommunityVO().name);
            map.put("username", "用户：" + post.getUser().userName);
            map.put("avatar", post.getUser().photo);
            list.add(map);
        }
        String[] from = {"communityName", "username", "avatar"};
        int[] to = {R.id.tv_community_message, R.id.tv_name_message, R.id.iv_avatar_message};

        SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(), list, R.layout.message_item, from, to) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                Button bt_agree = view.findViewById(R.id.bt_agree);
                Button bt_disagree = view.findViewById(R.id.bt_disagree);
                ImageView iv_avatar_message = view.findViewById(R.id.iv_avatar_message);
                String avatarUrl = (String) list.get(position).get("avatar");
                // 使用Glide加载头像图片
                Glide.with(getActivity())
                        .load(avatarUrl)
                        .into(iv_avatar_message);

                bt_agree.setOnClickListener(v -> {
                    MessageBean.Application application = posts.get(position);
                    agree(application.getUser().userId, String.valueOf(application.getCommunityVO().communityID));
                });

                bt_disagree.setOnClickListener(v ->{
                    // 获取被点击的item的数据
                    MessageBean.Application application = posts.get(position);
                    refuse(application.getUser().userId, String.valueOf(application.getCommunityVO().communityID));
                });
                return view;
            }
        };
        lv_message.setAdapter(simpleAdapter);
    }

    private void agree(String userID, String communityID) {
        application = MyApplication.getInstance();
        // 调用后端接口执行同意操作
        String url = constant.IP_ADDRESS + "/application/accept?userID=" + userID + "&communityID=" + communityID + "&handlerID=" + Objects.requireNonNull(application.infoMap.get("loginId"));
        Thread thread = new Thread(() -> {
            try {
                // 创建 HTTP 客户端
                OkHttpClient client = new OkHttpClient()
                        .newBuilder()
                        .connectTimeout(60000, TimeUnit.MILLISECONDS)
                        .readTimeout(60000, TimeUnit.MILLISECONDS)
                        .build();

                // 创建 HTTP 请求
                Request request = new Request.Builder()
                        .url(url)
                        .build();

                // 执行发送的指令，获得返回结果
                Response response = client.newCall(request).execute();
                // 输出响应的内容
                System.out.println(response.body().string());
                // 刷新页面内容
                if (response.isSuccessful()) {
                    // 重新获取帖子数据
                    messageViewModel.fetchData();
                }else {

                }
            } catch (Exception e) {
                // 处理异常，例如记录日志
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void refuse(String userID, String communityID) {
        application = MyApplication.getInstance();
        // 调用后端接口执行同意操作
        String url = constant.IP_ADDRESS + "/application/refuse?userID=" + userID + "&communityID=" + communityID + "&handlerID=" + Objects.requireNonNull(application.infoMap.get("loginId"));
        Thread thread = new Thread(() -> {
            try {
                // 创建 HTTP 客户端
                OkHttpClient client = new OkHttpClient()
                        .newBuilder()
                        .connectTimeout(60000, TimeUnit.MILLISECONDS)
                        .readTimeout(60000, TimeUnit.MILLISECONDS)
                        .build();

                // 创建 HTTP 请求
                Request request = new Request.Builder()
                        .url(url)
                        .build();

                // 执行发送的指令，获得返回结果
                Response response = client.newCall(request).execute();
                // 输出响应的内容
                System.out.println(response.body().string());
                // 刷新页面内容
                if (response.isSuccessful()) {
                    // 重新获取帖子数据
                    messageViewModel.fetchData();
                }else {

                }
            } catch (Exception e) {
                // 处理异常，例如记录日志
                e.printStackTrace();
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
