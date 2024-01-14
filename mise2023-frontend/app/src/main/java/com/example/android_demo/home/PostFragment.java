package com.example.android_demo.home;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.android_demo.Constants.constant;
import com.example.android_demo.R;
import com.example.android_demo.databinding.FragmentPostBinding;
import com.example.android_demo.ui.square.post.PostDetailActivity;
import com.example.android_demo.utils.PostData;
import com.example.android_demo.utils.TimeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author SummCoder
 * @date 2023/12/9 17:54
 */
public class PostFragment extends Fragment {
    private FragmentPostBinding binding;
    private ListView lv_post;
    private PostViewModel postViewModel;
    private List<Map<String, Object>> list;

    AlertDialog dialog;
    private TextView tv_none_post;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPostBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        lv_post = root.findViewById(R.id.lv_post);
        tv_none_post = root.findViewById(R.id.tv_none_post);
        return root;
    }
    private void updateUI(List<PostData.Post> posts) {
        if(posts == null || posts.isEmpty()){
            tv_none_post.setVisibility(View.VISIBLE);
            lv_post.setVisibility(View.GONE);
            return;
        }else {
            tv_none_post.setVisibility(View.GONE);
            lv_post.setVisibility(View.VISIBLE);
        }
        list = new ArrayList<>();
        for (PostData.Post post : posts) {
            Map<String, Object> map = new HashMap<>();
            map.put("avatar", post.getPhoto());
            map.put("username", post.getUserName());
            map.put("postTime", TimeUtils.convert(post.getCreateTime()));
            map.put("content", post.getTitle());
            map.put("numberUp", post.getLikeNum());
            map.put("numberDown", post.getDislikeNum());
            map.put("numberReview", post.getCommentNum());
            list.add(map);
        }
        String[] from = {"avatar", "username", "postTime", "content", "numberUp", "numberDown", "numberReview"};
        int[] to = {R.id.avatar, R.id.username1, R.id.postTime1, R.id.tv_content, R.id.cb_number_up,
                R.id.tv_number_down, R.id.tv_number_review};

        SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(), list, R.layout.post_item, from, to) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ImageView avatarImageView = view.findViewById(R.id.avatar);
                CheckBox cb_up = view.findViewById(R.id.cb_up);
                cb_up.setEnabled(false);
                CheckBox cb_down = view.findViewById(R.id.iv_down);
                cb_down.setEnabled(false);
                String avatarUrl = (String) list.get(position).get("avatar");
                ImageView iv_post_delete = view.findViewById(R.id.iv_post_delete);
                iv_post_delete.setEnabled(true);
                iv_post_delete.setVisibility(View.VISIBLE);
                // 使用Glide加载头像图片
                Glide.with(getActivity())
                        .load(avatarUrl)
                        .into(avatarImageView);
                // 为删除按钮设置点击事件
                iv_post_delete.setOnClickListener(v ->{
                    // 获取被点击的item的数据
                    PostData.Post post = posts.get(position);
                    // 调用后端接口执行删除操作
                    // 弹出一个对话框，让用户确认是否要删除该帖子
                    dialog = new AlertDialog.Builder(getActivity())
                            .setTitle("确认删除")
                            .setMessage("确定要删除该帖子吗？")
                            .setPositiveButton("确定", (dialog, which) -> {
                                // 用户点击确定按钮，执行删除操作
                                deletePost(post.getPostId());
                            })
                            .setNegativeButton("取消", null)
                            .show();
                });
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
        lv_post.setAdapter(simpleAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        reload();
    }

    void reload(){
        postViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        postViewModel.getPostsLiveData().observe(getViewLifecycleOwner(), this::updateUI);
        postViewModel.fetchData();
    }

    private void navigateToPostDetail(PostData.Post post) {
        // 创建一个Intent或使用其他导航方法，将post传递给新的活动或片段
        Intent intent = new Intent(getActivity(), PostDetailActivity.class);
        intent.putExtra("post", post);
        startActivity(intent);
    }

    private void deletePost(String postId) {
        // 调用后端接口执行删除操作
        String url = constant.IP_ADDRESS + "/post/deletePost";
        Thread thread = new Thread(() -> {
            try {
                // 创建 HTTP 客户端
                OkHttpClient client = new OkHttpClient()
                        .newBuilder()
                        .connectTimeout(60000, TimeUnit.MILLISECONDS)
                        .readTimeout(60000, TimeUnit.MILLISECONDS)
                        .build();

                // 创建 POST 请求的表单数据
                RequestBody requestBody = new FormBody.Builder()
                        .add("postId", String.valueOf(postId))
                        .build();

                // 创建 HTTP 请求
                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();

                // 执行发送的指令，获得返回结果
                Response response = client.newCall(request).execute();
                // 输出响应的内容
                System.out.println(response.body().string());
                // 如果删除成功，刷新页面内容
                if (response.isSuccessful()) {
                    // 关闭对话框
                    getActivity().runOnUiThread(() -> {
                        dialog.dismiss();
                    });
                    // 重新获取帖子数据
                    postViewModel.fetchData();
                }else {
                    // 删除失败，关闭对话框并提示用户
                    getActivity().runOnUiThread(() -> {
                        dialog.dismiss();
                        Toast.makeText(getActivity(), "删除失败，请稍后重试", Toast.LENGTH_SHORT).show();
                    });
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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}
