package com.logicoverflow.fitbot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.Set;

public class SettingActivity extends AppCompatActivity {


    SharedPreferences themePreferences;
    SharedPreferences.Editor themeEditor;

    @Override
    protected void onCreate(final Bundle savedInstanceState)  {

        final String theme = getSharedPreferences(ChatActivity.THEME_PREFERENCES, MODE_PRIVATE).getString(ChatActivity.THEME_SAVED, ChatActivity.LIGHTTHEME);
        if (theme.equals(ChatActivity.LIGHTTHEME)) {
            setTheme(R.style.AppThemeLight);
        } else {
            setTheme(R.style.AppThemeDark);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow);


        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        Button change_theme_black = findViewById(R.id.change_theme_black);
        Button change_theme_white = findViewById(R.id.change_theme_white);

        themePreferences = getSharedPreferences(ChatActivity.THEME_PREFERENCES, Context.MODE_PRIVATE);
        themeEditor = themePreferences.edit();

        change_theme_black.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                themeEditor.putString(ChatActivity.THEME_SAVED, ChatActivity.DARKTHEME);
                themeEditor.apply();
                Intent intent = new Intent(SettingActivity.this,SettingActivity.class);
                startActivity(intent);
                finish();
                //recreate();
            }
        });

        change_theme_white.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                themeEditor.putString(ChatActivity.THEME_SAVED, ChatActivity.LIGHTTHEME);
                themeEditor.apply();
                Intent intent = new Intent(SettingActivity.this,SettingActivity.class);
                startActivity(intent);
                finish();
                //recreate();
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



}
