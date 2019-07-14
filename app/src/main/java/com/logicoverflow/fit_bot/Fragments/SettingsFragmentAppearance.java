package com.logicoverflow.fit_bot.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import com.logicoverflow.fit_bot.ChatActivity;
import com.logicoverflow.fit_bot.R;
import com.logicoverflow.fit_bot.SettingsActivity;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;

public class SettingsFragmentAppearance extends Fragment {

    private Button select_color_button;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;
    private Switch enable_darkMode_switch;
    private ImageButton background_picker;
    private ImageButton remove_background;

    public SettingsFragmentAppearance() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_settings_fragment_appearance, container, false);

        sharedPreferences = getActivity().getSharedPreferences(ChatActivity.THEME_PREFERENCES, Context.MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();

        final String theme = sharedPreferences.getString(ChatActivity.THEME_SAVED, ChatActivity.LIGHTTHEME);
        if (theme.equals(ChatActivity.LIGHTTHEME)) {
            getActivity().setTheme(R.style.AppThemeLight);

        } else {
            getActivity().setTheme(R.style.AppThemeDark);
        }




        enable_darkMode_switch = root.findViewById(R.id.enable_darkMode_switch);
        if (theme.equals(ChatActivity.LIGHTTHEME)) {
            enable_darkMode_switch.setChecked(false);

        } else {
            enable_darkMode_switch.setChecked(true);
        }

        enable_darkMode_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sharedPreferencesEditor.putString(ChatActivity.THEME_SAVED, ChatActivity.DARKTHEME);
                    sharedPreferencesEditor.apply();
                    Intent intent = new Intent(getActivity(), SettingsActivity.class);
                    getActivity().finish();
                    startActivity(intent);

                }else{
                    sharedPreferencesEditor.putString(ChatActivity.THEME_SAVED, ChatActivity.LIGHTTHEME);
                    sharedPreferencesEditor.apply();
                    Intent intent = new Intent(getActivity(), SettingsActivity.class);
                    getActivity().finish();
                    startActivity(intent);

                }
            }
        });

//        select_color_button = root.findViewById(R.id.select_color_button);
//        select_color_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showSelectColorDialog();
//            }
//        });


        background_picker = root.findViewById(R.id.background_picker);
        background_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickImageDialog.build(new PickSetup()).show(getActivity());
            }
        });

        remove_background = root.findViewById(R.id.remove_background);
        remove_background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(sharedPreferences.getString(ChatActivity.BACKGROUND_SAVED,"")==""){
                    Toast.makeText(getActivity(), "لا يوجد خلفية", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(), "تم ازالة الخلفية", Toast.LENGTH_SHORT).show();
                    //sharedPreferencesEditor.remove(ChatActivity.THEME_SAVED);
                    sharedPreferencesEditor.putString(ChatActivity.BACKGROUND_SAVED,"");
                    sharedPreferencesEditor.apply();
                }

            }
        });

        return root;
    }

//    private void showSelectColorDialog() {
//        new ColorPickerDialog()
//                .withPickers(PresetPickerView.class)
//                .withColor(Color.BLACK) // the default / initial color
//                .withListener(new OnColorPickedListener<ColorPickerDialog>() {
//                    @Override
//                    public void onColorPicked(@Nullable ColorPickerDialog dialog, int color) {
//                        Toast.makeText(getActivity(), color+"", Toast.LENGTH_SHORT).show();
//                        sharedPreferencesEditor.putString(ChatActivity.COLOR_SAVED, String.valueOf(color));
//                        sharedPreferencesEditor.apply();
//                    }
//                })
//                .show(getFragmentManager(), "colorPicker");
//    }

}
