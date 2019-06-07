package com.logicoverflow.fitbot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
import com.logicoverflow.fitbot.Util.AppInternetStatus;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.logicoverflow.fitbot.Util.ZipManager.unzip;

public class SplashActivity extends AppCompatActivity {

    private ImageView logo_image;
    private TextView logo_text;
    private TextView progress_text;
    private ProgressBar progressBar;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;
    private int storedVersion;
    private int databaseVersion;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;
    private File fileDirectory;
    private File downloadedFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(getApplicationContext());
        setContentView(R.layout.activity_splash);

        logo_image = findViewById(R.id.logo_image_in_splash_activity);
        logo_text = findViewById(R.id.logo_text_in_splash_activity);
        progressBar = findViewById(R.id.splash_progress_bar);
        progress_text = findViewById(R.id.progress_text);

        fileDirectory = new File(Environment.getExternalStorageDirectory().toString() + "/FITChatbot/bots/Fitbot");

    }

    private void getDatabaseVersion() {

        sharedPreferences = getSharedPreferences("version", Context.MODE_PRIVATE);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if(progress_text.getText().toString().equals("Checking For Updates...")){
                    progress_text.setText("Couldn't check for updates / Saving default files");
                    if (sharedPreferences.getInt("version", 0) == 0) {
                        storeDefaultAIMLfiles();
                    }
                    startChatActivity();
                }

            }
        }, 8000);

        if (!AppInternetStatus.getInstance(SplashActivity.this).isOnline()) {
            progress_text.setText("Couldn't check for updates\nSaving default files");
            if (sharedPreferences.getInt("version", 0) == 0) {
                storeDefaultAIMLfiles();
            }
            startChatActivity();
        } else {
            progress_text.setText("Checking For Updates...");
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mDatabaseReference = mFirebaseDatabase.getReference("version");


            storedVersion = sharedPreferences.getInt("version", 0);



            mDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    databaseVersion = dataSnapshot.getValue(Integer.class);

                    if (databaseVersion > storedVersion) {
                        progress_text.setText("Found Updates");
                        try {
                            updateAIMLfiles();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        progress_text.setText("No Updates Found");
                        startChatActivity();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progress_text.setText("Couldn't check for updates / Saving default files");
                    storeDefaultAIMLfiles();
                    startChatActivity();
                }
            });
        }

    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    private void updateAIMLfiles() throws IOException {
        progress_text.setText("Downloading Updates...");
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReferenceFromUrl("gs://fit-bot-936cb.appspot.com").child("Fitbot.zip");
        sharedPreferencesEditor = sharedPreferences.edit();


        fileDirectory.mkdirs();

        downloadedFile = new File(fileDirectory.getPath(), "Fitbot.zip");

        for (File child : fileDirectory.listFiles()){
            if(!child.isDirectory()){
                FileUtils.forceDelete(child);
            }
        }

        mStorageReference.getFile(downloadedFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        if (fileDirectory.canWrite()) {

                            progress_text.setText("Unzipping File...");
                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    deletePreviousAIMLfiles();
                                    try {
                                        unzip("Fitbot.zip", fileDirectory.getPath());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        progress_text.setText("Error Unzipping File / Saving default files");
                                        Toast.makeText(SplashActivity.this, "Error unzipping files", Toast.LENGTH_SHORT).show();
                                        storeDefaultAIMLfiles();
                                        startChatActivity();
                                    }
                                    sharedPreferencesEditor.putInt("version", databaseVersion);
                                    sharedPreferencesEditor.apply();
                                    startChatActivity();
                                }
                            });


                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                progress_text.setText("Download Failed / Saving default files");
                storeDefaultAIMLfiles();
                startChatActivity();
            }
        });
    }

    public void storeDefaultAIMLfiles() {

        AssetManager assets = getResources().getAssets();


        boolean b = fileDirectory.mkdirs();
        if (fileDirectory.exists()) {

            //to delete files everytime app is loaded (in case of editting aiml files)
            deletePreviousAIMLfiles();

            //Reading the file
            try {
                for (String dir : assets.list("Fitbot")) {
                    File subdir = new File(fileDirectory.getPath() + "/" + dir);
                    boolean subdir_check = subdir.mkdirs();
                    for (String file : assets.list("Fitbot/" + dir)) {
                        File f = new File(fileDirectory.getPath() + "/" + dir + "/" + file);
                        InputStream in = null;
                        OutputStream out = null;
                        in = assets.open("Fitbot/" + dir + "/" + file);
                        out = new FileOutputStream(fileDirectory.getPath() + "/" + dir + "/" + file);
                        //copy file from assets to the mobile's SD card or any secondary memory
                        copyFile(in, out);
                        in.close();
                        in = null;
                        out.flush();
                        out.close();
                        out = null;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    public void deletePreviousAIMLfiles() {
        fileDirectory.delete();
//        for (String subdir : fileDirectory.list()) {
//            File dir = new File(fileDirectory + "/" + subdir);
//            for (String file : dir.list()) {
//                Log.e("rmy", new File(dir + "/" + file).delete() + "");
//            }
//        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    public void startChatActivity() {
        Intent intent = new Intent(SplashActivity.this, ChatActivity.class);
        startActivity(intent);
        finish();
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
                                getDatabaseVersion();
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
