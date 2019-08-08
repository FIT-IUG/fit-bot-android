package com.logicoverflow.fit_bot;

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
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.logicoverflow.fit_bot.Util.AndroidAnimationBuilder;
import com.logicoverflow.fit_bot.Util.AppInternetStatus;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.logicoverflow.fit_bot.Util.ZipManager.unzip;

public class SplashActivity extends AppCompatActivity {

    private ImageView logo_image;
    private TextView logo_text;
    private TextView progress_text;
    private ProgressBar progressBar;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mDatabaseReference_2;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;
    private float storedVersion;
    private float databaseVersion;
    private boolean isInstalledBefore;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;
    private File fileDirectory;
    private File downloadedFile;
    private ValueEventListener versionValueEventListener;
    private ValueEventListener installationValueEventListener;

    boolean alreadyInChatActivity;

    private Thread timeout_thread;

    private boolean foundUpdate = false;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setUpUserAsAnonymous();

        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(getApplicationContext());
        setContentView(R.layout.activity_splash);

        alreadyInChatActivity = false;

        logo_image = findViewById(R.id.logo_image_in_splash_activity);
        logo_text = findViewById(R.id.logo_text_in_splash_activity);
        progressBar = findViewById(R.id.splash_progress_bar);
        progress_text = findViewById(R.id.progress_text);

        fileDirectory = new File(getFilesDir(),"/FITChatbot/bots/Fitbot");


         //String curr = getSharedPreferences(ChatActivity.THEME_PREFERENCES, MODE_PRIVATE).getString(ChatActivity.THEME_SAVED,ChatActivity.DEFTHEME);



        sharedPreferences = getSharedPreferences("version", Context.MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();
    }

