package com.example.android_demo.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.android_demo.community.HotFragment;
import com.example.android_demo.community.InFragment;
import com.example.android_demo.community.RecommendFragment;

/**
 * @author SummCoder
 * @date 2023/12/7 20:35
 */
public class CommunityAdapter extends FragmentStateAdapter {
    public CommunityAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new HotFragment();
            case 1:
                return new RecommendFragment();
            default:
                return new InFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
