package com.logicoverflow.fitbot;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.logicoverflow.fitbot.Util.AndroidAnimationBuilder;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class SplashActivity extends AppCompatActivity {

    ImageView logo_image;
    TextView logo_text;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        logo_image = findViewById(R.id.logo_image_in_splash_activity);
        logo_text = findViewById(R.id.logo_text_in_splash_activity);
        progressBar = findViewById(R.id.splash_progress_bar);

    }

    @Override
    protected void onResume() {
        super.onResume();
        fullScreen();
        animate();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Dexter.withActivity(SplashActivity.this)
                        .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                //readContacts();
                                Intent intent = new Intent(SplashActivity.this,ChatActivity.class);
                                startActivity(intent);
                                //avi.hide();
                                //splashImage.setVisibility(View.INVISIBLE);
                                finish();
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                finish();
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();

            }
        }, 2000);

        //Permission Check


    }

    public void fullScreen() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public void animate() {

        new AndroidAnimationBuilder(logo_image).setDefaultStepDuration(600)
                // first step: quick preparations.
                .alpha(0).ms(10)
                .scaleX(0).scaleY(0)
                .then().alpha(1)
                //.rotateBy(360)
                .reset().then()
                .execute();

        new AndroidAnimationBuilder(logo_text)
                // first step: quick preparations.
                .alpha(0).ms(10).then().pause(300).setDefaultStepDuration(250)
                .then().alpha(1).reset()
                .execute();

        new AndroidAnimationBuilder(progressBar)
                // first step: quick preparations.
                .alpha(0).ms(10).then().pause(600).setDefaultStepDuration(250)
                .then().alpha(1).reset()
                .execute();



    }
}