    private void setUpUserAsAnonymous(){
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d("rmy", "signInWithCustomToken:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    Log.e("rmy",user.getIdToken(true).toString());
                } else {
                    Log.w("rmy", "signInWithCustomToken:failure", task.getException());
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fullScreen();
        animate();
       // storeDefaultAIMLfiles();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkDatabaseVersion();
            }
        },2000);

    }

    public void startTerminationTimer(){
        timeout_thread = new Thread() {
            @Override
            public void run() {
                try {
                    while(true) {
                        sleep(15000);
                        if(!foundUpdate){
                            startChatActivity();
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        timeout_thread.start();
    }


    private void checkDatabaseVersion() {

        startTerminationTimer();

        if (!AppInternetStatus.getInstance(SplashActivity.this).isOnline()) {
            progress_text.setText("حدث مشكلة في البحث عن تحديثات");
            if (sharedPreferences.getFloat("version", 0) == 0) {
                storeDefaultAIMLfiles();
            }else{
                startChatActivity();
            }
        } else {
            progress_text.setText("البحث عن تحديثات...");
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mDatabaseReference = mFirebaseDatabase.getReference("version");
            mDatabaseReference_2 = mFirebaseDatabase.getReference();

            storedVersion = sharedPreferences.getFloat("version", 0);
            isInstalledBefore = sharedPreferences.getBoolean("isInstall", false);

            installationValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String manufacturer = getPhoneManufacturer();
                    String model = getPhoneModel();
                    int numberOfinstallationBymodel =0;


                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-yyyy", Locale.ENGLISH);
                    String monthYear = simpleDateFormat.format(new Date());

                    DataSnapshot devices = dataSnapshot.child("devices").child(monthYear);

                    if(devices.hasChild(manufacturer)){

                        DataSnapshot corporation = devices.child(manufacturer);


                        if(corporation.hasChild(model)){

                            numberOfinstallationBymodel = corporation.child(model).getValue(Integer.class);
                        }else{

                            mDatabaseReference_2.child("devices").child(monthYear).child(manufacturer).child(model).setValue(1);

                        }


                    }else{

                        mDatabaseReference_2.child("devices").child(monthYear).child(manufacturer).child(model).setValue(1);

                    }

                    if (!isInstalledBefore) {
                        //uploadDeviceToFirebase();
                        ++numberOfinstallationBymodel;
                        sharedPreferencesEditor.putBoolean("isInstall", true);
                        sharedPreferencesEditor.apply();
                        sharedPreferencesEditor.commit();
                        mDatabaseReference_2.child("devices").child(monthYear).child(manufacturer)
                                .child(model).setValue(numberOfinstallationBymodel);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };

            mDatabaseReference_2.addListenerForSingleValueEvent(installationValueEventListener);

            versionValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getKey().equals("version")) {
                        databaseVersion = dataSnapshot.getValue(Float.class);
                        if (databaseVersion != storedVersion) {
                            progress_text.setText("يوجد تحديثات...");
                            try {
                                updateAIMLfiles();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            progress_text.setText("لا يوجد تحديثات");
                            startChatActivity();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progress_text.setText("حدث مشكلة في البحث عن تحديثات");
                    storeDefaultAIMLfiles();
                }
            };


            mDatabaseReference.addValueEventListener(versionValueEventListener);
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
        progress_text.setText("جار تحديث الملفات...");
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReferenceFromUrl("gs://fit-bot-936cb.appspot.com").child("Fitbot.zip");
        sharedPreferencesEditor = sharedPreferences.edit();


        fileDirectory.mkdirs();

        downloadedFile = new File(fileDirectory.getPath(), "Fitbot.zip");

        if(fileDirectory.listFiles()!=null){
            for (File child : fileDirectory.listFiles()) {
                if (!child.isDirectory()) {
                    FileUtils.forceDelete(child);
                }
            }
        }


        mStorageReference.getFile(downloadedFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        if (fileDirectory.canWrite()) {

                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    deletePreviousAIMLfiles();
                                        AsyncTask.execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    foundUpdate = true;
                                                    unzip("Fitbot.zip", fileDirectory.getPath());
                                                    sharedPreferencesEditor.putFloat("version", databaseVersion);
                                                    sharedPreferencesEditor.apply();
                                                    startChatActivity();
                                                } catch (IOException e1) {
                                                    e1.printStackTrace();
                                                    progress_text.setText("حدث مشكلة في تحديث الملفات");
                                                    //Toast.makeText(SplashActivity.this, "Error unzipping files", Toast.LENGTH_SHORT).show();
                                                    storeDefaultAIMLfiles();
                                                }
                                            }
                                        });


                                }
                            });


                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                progress_text.setText("حدث مشكلة في تنزيل التحديثات");
                storeDefaultAIMLfiles();
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
                        InputStream in = null;
                        OutputStream out = null;
                        in = assets.open("Fitbot/" + dir + "/" + file);
                        Log.e("rmy",fileDirectory.getPath() + "/" + dir + "/" + file);
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

            startChatActivity();
        }
    }

    public void deletePreviousAIMLfiles() {
        fileDirectory.delete();
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    public void startChatActivity() {
        timeout_thread = null;
        if (!alreadyInChatActivity) {
            Boolean it = sharedPreferences.getBoolean("tutorial", false);
            if (!it) {
                Intent intent = new Intent(SplashActivity.this, IntroActivity.class);
                startActivity(intent);
                finish();
                if (versionValueEventListener != null && installationValueEventListener != null) {
                    mDatabaseReference.removeEventListener(versionValueEventListener);
                    mDatabaseReference_2.removeEventListener(installationValueEventListener);
                }
                alreadyInChatActivity = true;
            }else {
                Intent intent = new Intent(SplashActivity.this, ChatActivity.class);
                startActivity(intent);
                finish();
                if (versionValueEventListener != null && installationValueEventListener != null) {
                    mDatabaseReference.removeEventListener(versionValueEventListener);
                    mDatabaseReference_2.removeEventListener(installationValueEventListener);
                }
                alreadyInChatActivity = true;
            }
        }
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

    public  String getPhoneManufacturer(){

        return Build.MANUFACTURER.toString();

    }

    public  String getPhoneModel(){

        return Build.MODEL.toString();

    }

}
