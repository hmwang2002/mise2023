package com.example.android_demo.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.android_demo.home.JoinedFragment;
import com.example.android_demo.home.LikeFragment;
import com.example.android_demo.home.MessageFragment;
import com.example.android_demo.home.PostFragment;

/**
 * @author SummCoder
 * @date 2023/12/9 17:50
 */
public class HomeAdapter extends FragmentStateAdapter {
    public HomeAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override

    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new MessageFragment();
            case 1:
                return new JoinedFragment();
            case 2:
                return new PostFragment();
            default:
                return new LikeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
