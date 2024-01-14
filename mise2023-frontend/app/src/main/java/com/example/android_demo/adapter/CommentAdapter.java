package com.example.android_demo.adapter;

// 适配器类 CommentAdapter.java

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android_demo.R;
import com.example.android_demo.ui.square.post.PostDetailActivity;
import com.example.android_demo.utils.CommentData;
import com.example.android_demo.utils.TimeUtils;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<CommentData.Comment> commentList;
    private Context mContext;


    public CommentAdapter(Context context, List<CommentData.Comment> commentList) {
        this.mContext = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        CommentData.Comment comment = commentList.get(position);
        // 设置头像（这里使用了占位图 avatar0）
        Glide.with(mContext)
                .load(comment.getPhoto())
                .into(holder.avatarImageView);
        holder.usernameTextView.setText(comment.getUserName());
        holder.postTimeTextView.setText(TimeUtils.convert(comment.getCreateTime()));
        holder.contentTextView.setText(comment.getContent());
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView avatarImageView;
        TextView usernameTextView;
        TextView postTimeTextView;
        TextView contentTextView;

        public CommentViewHolder(View itemView) {
            super(itemView);
            avatarImageView = itemView.findViewById(R.id.avatar);
            usernameTextView = itemView.findViewById(R.id.username1);
            postTimeTextView = itemView.findViewById(R.id.postTime1);
            contentTextView = itemView.findViewById(R.id.tv_content);


        }
    }
}
