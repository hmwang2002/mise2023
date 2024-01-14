package com.example.android_demo.community;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static com.example.android_demo.utils.UserUtils.application;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.android_demo.Constants.constant;
import com.example.android_demo.MyApplication;
import com.example.android_demo.R;
import com.example.android_demo.ui.square.post.PostDetailActivity;
import com.example.android_demo.utils.PostData;
import com.example.android_demo.bean.CommunityBean;
import com.example.android_demo.bean.PostBean;
import com.example.android_demo.ui.square.post.PostDetailActivity;
import com.example.android_demo.utils.PostData;
import com.example.android_demo.utils.ResponseData;
import com.example.android_demo.utils.TimeUtils;
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

public class CommunityInnerActivity extends AppCompatActivity {
    List<PostData.Post> PostList = new ArrayList<>();
    TextView tv_name;
    ImageView iv_cover;
    private ListView lv_in_community;
    long id;
    int cover;
    String name;
    Boolean isPublic;
    Boolean isJoined;
    private List<Map<String, Object>> postList;

    private static int[] coverArray = {R.drawable.cover0, R.drawable.cover1, R.drawable.cover2};
    private Button bt_join;
    private LinearLayout linear_managerMember;
    private ImageView iv_communityInnerBack;
    private TextView tv_communityInnerTitle;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        //获取数据
        Bundle bundle = intent.getBundleExtra("Message");
        assert bundle != null;

        id = Long.parseLong(Objects.requireNonNull(bundle.getString("id")));
        cover = Integer.parseInt(Objects.requireNonNull(bundle.getString("cover")));
        name = bundle.getString("name");
        isPublic = bundle.getBoolean("isPublic");
        //开始渲染
        setContentView(R.layout.activity_community_inner);
        tv_name = findViewById(R.id.tv_community_name);
        tv_name.setText(name);

        iv_cover = findViewById(R.id.iv_community_image);
        iv_cover.setImageResource(cover);
        // 验证用户是否是该社区一员
        findUserInCommunity();
        lv_in_community = findViewById(R.id.lv_in_community);
        ImageView iv_write = findViewById(R.id.iv_write);
        iv_communityInnerBack = findViewById(R.id.iv_communityInnerBack);
        iv_communityInnerBack.setOnClickListener(v -> {
            finish();
        });
        linear_managerMember = findViewById(R.id.linear_managerMember);
        // 只有社区成员才能发帖
        if(isJoined){
            iv_write.setOnClickListener(view -> {
                toAddPost();
            });
            linear_managerMember.setOnClickListener(view -> {
                Intent intent1 = new Intent(CommunityInnerActivity.this, MemberActivity.class);
                intent1.putExtra("communityId", id);
                startActivity(intent1);
            });
        }
        bt_join = findViewById(R.id.bt_join);
        if(!isJoined){
            bt_join.setOnClickListener(view -> {
                // 点击按钮进行申请
                apply(String.valueOf(id));
                bt_join.setText("已申请");
                bt_join.setBackgroundColor(Color.GRAY);
            });
        }
        tv_communityInnerTitle = findViewById(R.id.tv_communityInnerTitle);
        tv_communityInnerTitle.setText(name);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume(){
        super.onResume();
        getPosts();
        if(isJoined){
            bt_join.setText("已关注");
            bt_join.setBackgroundColor(Color.GRAY);
        }else {
            bt_join.setText("关注");
        }
    }

