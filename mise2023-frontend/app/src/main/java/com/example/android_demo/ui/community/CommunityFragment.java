package com.example.android_demo.ui.community;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.android_demo.Constants.constant;
import com.example.android_demo.MyApplication;
import com.example.android_demo.R;
import com.example.android_demo.adapter.CommunityAdapter;
import com.example.android_demo.bean.CommunityVO;
import com.example.android_demo.databinding.FragmentCommunityBinding;
import com.example.android_demo.utils.ResponseData;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author SummCoder
 * @date 2023/12/7 21:24
 */
public class CommunityFragment extends Fragment {

    private FragmentCommunityBinding binding;
    private AlertDialog dialog;
    private EditText et_community_name;
    private Button bt_create_community;
    private CheckBox cb_public;
    private ResponseData rdata;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCommunityBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        TabLayout tabLayout_community = view.findViewById(R.id.tabLayout_community);
        ViewPager2 vp2_community = view.findViewById(R.id.vp2_community);
        Button bt_createCommunity = view.findViewById(R.id.bt_createCommunity);
        bt_createCommunity.setOnClickListener(v -> {
            // 创建新社区
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("新建社区");
            // 设置对话框的布局
            View view1 = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_createcommunity, null);
            builder.setView(view1);
            // 创建并显示对话框
            dialog = builder.create();
            dialog.show();
            et_community_name = view1.findViewById(R.id.et_community_name);
            bt_create_community = view1.findViewById(R.id.bt_create_community);
            cb_public = view1.findViewById(R.id.cb_public);
            bt_create_community.setOnClickListener(v1 -> {
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
                        Gson gson = new Gson();
                        CommunityVO communityVO = new CommunityVO();

                        communityVO.name = et_community_name.getText().toString();
                        if(cb_public.isChecked()){
                            communityVO.isPublic = true;
                        }else {
                            communityVO.isPublic = false;
                        }
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
                        communityVO.createTime = dateFormat.format(new Date());

                        String json = gson.toJson(communityVO);
                        RequestBody requestBody = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));

                        Request request = new Request.Builder()
                                .url(constant.IP_ADDRESS + "/community/createCommunity?userID=" + Long.parseLong(Objects.requireNonNull(application.infoMap.get("loginId"))))
                                .post(requestBody)
                                .build();
                        // 执行发送的指令，获得返回结果
                        Response response = client.newCall(request).execute();
                        String reData=response.body().string();
                        System.out.println("redata"+reData);
                        rdata = gson.fromJson(reData, ResponseData.class);
                        System.out.println(rdata.getData());
                    } catch (Exception e) {
                        Log.e(TAG, Log.getStackTraceString(e));
                    }
                });
                thread.start();
                try {
                    thread.join();
                    if(rdata.getCode().equals("200")){
                        Toast.makeText(getActivity(), "社区创建成功", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }else{
                        Toast.makeText(getActivity(), "该社区已存在，社区创建失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

        });
        vp2_community.setAdapter(new CommunityAdapter(requireActivity()));
        TabLayoutMediator tab = new TabLayoutMediator(tabLayout_community, vp2_community, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0:
                        tab.setText("热门");
                        break;
                    case 1:
                        tab.setText("推荐");
                        break;
                    case 2:
                        tab.setText("关注");
                        break;
                }
            }
        });
        tab.attach();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}