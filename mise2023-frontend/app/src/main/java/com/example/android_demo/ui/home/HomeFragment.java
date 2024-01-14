package com.example.android_demo.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.android_demo.MainViewModel;
import com.example.android_demo.R;
import com.example.android_demo.adapter.CommunityAdapter;
import com.example.android_demo.adapter.HomeAdapter;
import com.example.android_demo.databinding.FragmentHomeBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    MainViewModel mainViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        ListView listview = binding.lvTest;
//
//        putData();
//
//        SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(),list, R.layout.community_item_layout,
//                new String[]{"name","id","age","pic","tel"},
//                new int[]{R.id.txtCommunityName,R.id.txtCommunityID,R.id.txtUserAge,R.id.imgHead,R.id.txtUserTel});
//        listview.setAdapter(simpleAdapter);


        //尝试获取共享数据viewmodel
        mainViewModel=new ViewModelProvider(getActivity()).get(MainViewModel.class);

        TabLayout tabLayout_home = root.findViewById(R.id.tabLayout_home);
        ViewPager2 vp2_home = root.findViewById(R.id.vp2_home);
        vp2_home.setAdapter(new HomeAdapter(requireActivity()));
        TabLayoutMediator tab = new TabLayoutMediator(tabLayout_home, vp2_home, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0:
                        tab.setText("消息");
                        break;
                    case 1:
                        tab.setText("社区");
                        break;
                    case 2:
                        tab.setText("泡泡");
                        break;
                    case 3:
                        tab.setText("喜欢");
                        break;
                }
            }
        });
        tab.attach();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}