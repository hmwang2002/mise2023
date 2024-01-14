package com.example.android_demo.community;

import static com.example.android_demo.utils.UserUtils.application;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.android_demo.Constants.constant;
import com.example.android_demo.MyApplication;
import com.example.android_demo.R;
import com.example.android_demo.bean.MemberData;
import com.example.android_demo.bean.OwnerData;
import com.example.android_demo.bean.User;
import com.example.android_demo.utils.ResponseData;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author SummCoder
 * @date 2023/12/26 22:10
 */
public class MemberActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView iv_memberBack;
    private long communityID;

    private User owner;

    private List<User> managers;

    private List<User> members;

    private List<Map<String, Object>> memberList;
    private ListView lv_members;
    private int myRole = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);
        Intent intent = getIntent();
        communityID = intent.getLongExtra("communityId", 1L);
        iv_memberBack = findViewById(R.id.iv_memberBack);
        iv_memberBack.setOnClickListener(this);
        lv_members = findViewById(R.id.lv_members);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchOwnerData();
        fetchManagerData();
        fetchMemberData();
        updateUI();
    }

    private void updateUI() {
//        List<User> users = new ArrayList<>();
//        users.add(owner);
//        if(managers != null){
//            users.addAll(managers);
//        }
//        if(members != null){
//            users.addAll(members);
//        }
        memberList = new ArrayList<>();
//        for (User user : users) {
//            Map<String, Object> map = new HashMap<>();
//            map.put("avatar", user.photo);
//            map.put("username", user.userName);
//            memberList.add(map);
//        }

        Map<String, Object> map0 = new HashMap<>();
        map0.put("userId", owner.userId);
        map0.put("role", 0);
        map0.put("avatar", owner.photo);
        map0.put("username", owner.userName);
        memberList.add(map0);

        for (User manager : managers){
            Map<String, Object> map = new HashMap<>();
            map.put("userId", manager.userId);
            map.put("role", 1);
            map.put("avatar", manager.photo);
            map.put("username", manager.userName);
            memberList.add(map);
        }

        for (User member : members){
            Map<String, Object> map = new HashMap<>();
            map.put("userId", member.userId);
            map.put("role", 2);
            map.put("avatar", member.photo);
            map.put("username", member.userName);
            memberList.add(map);
        }

        String[] from = {"avatar", "username"};
        int[] to = {R.id.iv_member_avatar, R.id.tv_member_name};

        SimpleAdapter simpleAdapter = new SimpleAdapter(this, memberList, R.layout.item_member, from, to) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ImageView iv_cover = view.findViewById(R.id.iv_member_avatar);
                String avatarUrl = (String) memberList.get(position).get("avatar");
                // 使用Glide加载图片
                Glide.with(MemberActivity.this)
                        .load(avatarUrl)
                        .into(iv_cover);
                CardView cv_role = view.findViewById(R.id.cv_role);
                TextView tv_role = view.findViewById(R.id.tv_role);
                int role = (int) memberList.get(position).get("role");
                if(role == 0){
                    int color = ContextCompat.getColor(MemberActivity.this, R.color.orange);
                    cv_role.setCardBackgroundColor(color);
                    tv_role.setText("群主");
                } else if (role == 1) {
                    int color = ContextCompat.getColor(MemberActivity.this, R.color.teal_200);
                    cv_role.setCardBackgroundColor(color);
                    tv_role.setText("管理员");
                }else {
                    int color = ContextCompat.getColor(MemberActivity.this, R.color.gray);
                    cv_role.setCardBackgroundColor(color);
                    tv_role.setText("群成员");
                }
                String userId = (String) memberList.get(position).get("userId");
                if(Objects.equals(userId, MyApplication.getInstance().infoMap.get("loginId"))){
                    TextView tv_me = view.findViewById(R.id.tv_me);
                    tv_me.setVisibility(View.VISIBLE);
                    myRole = role;
                }
                System.out.println(myRole);

                if(myRole == 0 && role != 0){
                    // 为每个item设置点击事件
                    view.setOnLongClickListener(v -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MemberActivity.this);
                        builder.setTitle("操作选择");

                        String[] options;
                        if (role == 1) {
                            options = new String[]{"删除用户", "取消管理员身份"};
                        } else {
                            options = new String[]{"删除用户", "设为管理员"};
                        }
                        builder.setSingleChoiceItems(options, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        deleteMember(Long.parseLong((String) memberList.get(position).get("userId")));
                                        memberList.remove(position);
                                        notifyDataSetChanged(); // 通知适配器数据已更改
                                        break;
                                    case 1:
                                        if (role == 1) {
                                            // 执行取消管理员身份操作
                                            cancelAdmin(Long.parseLong((String) memberList.get(position).get("userId")));
                                            Map<String, Object> map = new HashMap<>();
                                            map.put("userId", memberList.get(position).get("userId"));
                                            map.put("role", 2);
                                            map.put("avatar", memberList.get(position).get("avatar"));
                                            map.put("username", memberList.get(position).get("username"));
                                            memberList.set(position, map);
                                            notifyDataSetChanged(); // 通知适配器数据已更改
                                        } else {
                                            setAdmin(Long.parseLong((String) memberList.get(position).get("userId")));
                                            Map<String, Object> map = new HashMap<>();
                                            map.put("userId", memberList.get(position).get("userId"));
                                            map.put("role", 1);
                                            map.put("avatar", memberList.get(position).get("avatar"));
                                            map.put("username", memberList.get(position).get("username"));
                                            memberList.set(position, map);
                                            notifyDataSetChanged(); // 通知适配器数据已更改
                                        }
                                        break;
                                }
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                        return true;
                    });
                } else if (myRole == 1 && role == 2) {
                    // 为每个item设置点击事件
                    view.setOnLongClickListener(v -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MemberActivity.this);
                        builder.setTitle("操作选择");

                        String[] options;
                        options = new String[]{"删除用户"};

                        builder.setSingleChoiceItems(options, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {// 执行删除用户操作
                                    deleteMember((Long) memberList.get(position).get("userId"));
                                    memberList.remove(position);
                                    notifyDataSetChanged(); // 通知适配器数据已更改
                                }
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                        return true;
                    });
                }


                return view;
            }
        };

        lv_members.setAdapter(simpleAdapter);
    }

    private void fetchOwnerData(){
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
                        .url(constant.IP_ADDRESS + "/user/getCommunityOwner?communityID=" + communityID)
                        .addHeader("satoken", Objects.requireNonNull(application.infoMap.get("satoken")))
                        .build();
                // 执行发送的指令，获得返回结果
                Response response = client.newCall(request).execute();
                String reData = response.body().string();
                System.out.println("res" + reData);
                Gson gson = new Gson();
                OwnerData rdata = gson.fromJson(reData, OwnerData.class);
                if (rdata.getCode().equals("200")) {
                    System.out.println("获取群主成功");
                    owner = rdata.getData();
                } else {
                    System.out.println("获取群主失败");
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

    private void fetchManagerData(){
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
                        .url(constant.IP_ADDRESS + "/user/getCommunityManagers?communityID=" + communityID)
                        .addHeader("satoken", Objects.requireNonNull(application.infoMap.get("satoken")))
                        .build();
                // 执行发送的指令，获得返回结果
                Response response = client.newCall(request).execute();
                String reData = response.body().string();
                System.out.println("res" + reData);
                Gson gson = new Gson();
                MemberData rdata = gson.fromJson(reData, MemberData.class);
                if (rdata.getCode().equals("200")) {
                    System.out.println("获取管理员成功");
                    managers = rdata.getData();
                } else {
                    System.out.println("获取管理员失败");
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

    private void fetchMemberData(){
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
                        .url(constant.IP_ADDRESS + "/user/getCommunityMembers?communityID=" + communityID)
                        .addHeader("satoken", Objects.requireNonNull(application.infoMap.get("satoken")))
                        .build();
                // 执行发送的指令，获得返回结果
                Response response = client.newCall(request).execute();
                String reData = response.body().string();
                System.out.println("res" + reData);
                Gson gson = new Gson();
                MemberData rdata = gson.fromJson(reData, MemberData.class);
                if (rdata.getCode().equals("200")) {
                    System.out.println("获取普通用户成功");
                    members = rdata.getData();
                } else {
                    System.out.println("获取普通用户失败");
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

    private void setAdmin(long userId){
        Thread thread = new Thread(() -> {
            try {
                // 创建HTTP客户端
                OkHttpClient client = new OkHttpClient()
                        .newBuilder()
                        .connectTimeout(60000, TimeUnit.MILLISECONDS)
                        .readTimeout(60000, TimeUnit.MILLISECONDS)
                        .build();
                // 创建HTTP请求
                System.out.println(communityID);
                System.out.println(userId);
                Request request = new Request.Builder()
                        .url(constant.IP_ADDRESS + "/community/setAdmin?communityId=" + communityID + "&userId=" + userId)
                        .addHeader("satoken", Objects.requireNonNull(application.infoMap.get("satoken")))
                        .build();
                // 执行发送的指令，获得返回结果
                Response response = client.newCall(request).execute();
                String reData = response.body().string();
                System.out.println(reData);
                Gson gson = new Gson();
                ResponseData rdata = gson.fromJson(reData, ResponseData.class);
                if (rdata.getCode().equals("200")) {
                    System.out.println("添加管理员成功");
                } else {
                    System.out.println("添加管理员失败");
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

    private void cancelAdmin(long userId){
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
                        .url(constant.IP_ADDRESS + "/community/cancelAdmin?communityId=" + communityID + "&userId=" + userId)
                        .addHeader("satoken", Objects.requireNonNull(application.infoMap.get("satoken")))
                        .build();
                // 执行发送的指令，获得返回结果
                Response response = client.newCall(request).execute();
                String reData = response.body().string();
                System.out.println(reData);
                Gson gson = new Gson();
                ResponseData rdata = gson.fromJson(reData, ResponseData.class);
                if (rdata.getCode().equals("200")) {
                    System.out.println("移除管理员成功");
                } else {
                    System.out.println("移除管理员失败");
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

    private void deleteMember(long memberID){
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
                        .url(constant.IP_ADDRESS + "/community/deleteMember?communityID=" + communityID + "&memberID=" + memberID)
                        .addHeader("satoken", Objects.requireNonNull(application.infoMap.get("satoken")))
                        .build();
                // 执行发送的指令，获得返回结果
                Response response = client.newCall(request).execute();
                String reData = response.body().string();
                System.out.println(reData);
                Gson gson = new Gson();
                ResponseData rdata = gson.fromJson(reData, ResponseData.class);
                if (rdata.getCode().equals("200")) {
                    System.out.println("删除成员成功");
                } else {
                    System.out.println("删除成员失败");
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



    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.iv_memberBack){
            finish();
        }
    }
}
