package com.example.prototypesabre.AuthenticatedUserFragment.Chat;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.prototypesabre.R;
import com.google.android.material.tabs.TabLayout;


public class Chat extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_chat, container, false);
        setHasOptionsMenu(true);

        TabLayout tabLayout = root.findViewById(R.id.tabLayout);
        TabLayout tabChats = root.findViewById(R.id.messageIndividualTab);
        TabLayout tabGroup = root.findViewById(R.id.groupMessageTab);

        final ViewPager viewPager = root.findViewById(R.id.viewPager);

        PagerAdapter pagerAdapter = new pagerAdapter(getChildFragmentManager()
                , tabLayout.getTabCount());

        viewPager.setAdapter(pagerAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.chat_menu_options, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.connect:
                Intent intent = new Intent(getContext(), connectWithOthers.class);
                startActivity(intent);
                return true;

            case R.id.connectReqeust:
                Intent intent1 = new Intent(getContext(), connectRequest.class);
                startActivity(intent1);
                return true;
        }


        return super.onOptionsItemSelected(item);
    }
}
