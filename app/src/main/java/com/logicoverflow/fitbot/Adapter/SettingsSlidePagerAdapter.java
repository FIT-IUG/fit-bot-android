package com.logicoverflow.fitbot.Adapter;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.logicoverflow.fitbot.Fragments.SettingsFragmentAbout;
import com.logicoverflow.fitbot.Fragments.SettingsFragmentAppearance;

public class SettingsSlidePagerAdapter extends FragmentStatePagerAdapter {

    private static int PAGE_NUM;

    public SettingsSlidePagerAdapter(@NonNull FragmentManager fm, int behavior, int numOfPages) {
        super(fm, behavior);
        this.PAGE_NUM = numOfPages;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position){
            case 0: {
                fragment = new SettingsFragmentAppearance();
                break;
            }
            case 1: {
                fragment = new SettingsFragmentAbout();
                break;
            }

        }
        return fragment;
    }

    @Override
    public int getCount() {
        return PAGE_NUM;
    }
}
