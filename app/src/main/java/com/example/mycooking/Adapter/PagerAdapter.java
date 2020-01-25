package com.example.mycooking.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.mycooking.Chat.ChatlistFragment;
import com.example.mycooking.Users.UsersFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;
    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                UsersFragment tab1 = new UsersFragment();
                return tab1;
            case 1:
                ChatlistFragment tab2 = new ChatlistFragment();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
