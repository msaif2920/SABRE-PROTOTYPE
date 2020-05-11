package com.example.prototypesabre.AuthenticatedUserFragment.Chat;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.prototypesabre.AuthenticatedUserFragment.Chat.chatFragment.groupMessageFragment;
import com.example.prototypesabre.AuthenticatedUserFragment.Chat.chatFragment.messageFragment;

public class pagerAdapter extends FragmentPagerAdapter {

    private int numOfTabs;

    public pagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        this.numOfTabs = behavior;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new messageFragment();
            case 1:
                return new groupMessageFragment();


        }

        return null;
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