    public void toAddPost(){
        Intent intent=new Intent(this, AddPostActivity.class);
        Bundle bundle = new Bundle();
        //把数据保存到Bundle里
        bundle.putString("community_id",String.valueOf(id));
        //把bundle放入intent里
        intent.putExtra("Message",bundle);
        startActivity(intent);
    }
    public void apply(String communityId){
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
                        .url(constant.IP_ADDRESS + "/application/applyForCommunity?userID=" + Objects.requireNonNull(application.infoMap.get("loginId")) + "&communityID=" + communityId)
                        .build();
                client.newCall(request).execute();
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
    public void leave(String communityId){
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
                        .url(constant.IP_ADDRESS + "/community/deleteMember?communityID="+communityId+"&memberID=" + Objects.requireNonNull(application.infoMap.get("loginId")))
                        .build();
                client.newCall(request).execute();
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
    public void getPosts(){
        new Thread(() -> {
            try {
                // 创建HTTP客户端
                OkHttpClient client = new OkHttpClient()
                        .newBuilder()
                        .connectTimeout(60000, TimeUnit.MILLISECONDS)
                        .readTimeout(60000, TimeUnit.MILLISECONDS)
                        .build();
                // 创建HTTP请求
                String url = constant.IP_ADDRESS+ "/user/getAllPosts?communityId=" + id;
                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("satoken", Objects.requireNonNull(application.infoMap.get("satoken")))
                        .get()
                        .build();
                // 执行发送的指令，获得返回结果
                Response response = client.newCall(request).execute();
                String reData = response.body().string();
                Gson gson = new Gson();
                PostData rdata = gson.fromJson(reData, PostData.class);
                if (rdata.getCode().equals("200")) {
                    PostList = rdata.getData();
                    System.out.println(PostList.size());
                    updateUI(PostList);
                } else {
                    System.out.println("获取热门社区失败");
                }
            } catch (Exception e) {
                // 处理异常，例如记录日志
                e.printStackTrace();
            }
        }).start();
    }


    private void findUserInCommunity(){
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
                        .url(constant.IP_ADDRESS + "/community/getWhetherIn?userId=" + MyApplication.getInstance().infoMap.get("loginId") + "&communityId=" + id)
                        .addHeader("satoken", Objects.requireNonNull(application.infoMap.get("satoken")))
                        .build();
                // 执行发送的指令，获得返回结果
                Response response = client.newCall(request).execute();
                String reData = response.body().string();
                System.out.println("res" + reData);
                Gson gson = new Gson();
                ResponseData rdata = gson.fromJson(reData, ResponseData.class);
                if (rdata.getCode().equals("200")) {
                    System.out.println("获取用户是否在社区中成功");
                    isJoined = (Boolean) rdata.getData();
                } else {
                    System.out.println("获取用户是否在社区中失败");
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

    private void updateUI(List<PostData.Post> posts) {
        postList = new ArrayList<>();
        for (PostData.Post post : posts) {
            Map<String, Object> map = new HashMap<>();
            map.put("avatar", post.getPhoto());
            map.put("username", post.getUserName());
            map.put("postTime", TimeUtils.convert(post.getCreateTime()));
            map.put("title", post.getTitle());
            map.put("likeNum", post.getLikeNum());
            map.put("dislikeNum", post.getDislikeNum());
            map.put("commentNum", post.getCommentNum());
            postList.add(map);
        }

        String[] from = {"avatar", "username", "postTime", "title", "likeNum", "dislikeNum", "commentNum"};
        int[] to = {R.id.avatar, R.id.username1, R.id.postTime1, R.id.tv_content, R.id.cb_number_up, R.id.tv_number_down, R.id.tv_number_review};

        SimpleAdapter simpleAdapter = new SimpleAdapter(CommunityInnerActivity.this, postList, R.layout.post_item, from, to) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ImageView avatar = view.findViewById(R.id.avatar);
                String avatarUrl = (String) postList.get(position).get("avatar");
                // 使用Glide加载图片
                Glide.with(CommunityInnerActivity.this)
                        .load(avatarUrl)
                        .into(avatar);
                // 为每个item设置点击事件
                view.setOnClickListener(v -> {
                    // 获取被点击的item的数据
                    PostData.Post post = posts.get(position);
                    // 执行跳转操作
                    navigateToPostDetail(post);
                });
                return view;
            }
        };
        CommunityInnerActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                lv_in_community.setAdapter(simpleAdapter);
            }
        });
    }

    private void navigateToPostDetail(PostData.Post post) {
        // 创建一个Intent或使用其他导航方法，将post传递给新的活动或片段
        Intent intent = new Intent(CommunityInnerActivity.this, PostDetailActivity.class);
        intent.putExtra("post", post);
        startActivity(intent);
    }


}
