package com.logicoverflow.fit_bot.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.logicoverflow.fit_bot.Adapter.ThemeAdapter;
import com.logicoverflow.fit_bot.ChatActivity;
import com.logicoverflow.fit_bot.Model.Theme;
import com.logicoverflow.fit_bot.R;
import com.logicoverflow.fit_bot.SettingsActivity;
import com.logicoverflow.fit_bot.Util.Const;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;

import java.util.ArrayList;

public class SettingsFragmentAppearance extends Fragment {

    private Button select_color_button;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;
    private Switch enable_darkMode_switch;
    private ImageButton background_picker;
    private ImageButton remove_background;
    private ThemeAdapter adapter;
    private ArrayList<Theme> themesList;
    private RecyclerView recyclerView;
    private SeekBar sizeChanger ;
    TextView textSize;
    public SettingsFragmentAppearance() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

         final Context context = getActivity();

           Theme defaultTheme = new Theme(R.drawable.default_theme , "Default" ,0 ,"DefaultTheme" ,true);
           Theme darkblueTheme = new Theme(R.drawable.dark_blue_theme,"Dark blue",1,"DarkBlueTheme",false);
           Theme darkTheme = new Theme(R.drawable.dark_theme , "Dark",2,"DarkTheme",false);
           themesList = new ArrayList<>();
           themesList.add(defaultTheme);
           themesList.add(darkblueTheme);
           themesList.add(darkTheme);

        sharedPreferences = getActivity().getSharedPreferences(Const.THEME_PREFERENCES, Context.MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();

        final String theme = sharedPreferences.getString(Const.THEME_SAVED, Const.DEFTHEME);

        if (theme.equals(Const.DEFTHEME)){

            getActivity().setTheme(R.style.DefaultTheme);
        }else if (theme.equals(Const.DARKBLUETHEME)){

            getActivity().setTheme(R.style.DarkBlueTheme);
        }else if(theme.equals(Const.DARKTHEME)){

          getActivity().setTheme(R.style.DarkTheme);

        }

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_settings_fragment_appearance, container, false);

         textSize = root.findViewById(R.id.textSize_tracker_text);

        sizeChanger = root.findViewById(R.id.text_size_changer);



        int savedTextSize = sharedPreferences.getInt(Const.TEXT_SIZE_OF_MESSAGE,12);
        sizeChanger.setProgress(savedTextSize-12);
        textSize.setText(savedTextSize+"");

        sizeChanger.setMax(18);

        sizeChanger.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                textSize.setText(progress+12+"");
                //Toast.makeText(context , (seekBar.getProgress()+12)+"" ,Toast.LENGTH_SHORT).show();

                //int val = (progress * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
                //textSize.setText(12 + progress+"");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {



            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                int textSize = seekBar.getProgress()+12;
                sharedPreferencesEditor.putInt(Const.TEXT_SIZE_OF_MESSAGE,textSize);
                sharedPreferencesEditor.apply();


            }
        });






//        select_color_button = root.findViewById(R.id.select_color_button);
//        select_color_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showSelectColorDialog();
//            }
//        });



        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        adapter = new ThemeAdapter(themesList ,context);
        recyclerView = root.findViewById(R.id.theme_list);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

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
