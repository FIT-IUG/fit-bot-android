package com.logicoverflow.fit_bot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.gauravk.bubblenavigation.BubbleNavigationConstraintView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.logicoverflow.fit_bot.Adapter.SettingsSlidePagerAdapter;
import com.logicoverflow.fit_bot.Model.FirebaseFeedback;
import com.logicoverflow.fit_bot.Util.Const;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

public class SettingsActivity extends AppCompatActivity implements RatingDialogListener , IPickResult {

    private static final int NUM_PAGES = 5;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter pagerAdapter;
    private BubbleNavigationConstraintView floating_top_bar_navigation;
    private ImageView back_button;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;
    private SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener;
    private SharedPreferences sharedPreferences_log;
    private SharedPreferences.Editor sharedPreferencesEditor_log;

    private FloatingActionButton feedback_button;

    private ArrayList<FirebaseFeedback> feedbackLogArrayList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {


                finish();
                overridePendingTransition(0, 0);
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);

            }
        };

        sharedPreferences = getSharedPreferences(Const.THEME_PREFERENCES, Context.MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
        String currentTheme = sharedPreferences.getString(Const.THEME_SAVED , Const.DEFTHEME);


        if(currentTheme.equals(Const.DEFTHEME)){

            setTheme(R.style.DefaultTheme);

        }else if(currentTheme.equals(Const.DARKBLUETHEME)){

            setTheme(R.style.DarkBlueTheme);

        }else if(currentTheme.equals(Const.DARKTHEME)){

            setTheme(R.style.DarkTheme);
        }



        sharedPreferences_log = getSharedPreferences("LOG",MODE_PRIVATE);
        sharedPreferencesEditor_log = sharedPreferences_log.edit();

        loadFeedbackLog();

        setContentView(R.layout.activity_settings);

        feedback_button = findViewById(R.id.feedback_button);

        mPager = findViewById(R.id.view_pager);
        pagerAdapter = new SettingsSlidePagerAdapter(getSupportFragmentManager(),0,2);
        mPager.setAdapter(pagerAdapter);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        floating_top_bar_navigation = findViewById(R.id.floating_top_bar_navigation);



        floating_top_bar_navigation.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
                mPager.setCurrentItem(position);
            }
        });

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                floating_top_bar_navigation.setCurrentActiveItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        floating_top_bar_navigation.setCurrentActiveItem(1);
        mPager.setCurrentItem(1);


        feedback_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFeedbackDialog();
            }
        });

        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(this, ChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        super.onBackPressed();
        Animatoo.animateSlideRight(this);
    }

    private void showFeedbackDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("تقييم")
                .setNeutralButtonText("الغاء")
                .setNoteDescriptions(Arrays.asList("سيء جدا", "ليس جيدا", "مقبول", "جيد جدا", "ممتاز"))
                .setDefaultRating(3)
                .setTitle("قيم هذا التطبيق")
                .setDescription("كم نجمة يستحق التطبيق برايك؟")
                .setCommentInputEnabled(true)
                .setStarColor(R.color.starColor)
                .setNoteDescriptionTextColor(R.color.noteDescriptionTextColor)
                .setTitleTextColor(R.color.titleTextColor)
                .setDescriptionTextColor(R.color.contentTextColor)
                .setHint("اذا كان لديك اي تعليق الرجاء كتابته هنا...")
                .setHintTextColor(R.color.hintTextColor)
                .setCommentTextColor(R.color.commentTextColor)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setWindowAnimation(R.style.MyDialogFadeAnimation)
                .setCancelable(false)
                .setCanceledOnTouchOutside(true)
                .create(SettingsActivity.this)
                .show();
    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onNeutralButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(int rating, String feedback) {
        saveFeedbackToLog(new FirebaseFeedback(rating,feedback));
        //mDatabaseReference.child("feedbacks").push().setValue(new FirebaseFeedback(i , s));
        Toast.makeText(this, "تم التقييم بنجاح", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPickResult(PickResult pickResult) {
        sharedPreferencesEditor.putString(Const.BACKGROUND_SAVED, bitmapToString(pickResult.getBitmap()));
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


    public void loadFeedbackLog(){
        if(feedbackLogArrayList == null){
            feedbackLogArrayList = new ArrayList<>();
        }

        Gson gson = new Gson();
        String json = sharedPreferences_log.getString("FeedbackLog", null);
        Type type = new TypeToken<ArrayList<FirebaseFeedback>>() {}.getType();
        feedbackLogArrayList =  gson.fromJson(json, type);
        if(feedbackLogArrayList==null){
            feedbackLogArrayList = new ArrayList<FirebaseFeedback>();
        }
    }

    public void saveFeedbackToLog(FirebaseFeedback firebaseFeedback){
        feedbackLogArrayList.add(firebaseFeedback);
        Gson gson = new Gson();
        String json = gson.toJson(feedbackLogArrayList);
        sharedPreferencesEditor_log.putString("FeedbackLog", json);
        sharedPreferencesEditor_log.commit();
        sharedPreferencesEditor_log.apply();
    }




}
