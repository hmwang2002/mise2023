package com.example.android_demo.ui.square.post;

import static com.example.android_demo.ui.square.SquareFragment.DISLIKE_BACK_URL;
import static com.example.android_demo.ui.square.SquareFragment.DISLIKE_URL;
import static com.example.android_demo.ui.square.SquareFragment.LIKE_BACK_URL;
import static com.example.android_demo.ui.square.SquareFragment.LIKE_URL;
import static com.example.android_demo.utils.UserUtils.application;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android_demo.Constants.constant;
import com.example.android_demo.MainViewModel;
import com.example.android_demo.R;
import com.example.android_demo.adapter.CommentAdapter;
import com.example.android_demo.utils.CommentData;
import com.example.android_demo.utils.PostData;
import com.example.android_demo.utils.TimeUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostDetailActivity extends AppCompatActivity {
    private List<CommentData.Comment> commentList = new ArrayList<>();

    private CommentAdapter commentAdapter;
    ImageView image;
    String imageUrl;
    private ImageView iv_postDetailAvatar;
    private TextView tv_postDetailUsername;

    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_detail);
        image = findViewById(R.id.image);

        iv_postDetailAvatar = findViewById(R.id.iv_postDetailAvatar);
        tv_postDetailUsername = findViewById(R.id.tv_postDetailUsername);


        // 获取从上一个活动传递的帖子ID
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("post")) {
            PostData.Post post = (PostData.Post) intent.getSerializableExtra("post");
            // 在这里加载和显示帖子详细信息
            assert post != null;
            imageUrl = post.getPostPicture();
            System.out.println(" image  " + imageUrl);
            showPostDetails(post);
            Glide.with(PostDetailActivity.this)
                    .load(post.getPhoto())
                    .into(iv_postDetailAvatar);
            tv_postDetailUsername.setText(post.getUserName());
            getComments(post);
            commentAdapter = new CommentAdapter(this, commentList);
            if (commentList != null) {
                RecyclerView rv_comment = findViewById(R.id.rv_comment);
                rv_comment.setAdapter(commentAdapter);
                rv_comment.setLayoutManager(new LinearLayoutManager(this) {
                    @Override
                    public boolean canScrollVertically() {
                        return false;//禁止滑动
                    }
                });
                rv_comment.setFocusable(false); // 关闭默认聚焦
            }
            try {
                URL url = new URL(imageUrl);
                requestImg(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        CheckBox up = findViewById(R.id.detail_up);
        CheckBox down = findViewById(R.id.detail_down);

        // 获取SharedPreferences对象
        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        up.setOnClickListener(v -> {
            down.setEnabled(!up.isChecked());
            PostData.Post post = (PostData.Post) getIntent().getSerializableExtra("post");

            // 存储点赞状态
            editor.putBoolean("post_" + post.getPostId() + "_like", up.isChecked());
            editor.apply();

            // 在这里可以执行其他你想要的操作，比如发送网络请求来保存点赞状态等
            saveData(post, up.isChecked() ? LIKE_URL : LIKE_BACK_URL);

            if (up.isChecked()) {
                Toast.makeText(this, "点赞成功！", Toast.LENGTH_SHORT).show();
            }
        });

        down.setOnClickListener(v -> {
            up.setEnabled(!down.isChecked());
            PostData.Post post = (PostData.Post) getIntent().getSerializableExtra("post");

            // 存储点踩状态
            editor.putBoolean("post_" + post.getPostId() + "_dislike", down.isChecked());
            editor.apply();

            // 在这里可以执行其他你想要的操作，比如发送网络请求来保存点踩状态等
            saveData(post, down.isChecked() ? DISLIKE_URL : DISLIKE_BACK_URL);

            if (down.isChecked()) {
                Toast.makeText(this, "感谢反馈！", Toast.LENGTH_SHORT).show();
            }
        });

        PostData.Post post = (PostData.Post) getIntent().getSerializableExtra("post");
        boolean liked = preferences.getBoolean("post_" + post.getPostId() + "_like", false);
        boolean disliked = preferences.getBoolean("post_" + post.getPostId() + "_dislike", false);

        up.setChecked(liked);
        down.setChecked(disliked);
        down.setEnabled(!liked);
        up.setEnabled(!disliked);

        ImageView iv_postDetailBack = findViewById(R.id.iv_postDetailBack);
        iv_postDetailBack.setOnClickListener(view -> {
            finish();
        });

    }

    private void getComments(PostData.Post post) {
        Thread thread = new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient()
                        .newBuilder()
                        .connectTimeout(60000, TimeUnit.MILLISECONDS)
                        .readTimeout(60000, TimeUnit.MILLISECONDS)
                        .build();

                RequestBody requestBody = new FormBody.Builder()
                        .add("postId", String.valueOf(post.getPostId()))
                        .build();

                Request request = new Request.Builder()
                        .url(constant.IP_ADDRESS + "/user/getComments")
                        .post(requestBody)
                        .addHeader("satoken", Objects.requireNonNull(application.infoMap.get("satoken")))
                        .build();

                Response response = client.newCall(request).execute();

                // 读取响应体的内容并保存到变量中
                String responseData = response.body().string();

                // 在这里进行两次调用之间的处理，比如将字符串转换为对象
                Gson gson = new Gson();
                CommentData rdata = gson.fromJson(responseData, CommentData.class);
                commentList = rdata.getData();

            } catch (Exception e) {
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

    private void addComments(String comment, PostData.Post post) {
        Thread thread = new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient()
                        .newBuilder()
                        .connectTimeout(60000, TimeUnit.MILLISECONDS)
                        .readTimeout(60000, TimeUnit.MILLISECONDS)
                        .build();

                // 设置请求体为 JSON
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                // 构建 JSON 数据
                String json = new Gson().toJson(new CommentData.CommentVO("", post.getPostId(), comment));

                RequestBody requestBody = RequestBody.create(JSON, json);

                Request request = new Request.Builder()
                        .url(constant.IP_ADDRESS + "/user/comment")
                        .post(requestBody)
                        .addHeader("satoken", Objects.requireNonNull(application.infoMap.get("satoken")))
                        .build();

                Response response = client.newCall(request).execute();
                String responseData = response.body().string();

            } catch (Exception e) {
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


    private void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        totalHeight += (listView.getDividerHeight() * (listAdapter.getCount() - 1));

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


    @SuppressLint("SetTextI18n")
    private void showPostDetails(PostData.Post post) {
        // 根据postId加载帖子详细信息，例如从数据库或网络请求中获取数据
        // 这里只是一个示例，您需要根据您的应用程序逻辑进行实际的实现

        // 在布局中找到用于显示帖子详细信息的TextView
        TextView postDetailTitleTextView = findViewById(R.id.postDetailTitle);
        TextView postDetailUsernameAndTimeTextView = findViewById(R.id.postDetailUsername);
        TextView postDetailCommunityNameTextView = findViewById(R.id.postDetailCommunityName);
        TextView postDetailTagListTextView = findViewById(R.id.postDetailTagList);
        TextView postDetailContentTextView = findViewById(R.id.postDetailContent);

        // 将帖子信息显示在TextView中
        postDetailTitleTextView.setText(post.getTitle());
        postDetailUsernameAndTimeTextView.setText("作者：" + post.getUserName() + "    发布时间：" + TimeUtils.convert(post.getCreateTime()));
        postDetailTagListTextView.setText("标签：" + post.getTagList());
        postDetailCommunityNameTextView.setText("社区：" + post.getCommunityName());
        postDetailContentTextView.setText(post.getContent());

    }

    private void requestImg(final URL imgUrl) {
        new Thread(() -> {
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(imgUrl.openStream());
                showImg(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void showImg(final Bitmap bitmap) {
        runOnUiThread(() -> image.setImageBitmap(bitmap));
    }

    public void submitComment(View view) {
        // 获取评论框的文本
        EditText commentEditText = findViewById(R.id.commentEditText);
        String commentText = commentEditText.getText().toString().trim();

        // 检查评论是否为空
        if (!commentText.isEmpty()) {
            // 执行提交评论的逻辑，可以发送评论到服务器
            Toast.makeText(this, "你的评论已发布！", Toast.LENGTH_SHORT).show();
            addComments(commentText, (PostData.Post) getIntent().getSerializableExtra("post"));
            // 清空评论框
            commentEditText.setText("");
            // 刷新评论列表
            runOnUiThread(() -> {
                commentAdapter.notifyDataSetChanged();
                restartActivity();
            });

        } else {
            // 提示用户评论不能为空
            Toast.makeText(this, "评论不能为空！", Toast.LENGTH_SHORT).show();
        }
    }

    private void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private void saveData(PostData.Post post, String url) {
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
                        .add("postId", String.valueOf(post.getPostId()))
                        .build();

                // 创建 HTTP 请求
                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .addHeader("satoken", Objects.requireNonNull(application.infoMap.get("satoken")))
                        .build();

                // 执行发送的指令，获得返回结果
                Response response = client.newCall(request).execute();

                // 输出响应的内容
                System.out.println(response.body().string());
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
