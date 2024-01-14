package com.example.android_demo.ui.square;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.android_demo.Constants.constant;
import com.example.android_demo.R;
import com.example.android_demo.databinding.FragmentSquareBinding;
import com.example.android_demo.ui.square.post.PostDetailActivity;
import com.example.android_demo.utils.PostData;
import com.example.android_demo.utils.TimeUtils;
import com.example.android_demo.utils.UserUtils;

import java.util.List;

public class SquareFragment extends Fragment {
    private FragmentSquareBinding binding;
    private View view;
    private GridLayout gl_posts;
    private SquareViewModel squareViewModel;

    public static final String LIKE_URL = constant.IP_ADDRESS + "/user/like";
    public static final String LIKE_BACK_URL = constant.IP_ADDRESS + "/user/like_back";
    public static final String DISLIKE_URL = constant.IP_ADDRESS + "/user/dislike";
    public static final String DISLIKE_BACK_URL = constant.IP_ADDRESS + "/user/dislike_back";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            binding = FragmentSquareBinding.inflate(inflater, container, false);
            view = binding.getRoot();
            gl_posts = view.findViewById(R.id.gl_posts);

            // 初始化 ViewModel，但只在第一次创建时执行
            squareViewModel = new ViewModelProvider(this).get(SquareViewModel.class);

            // 观察 LiveData 变化，但只在第一次创建时执行
            squareViewModel.getPostsLiveData().observe(getViewLifecycleOwner(), posts -> {
                if (posts != null && !posts.isEmpty()) {
                    updateUI(posts);
                } else {
                    gl_posts.removeAllViews();
                }
            });

            // 发起数据请求，但只在第一次创建时执行
            squareViewModel.fetchData();
        }

        return view;
    }


    private void updateUI(List<PostData.Post> posts) {

        gl_posts.removeAllViews();

        for (PostData.Post post : posts) {
            View view1 = LayoutInflater.from(getActivity()).inflate(R.layout.post_item, null);

            TextView NameTextView = view1.findViewById(R.id.username1);
            NameTextView.setText(post.getUserName());

            ImageView avatorImageView = view1.findViewById(R.id.avatar);
            Glide.with(this)
                    .load(post.getPhoto())
                    .into(avatorImageView);

            TextView contentTextView = view1.findViewById(R.id.tv_content);
            contentTextView.setText(post.getTitle());

            TextView TimeTextView = view1.findViewById(R.id.postTime1);
            TimeTextView.setText(TimeUtils.convert(post.getCreateTime()));

            TextView UpTextView = view1.findViewById(R.id.cb_number_up);
            UpTextView.setText(post.getLikeNum());

            TextView DownTextView = view1.findViewById(R.id.tv_number_down);
            DownTextView.setText(post.getDislikeNum());

            TextView CommentTextView = view1.findViewById(R.id.tv_number_review);
            CommentTextView.setText(post.getCommentNum());

            CheckBox cbUp = view1.findViewById(R.id.cb_up);

            cbUp.setOnClickListener(v -> {
                Toast.makeText(getActivity(), "点赞无效~请查看全文", Toast.LENGTH_SHORT).show();
            });

            CheckBox ivDown = view1.findViewById(R.id.iv_down);


            ivDown.setOnClickListener(v -> {
                Toast.makeText(getActivity(), "点踩无效~请查看全文", Toast.LENGTH_SHORT).show();
            });

            // 为 post_item 添加点击事件
            view1.setOnClickListener(v -> {
                // 在这里处理点击事件，例如导航到帖子详情页
                navigateToPostDetail(post);
            });

            gl_posts.addView(view1);
        }
    }

    private void navigateToPostDetail(PostData.Post post) {
        // 创建一个Intent或使用其他导航方法，将post传递给新的活动或片段
        Intent intent = new Intent(getActivity(), PostDetailActivity.class);
        intent.putExtra("post", post);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        squareViewModel.fetchData();

        squareViewModel.getPostsLiveData().observe(getViewLifecycleOwner(), posts -> {
            if (posts != null && !posts.isEmpty()) {
                updateUI(posts);
            } else {
                gl_posts.removeAllViews();
            }
        });
    }
}