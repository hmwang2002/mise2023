package com.example.android_demo.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.android_demo.R;
import com.example.android_demo.databinding.FragmentLikeBinding;
import com.example.android_demo.ui.square.post.PostDetailActivity;
import com.example.android_demo.utils.PostData;
import com.example.android_demo.utils.TimeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author SummCoder
 * @date 2023/12/9 17:54
 */
public class LikeFragment extends Fragment {

    private FragmentLikeBinding binding;
    private ListView lv_like;
    private LikeViewModel likeViewModel;
    private List<Map<String, Object>> list;
    private TextView tv_none_like;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentLikeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        lv_like = root.findViewById(R.id.lv_like);
        tv_none_like = root.findViewById(R.id.tv_none_like);
        return root;
    }


    private void updateUI(List<PostData.Post> posts) {
        if(posts == null || posts.isEmpty()){
            tv_none_like.setVisibility(View.VISIBLE);
            lv_like.setVisibility(View.GONE);
            return;
        }else {
            tv_none_like.setVisibility(View.GONE);
            lv_like.setVisibility(View.VISIBLE);
        }
        list = new ArrayList<>();
        for (PostData.Post post : posts) {
            Map<String, Object> map = new HashMap<>();
            map.put("avatar", post.getPhoto());
            map.put("username", post.getUserName());
            map.put("postTime", TimeUtils.convert(post.getCreateTime()));
            map.put("content", post.getTitle());
            list.add(map);
        }

        String[] from = {"avatar", "username", "postTime", "content"};
        int[] to = {R.id.iv_like_avatar, R.id.tv_like_username, R.id.tv_like_postTime, R.id.tv_like_desc};

        SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(), list, R.layout.like_item, from, to) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ImageView avatarImageView = view.findViewById(R.id.iv_like_avatar);
                String avatarUrl = (String) list.get(position).get("avatar");
                // 使用Glide加载头像图片
                Glide.with(getActivity())
                        .load(avatarUrl)
                        .into(avatarImageView);

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

        lv_like.setAdapter(simpleAdapter);
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

    void reload(){
        likeViewModel = new ViewModelProvider(this).get(LikeViewModel.class);
        likeViewModel.getPostsLiveData().observe(getViewLifecycleOwner(), this::updateUI);
        likeViewModel.fetchData();
    }

    private void navigateToPostDetail(PostData.Post post) {
        // 创建一个Intent或使用其他导航方法，将post传递给新的活动或片段
        Intent intent = new Intent(getActivity(), PostDetailActivity.class);
        intent.putExtra("post", post);
        startActivity(intent);
    }
}
