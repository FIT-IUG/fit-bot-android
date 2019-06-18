package com.logicoverflow.fitbot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.logicoverflow.fitbot.Util.AndroidAnimationBuilder;

public class SettingActivity extends AppCompatActivity {

    private LinearLayout settings_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        settings_layout = findViewById(R.id.settings_layout);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateSlideRight(this);
    }
}
