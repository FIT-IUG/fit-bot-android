package com.logicoverflow.fitbot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.logicoverflow.fitbot.Util.AndroidAnimationBuilder;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.apache.commons.codec.binary.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import static com.logicoverflow.fitbot.Util.ZipManager.unzip;

public class SplashActivity extends AppCompatActivity {

    ImageView logo_image;
    TextView logo_text;
    ProgressBar progressBar;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseStorage mFirebaseStorage;
    StorageReference mStorageReference;
    int storedVersion;
    int databaseVersion;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPreferencesEditor;
    File fileDirectory;
    File downloadedFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(getApplicationContext());
        setContentView(R.layout.activity_splash);

        logo_image = findViewById(R.id.logo_image_in_splash_activity);
        logo_text = findViewById(R.id.logo_text_in_splash_activity);
        progressBar = findViewById(R.id.splash_progress_bar);




        //String databaseVersion = mDatabaseReference.child("version").getKey();
        //Toast.makeText(this, databaseVersion, Toast.LENGTH_SHORT).show();


        //File jayDir = new File(Environment.getExternalStorageDirectory().toString() + "/FITChatbot/bots/Fitbot");



        //FirebaseStorage storage = FirebaseStorage.getInstance();
        //StorageReference storageRef = storage.getReferenceFromUrl("gs://fit-bot-936cb.appspot.com").child("version.txt");





//        try {
//            final File localFile = File.createTempFile("version", "txt");
//            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//
//                    try {
//
//                        BufferedReader bufferedReader = new BufferedReader(new FileReader(localFile));
//                        String version;
//                        SharedPreferences sharedPreferences = getSharedPreferences("VersionPreference",Context.MODE_PRIVATE);
//                        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
//
//                        if ((version = bufferedReader.readLine()) != null && version.matches("\\d+")) {
//                            int currentVersion = sharedPreferences.getInt("version",0);
//                            int updatedVersion = Integer.parseInt(version);
//                            if(currentVersion==0){
//                                sharedPreferencesEditor.putInt("version",updatedVersion);
//                            }else if(currentVersion<updatedVersion){
//
//                            }else if(currentVersion==updatedVersion){
//
//                            }
//
//                        }else{
//                            Toast.makeText(SplashActivity.this, "Invalid version number", Toast.LENGTH_SHORT).show();
//                        }
//                        bufferedReader.close();
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                }
//            });
//        } catch (IOException e ) {}

    }

    private void getDatabaseVersion(){

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("version");

        sharedPreferences = getSharedPreferences("version",Context.MODE_PRIVATE);


        storedVersion = sharedPreferences.getInt("version",0);

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                databaseVersion = dataSnapshot.getValue(Integer.class);

                if(databaseVersion>storedVersion){
                    try {
                        updateAIMLfiles();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SplashActivity.this, "Couldn't check for updates", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void updateAIMLfiles() throws IOException {
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReferenceFromUrl("gs://fit-bot-936cb.appspot.com").child("Fitbot.zip");
        sharedPreferencesEditor = sharedPreferences.edit();

        fileDirectory = new File(Environment.getExternalStorageDirectory().toString() + "/FITChatbot/bots/Fitbot");
        fileDirectory.mkdirs();

        downloadedFile = new File(fileDirectory.getPath(),"Fitbot.zip");

        mStorageReference.getFile(downloadedFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(SplashActivity.this, "File Downloaded", Toast.LENGTH_SHORT).show();


                        if (fileDirectory.canWrite()) {
                            final File backupDBFolder = new File(fileDirectory.getPath());
                            try {
                                unzip("Fitbot.zip", fileDirectory.getPath());
                                Toast.makeText(SplashActivity.this, "FileUnzipped", Toast.LENGTH_SHORT).show();
                                sharedPreferencesEditor.putInt("version",databaseVersion);
                                sharedPreferencesEditor.apply();
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(SplashActivity.this, "FileNotUnzipped", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(SplashActivity.this, "Download Failed", Toast.LENGTH_SHORT).show();
            }
        });
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
                                getDatabaseVersion();
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
