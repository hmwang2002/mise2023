package com.example.android_demo.community;

import static com.example.android_demo.utils.UserUtils.application;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.android_demo.Constants.constant;
import com.example.android_demo.MyApplication;
import com.example.android_demo.R;
import com.example.android_demo.bean.CommunityExpandData;
import com.example.android_demo.bean.CommunityVO;
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
 * @date 2023/12/7 20:38
 */
public class RecommendFragment extends Fragment {
    private List<Map<String, Object>> communityList;
    private static int[] coverArray = {R.drawable.cover0, R.drawable.cover1, R.drawable.cover2, R.drawable.cover3,
            R.drawable.cover4, R.drawable.cover5, R.drawable.cover6, R.drawable.cover7, R.drawable.cover8, R.drawable.cover9};    private int cover;
    private ListView lv_recommended;
    private List<CommunityVO> communityVOS;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_recommend, null);
        lv_recommended = view.findViewById(R.id.lv_recommended);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        fetchData();
    }

    // 获取推荐给用户的社区
    private void fetchData(){
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
                        .url(constant.IP_ADDRESS + "/community/getRecommendedCommunities?userId=" + MyApplication.getInstance().infoMap.get("loginId"))
                        .addHeader("satoken", Objects.requireNonNull(application.infoMap.get("satoken")))
                        .build();
                // 执行发送的指令，获得返回结果
                Response response = client.newCall(request).execute();
                String reData = response.body().string();
                System.out.println("res" + reData);
                Gson gson = new Gson();
                CommunityExpandData rdata = gson.fromJson(reData, CommunityExpandData.class);
                if (rdata.getCode().equals("200")) {
                    System.out.println("获取推荐社区成功");
                    communityVOS = rdata.getData();
                    updateUI(communityVOS);
                } else {
                    System.out.println("获取推荐社区失败");
                }
            } catch (Exception e) {
                // 处理异常，例如记录日志
                e.printStackTrace();
            }
        }).start();
    }

    private void updateUI(List<CommunityVO> communityVOs) {
        communityList = new ArrayList<>();
        int i = 0;
        for (CommunityVO communityVO : communityVOs) {
            Map<String, Object> map = new HashMap<>();
            map.put("communityID", communityVO.communityID);
            map.put("name", communityVO.name);

            map.put("cover", coverArray[i%10]);
            i++;
            communityList.add(map);
        }

        String[] from = {"cover", "name"};
        int[] to = {R.id.iv_cover, R.id.tv_community_name};

        SimpleAdapter simpleAdapter = new SimpleAdapter(getContext(), communityList, R.layout.community_item, from, to) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                CheckBox cb_community_follow = view.findViewById(R.id.cb_community_follow);
                cb_community_follow.setVisibility(View.GONE);
                cb_community_follow.setEnabled(false);
                Button bt_apply_community_follow = view.findViewById(R.id.bt_apply_community_follow);
                bt_apply_community_follow.setVisibility(View.VISIBLE);
                bt_apply_community_follow.setEnabled(true);
                bt_apply_community_follow.setOnClickListener(v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setMessage("确定要申请加入该社区吗？")
                            .setPositiveButton("确定", (dialog, which) -> {
                                applyForCommunity(Long.parseLong(Objects.requireNonNull(communityList.get(position).get("communityID")).toString()));
                                communityList.remove(position); // 从数据源中移除对应位置的评论
                                notifyDataSetChanged(); // 通知适配器数据已更改
                            })
                            .setNegativeButton("取消", (dialog, which) -> {
                                // 用户点击取消，不执行任何操作
                            })
                            .show();
                });
                // 为每个item设置点击事件
                view.setOnClickListener(v -> {
                    // 获取被点击的item的数据
                    CommunityVO communityVO = communityVOs.get(position);
                    cover = (int) communityList.get(position).get("cover");
                    // 执行跳转操作
                    navigateToCommunityVODetail(communityVO);
                });
                return view;
            }
        };
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lv_recommended.setAdapter(simpleAdapter);
            }
        });
    }

    private void navigateToCommunityVODetail(CommunityVO communityVO) {
        Intent intent = new Intent(getContext(), CommunityInnerActivity.class);
        Bundle bundle = new Bundle();
        //把数据保存到Bundle里
        bundle.putString("id", String.valueOf(communityVO.communityID));
        bundle.putString("cover", String.valueOf(cover));
        bundle.putString("name", communityVO.name);
        bundle.putBoolean("isPublic", communityVO.isPublic);
        //把bundle放入intent里
        intent.putExtra("Message",bundle);
        startActivity(intent);
    }


    // 加入社区提交申请
    private void applyForCommunity(long communityID){
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
                        .url(constant.IP_ADDRESS + "/application/applyForCommunity?userID=" + Objects.requireNonNull(application.infoMap.get("loginId")) + "&communityID=" + communityID )
                        .addHeader("satoken", Objects.requireNonNull(application.infoMap.get("satoken")))
                        .build();
                // 执行发送的指令，获得返回结果
                Response response = client.newCall(request).execute();
                String reData = response.body().string();
                Gson gson = new Gson();
                ResponseData rdata = gson.fromJson(reData, ResponseData.class);
                if (Objects.equals(rdata.getCode(), "200")){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "加入社区申请发送成功！", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (Exception e) {
                // 处理异常，例如记录日志
                e.printStackTrace();
            }
        }).start();
    }

}
