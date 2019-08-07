package com.logicoverflow.fit_bot;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.cuneytayyildiz.onboarder.OnboarderActivity;
import com.cuneytayyildiz.onboarder.OnboarderPage;
import com.cuneytayyildiz.onboarder.utils.OnboarderPageChangeListener;

import java.util.ArrayList;
import java.util.List;


public class IntroActivity extends OnboarderActivity implements OnboarderPageChangeListener {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = new Intent(IntroActivity.this, ChatActivity.class);
        sharedPreferences = getSharedPreferences("version" , Context.MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();

        makeTutorial();

    }

    @Override
    public void onFinishButtonPressed() {

        sharedPreferencesEditor.putBoolean("tutorial", true);
        sharedPreferencesEditor.apply();


        startActivity(intent);
        finish();


    }

    @Override
    protected void onSkipButtonPressed() {

        sharedPreferencesEditor.putBoolean("tutorial", true);
        sharedPreferencesEditor.apply();

        startActivity(intent);
        finish();
    }

    @Override
    public void onPageChanged(int position) {

    }

    public void makeTutorial(){
        List<OnboarderPage> list = new ArrayList<>();

        OnboarderPage page_1 = new OnboarderPage.Builder()
                .title("مرحبا بك")
                .description("اهلا وسهلا بك في المساعد الالي لكلية تكنولوجيا المعلومات و الذي نسعى من خلاله للاجابة على اكبر قدر ممكن من الاستفسارات , اذا اردت استكشاف التطبيق لوحدك اضغط على تخطي او اضغط التالي لنستكشف التطبيق معا 😃 ")
                .imageResourceId(R.drawable.ic_conversation)
                .backgroundColorId(R.color.background_color)
                .titleColorId(R.color.white)
                .imageSizeDp(200,200)
                .imageBias(1)
                .descriptionTextSize(25)
                .textPaddingBottomDp(80)
                .descriptionColorId(R.color.white)
                .multilineDescriptionCentered(true)
                .build();

        OnboarderPage page_2 = new OnboarderPage.Builder()
                .title("")
                .description("بالضغط على هذه الايقونة يمكنك معرفة انماط الاسئلة المطروحة وتصنيفاتها 📝")
                .imageResourceId( R.drawable.tutorial_guide_settings_info)
                .backgroundColorId(R.color.background_color)
                .descriptionColorId(R.color.white).textPaddingBottomDp(200)
                .descriptionTextSize(30).imageBias(1)
                .multilineDescriptionCentered(true)
                .build();

        OnboarderPage page_3 = new OnboarderPage.Builder()
                .title("")
                .description("اضغط على هذه الايقونة للدخول الى صفحة الاعدادت الخاصة بالتطبيق ⚙")
                .imageResourceId( R.drawable.tutorial_guide_settings_button)
                .backgroundColorId(R.color.background_color)
                .descriptionColorId(R.color.white)
                .textPaddingBottomDp(200)
                .descriptionTextSize(30).imageBias(1)
                .multilineDescriptionCentered(true)
                .build();

        OnboarderPage page_4 = new OnboarderPage.Builder()
                .title("")
                .description("في حال اردت تقييم التطبيق اضغط على هذه الايقونة المشار اليها بالسهم🎖")
                .imageResourceId( R.drawable.tutorial_guide_settings_rating)
                .backgroundColorId(R.color.background_color)
                .descriptionColorId(R.color.white)
                .textPaddingBottomDp(100)
                .descriptionTextSize(30).imageBias(1)
                .multilineDescriptionCentered(true)
                .build();


        OnboarderPage page_5 = new OnboarderPage.Builder()
                .description("في حال اردت التبليغ عن مشكلة في الرد عن احد استفساراتك الرجاء الضغط مطولا على الرد ومن ثم اضغط التبليغ عن خطا 🚩")
                .imageResourceId( R.drawable.tutorial_guide_chat_message)
                .backgroundColorId(R.color.background_color)
                .descriptionColorId(R.color.white)
                .textPaddingBottomDp(100)
                 .descriptionTextSize(30)
                .imageBias(1)
                .multilineDescriptionCentered(true)
                .build();

        list.add(page_1);
        list.add(page_2);
        list.add(page_3);
        list.add(page_4);
        list.add(page_5);

        setSkipButtonTitle("تخطي");
        setFinishButtonTitle("فهمت");
        setNextButtonTitle( "التالي");
        setOnboarderPageChangeListener(this);
        initOnboardingPages(list);

    }

}