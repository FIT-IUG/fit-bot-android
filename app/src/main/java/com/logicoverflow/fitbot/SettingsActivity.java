package com.logicoverflow.fitbot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class SettingsActivity extends AppCompatActivity implements IPickResult {


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPreferencesEditor;
    private ImageView back_button;
    private Switch enable_darkMode_switch;
    private ImageButton background_picker;

    @Override
    protected void onCreate(final Bundle savedInstanceState)  {


        sharedPreferences = getSharedPreferences(ChatActivity.THEME_PREFERENCES, Context.MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();

        final String theme = getSharedPreferences(ChatActivity.THEME_PREFERENCES, MODE_PRIVATE).getString(ChatActivity.THEME_SAVED, ChatActivity.LIGHTTHEME);
        if (theme.equals(ChatActivity.LIGHTTHEME)) {
            setTheme(R.style.AppThemeLight);

        } else {
            setTheme(R.style.AppThemeDark);
        }



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        background_picker = findViewById(R.id.background_picker);
        enable_darkMode_switch = findViewById(R.id.enable_darkMode_switch);
        if (theme.equals(ChatActivity.LIGHTTHEME)) {
            enable_darkMode_switch.setChecked(false);

        } else {
            enable_darkMode_switch.setChecked(true);
        }


        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        enable_darkMode_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sharedPreferencesEditor.putString(ChatActivity.THEME_SAVED, ChatActivity.DARKTHEME);
                    sharedPreferencesEditor.apply();
                    Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    sharedPreferencesEditor.putString(ChatActivity.THEME_SAVED, ChatActivity.LIGHTTHEME);
                    sharedPreferencesEditor.apply();
                    Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        //Button change_theme_black = findViewById(R.id.change_theme_black);
        //Button change_theme_white = findViewById(R.id.change_theme_white);



//        change_theme_black.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                sharedPreferencesEditor.putString(ChatActivity.THEME_SAVED, ChatActivity.DARKTHEME);
//                sharedPreferencesEditor.apply();
//                Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
//                startActivity(intent);
//                finish();
//                //recreate();
//            }
//        });
//
//        change_theme_white.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sharedPreferencesEditor.putString(ChatActivity.THEME_SAVED, ChatActivity.LIGHTTHEME);
//                sharedPreferencesEditor.apply();
//                Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
//                startActivity(intent);
//                finish();
//                //recreate();
//            }
//        });


        ImageButton background_picker = findViewById(R.id.background_picker);
        background_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        PickImageDialog.build(new PickSetup()).show(SettingsActivity.this);
                    }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateSlideRight(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPickResult(PickResult pickResult) {
        sharedPreferencesEditor.putString(ChatActivity.BACKGROUND_SAVED, bitmapToString(pickResult.getBitmap()));
        sharedPreferencesEditor.apply();
        Toast.makeText(this, "Background Saved", Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    public String bitmapToString(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();
        String encoded = Base64.encodeToString(b, Base64.DEFAULT);
        return encoded;
    }


}
