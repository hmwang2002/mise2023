package com.example.android_demo.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.android_demo.R;
import com.example.android_demo.adapter.CommunityExpandableAdapter;
import com.example.android_demo.bean.CommunityExpandBean;
import com.example.android_demo.bean.CommunityVO;
import com.example.android_demo.databinding.FragmentJoinedBinding;
import com.example.android_demo.utils.PostData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SummCoder
 * @date 2023/12/9 17:53
 */
public class JoinedFragment extends Fragment {

    private FragmentJoinedBinding binding;

    private ExpandableListView lv_joined;

    private List<CommunityExpandBean> dataEntityList = new ArrayList<>();

    private CommunityExpandableAdapter communityExpandableAdapter;

    private JoinedViewModel joinedViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentJoinedBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        lv_joined = root.findViewById(R.id.lv_joined);
        dataEntityList.add(new CommunityExpandBean("我创建的社区", new ArrayList<>()));
        dataEntityList.add(new CommunityExpandBean("我管理的社区", new ArrayList<>()));
        dataEntityList.add(new CommunityExpandBean("我加入的社区", new ArrayList<>()));
        initAdapter();
        return root;
    }

    private void loadData() {

        joinedViewModel = new ViewModelProvider(this).get(JoinedViewModel.class);

        joinedViewModel.getCommunityCreatedLiveData().observe(getViewLifecycleOwner(), communityVOs -> {
            List<CommunityExpandBean.ChildrenData> childrenData = new ArrayList<>();
            for (CommunityVO communityVO : communityVOs) {
                CommunityExpandBean.ChildrenData child = new CommunityExpandBean.ChildrenData(communityVO.name, communityVO.communityID);
                childrenData.add(child);
            }
            CommunityExpandBean communityExpandBean = new CommunityExpandBean("我创建的社区", childrenData);
            dataEntityList.set(0, communityExpandBean);
        });

        joinedViewModel.getCommunityManagedLiveData().observe(getViewLifecycleOwner(), communityVOs -> {
            List<CommunityExpandBean.ChildrenData> childrenData = new ArrayList<>();
            for (CommunityVO communityVO : communityVOs) {
                CommunityExpandBean.ChildrenData child = new CommunityExpandBean.ChildrenData(communityVO.name, communityVO.communityID);
                childrenData.add(child);
            }
            CommunityExpandBean communityExpandBean = new CommunityExpandBean("我管理的社区", childrenData);
            dataEntityList.set(1, communityExpandBean);
        });

        joinedViewModel.getCommunityJoinedLiveData().observe(getViewLifecycleOwner(), communityVOs -> {
            List<CommunityExpandBean.ChildrenData> childrenData = new ArrayList<>();
            for (CommunityVO communityVO : communityVOs) {
                CommunityExpandBean.ChildrenData child = new CommunityExpandBean.ChildrenData(communityVO.name, communityVO.communityID);
                childrenData.add(child);
            }
            CommunityExpandBean communityExpandBean = new CommunityExpandBean("我加入的社区", childrenData);
            dataEntityList.set(2, communityExpandBean);
        });

        joinedViewModel.fetchCreatedData();
        joinedViewModel.fetchManagedData();
        joinedViewModel.fetchJoinedData();
    }

    private void initAdapter() {
        communityExpandableAdapter = new CommunityExpandableAdapter(getContext(), dataEntityList);
        lv_joined.setAdapter(communityExpandableAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
        communityExpandableAdapter.reFreshData(dataEntityList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
