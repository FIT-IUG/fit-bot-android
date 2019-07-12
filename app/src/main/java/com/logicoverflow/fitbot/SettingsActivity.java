package com.logicoverflow.fitbot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
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
import com.logicoverflow.fitbot.Adapter.SettingsSlidePagerAdapter;
import com.logicoverflow.fitbot.Model.FirebaseFeedback;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.ByteArrayOutputStream;
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

    private FloatingActionButton feedback_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        sharedPreferences = getSharedPreferences(ChatActivity.THEME_PREFERENCES, Context.MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();

        final String theme = sharedPreferences.getString(ChatActivity.THEME_SAVED, ChatActivity.LIGHTTHEME);
        if (theme.equals(ChatActivity.LIGHTTHEME)) {
            setTheme(R.style.AppThemeLight);

        } else {
            setTheme(R.style.AppThemeDark);
        }

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
    public void onPositiveButtonClicked(int i, String s) {
        mDatabaseReference.child("feedBacks").push().setValue(new FirebaseFeedback(i , s));
        Toast.makeText(this, "تم التقييم بنجاح", Toast.LENGTH_SHORT).show();
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
