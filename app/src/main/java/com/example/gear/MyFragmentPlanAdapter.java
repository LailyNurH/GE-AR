package com.example.gear;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MyFragmentPlanAdapter extends FragmentStateAdapter {
    public MyFragmentPlanAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position== 1){
            return new DoneFragment();
        }
        return new ActiveFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
