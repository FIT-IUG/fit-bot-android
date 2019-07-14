package com.logicoverflow.fit_bot.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.logicoverflow.fit_bot.R;

public class SettingsFragmentAbout extends Fragment {

    public SettingsFragmentAbout() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings_fragment_about, container, false);
    }

}
