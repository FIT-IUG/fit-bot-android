package com.logicoverflow.fit_bot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.logicoverflow.fit_bot.Adapter.ChatMessageAdapter;
import com.logicoverflow.fit_bot.BroadcastReceivers.InternetBroadcastReceiver;
import com.logicoverflow.fit_bot.Database.DbHelper;
import com.logicoverflow.fit_bot.Model.ChatMessage;
import com.logicoverflow.fit_bot.Model.FirebaseFeedback;
import com.logicoverflow.fit_bot.Model.FirebaseMessage;
import com.logicoverflow.fit_bot.Model.FirebaseReport;
import com.logicoverflow.fit_bot.Util.AppInternetStatus;
import com.logicoverflow.fit_bot.Util.Const;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.alicebot.ab.AIMLProcessor;
import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.ab.Graphmaster;
import org.alicebot.ab.MagicBooleans;
import org.alicebot.ab.MagicNumbers;
import org.alicebot.ab.MagicStrings;
import org.alicebot.ab.PCAIMLProcessorExtension;
import org.alicebot.ab.Timer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class ChatActivity extends AppCompatActivity implements RatingDialogListener {


    private ListView mListView;
    private FloatingActionButton mButtonSend;
    private ImageView guideLine;
    private ImageView dismiss_guide_button;
    private static ImageView connectivity_circle;
    private EditText mEditTextMessage;
    public static Bot bot;
    public static Chat chat;
    private ChatMessageAdapter mAdapter;
    private static ArrayList<FirebaseMessage> messageLogArrayList;
    private static ArrayList<FirebaseFeedback> feedbackLogArrayList;
    private static ArrayList<FirebaseReport> reportLogArrayList;
    private WebView guideWebView;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private InternetBroadcastReceiver myReceiver ;
    private SharedPreferences sharedPreferences_log;
    private SharedPreferences.Editor sharedPreferencesEditor_log;
    private SharedPreferences sharedPreferences_theme;
    private SharedPreferences.Editor sharedPreferencesEditor_theme;

    private SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener;

    private DialogPlus guideDialog;

    private static File filesDirectory;
    private static File guideFileDirectory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        filesDirectory = new File(getFilesDir() + "/FITChatbot");
        guideFileDirectory = new File(filesDirectory + "/bots/Fitbot/guide.html");

        startBot();


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        sharedPreferences_log = getSharedPreferences("LOG", MODE_PRIVATE);
        sharedPreferencesEditor_log = sharedPreferences_log.edit();


        sharedPreferences_theme = getSharedPreferences(Const.THEME_PREFERENCES, MODE_PRIVATE);
        String currentTheme = sharedPreferences_theme.getString(Const.THEME_SAVED, Const.DEFTHEME);

        //Toast.makeText(this,currentTheme , Toast.LENGTH_LONG).show();

        //Log.e("rmy","Current Theme:"+currentTheme);

        if (currentTheme.equals(Const.DEFTHEME)) {
            //Log.e("rmy","First If");
            setTheme(R.style.DefaultTheme);

            //Toast.makeText(this, "1", Toast.LENGTH_LONG).show();

        } else if (currentTheme.equals(Const.DARKBLUETHEME)) {
            //Log.e("rmy","Second If");
            setTheme(R.style.DarkBlueTheme);
            //Toast.makeText(this,"2" , Toast.LENGTH_LONG).show();

        } else if (currentTheme.equals(Const.DARKTHEME)) {
            //Log.e("rmy","Third If");
            setTheme(R.style.DarkTheme);
            //Toast.makeText(this,"3" , Toast.LENGTH_LONG).show();

        } else {
            //Log.e("rmy","Else");
            //Toast.makeText(this,currentTheme+" "+ChatActivity.DARKBLUETHEME , Toast.LENGTH_LONG).show();

        }


        setContentView(R.layout.activity_chat);

        ScrollView sv = findViewById(R.id.scroll);
        sv.setEnabled(false);

        myReceiver = new InternetBroadcastReceiver();
        mListView = findViewById(R.id.listView);
        guideLine = findViewById(R.id.guideline_imageView);
        mButtonSend = findViewById(R.id.btn_send);
        mEditTextMessage = findViewById(R.id.et_message);
        //connectivity_text = findViewById(R.id.toolbar_connectivity_text);
        connectivity_circle = findViewById(R.id.toolbar_connectivity_circle);
        Toolbar actionToolbar = findViewById(R.id.toolbar_chat_activity);
        setSupportActionBar(actionToolbar);

        if (!AppInternetStatus.getInstance(ChatActivity.this).isOnline()) {
            connectivity_circle.setImageResource(R.drawable.offline_circle);
            //connectivity_text.setText("Offline");
        } else {
            connectivity_circle.setImageResource(R.drawable.online_circle);
        }

        //max same responses allowed
        MagicNumbers.repetition_count = 100;


        mEditTextMessage.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mEditTextMessage.setRawInputType(InputType.TYPE_CLASS_TEXT);

        mAdapter = new ChatMessageAdapter(this, new ArrayList<ChatMessage>());
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                hideSoftKeyboard(ChatActivity.this);
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (position % 2 == 0) {
                    showReportDialog(new FirebaseReport(mAdapter.getItem(position).getContent(),
                            mAdapter.getItem(position + 1).getContent()));
                } else {
                    showReportDialog(new FirebaseReport(mAdapter.getItem(position - 1).getContent(),
                            mAdapter.getItem(position).getContent()));
                }
                return false;
            }
        });

        MagicStrings.default_bot_response = "هل بامكانك صياغة السؤال مرة اخرى؟";

        mEditTextMessage.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    sendMessageButton();
                    return true;
                }
                return false;
            }
        });

        mEditTextMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapter.notifyDataSetChanged();
            }
        });

        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageButton();
            }
        });


        onSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                recreate();
            }
        };
        sharedPreferences_theme.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);


        guideLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showGuide();

            }
        });

    }


    public static void checkConnectivity(Context context) {
        if (!AppInternetStatus.getInstance(context).isOnline()) {
            connectivity_circle.setImageResource(R.drawable.offline_circle);
        } else {
            connectivity_circle.setImageResource(R.drawable.online_circle);
        }
    }


    private void sendMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, true, false);
        mAdapter.add(chatMessage);

        //mimicOtherMessage(message);
    }

    private void mimicOtherMessage(String message) {
        ChatMessage chatMessage;
        if (message.contains("gif")) {
            message = message.replace("gif", "");
            chatMessage = new ChatMessage(message, false, true);
        } else {
            chatMessage = new ChatMessage(message, false, false);
        }

        mAdapter.add(chatMessage);
    }


    //Request and response of user and the bot
    public static void startBot() {

        //get the working directory
        MagicStrings.root_path = filesDirectory.getPath();

        System.out.println("Working Directory = " + MagicStrings.root_path);
        AIMLProcessor.extension = new PCAIMLProcessorExtension();
        //Assign the AIML files to bot for processing
        bot = new Bot("Fitbot", MagicStrings.root_path, "chat");
        chat = new Chat(bot);
        bot.writeAIMLIFFiles();


        MagicBooleans.trace_mode = false;
        System.out.println("trace mode = " + MagicBooleans.trace_mode);
        Graphmaster.enableShortCuts = true;
        //String request = "Hello.";
        //String response = chat.multisentenceRespond(request);

        //System.out.println("Human: " + request);
        //System.out.println("Robot: " + response);
    }

    public void sendMessageButton() {

        ChatMessage chatMessage = new ChatMessage();

        String message = mEditTextMessage.getText().toString();

        chatMessage.setContent(message);
        chatMessage.setIsImage(false);
        chatMessage.setIsMine(true);
        DbHelper.getInstance(this).insertMessage(chatMessage);


        String formattedMessage = formatRepetition(message);

        formattedMessage = formattedMessage.replace("؟", "");
        formattedMessage = formattedMessage.replace("?", "");
        //bot
        String response = getBotResponse(formattedMessage);
        if (TextUtils.isEmpty(message)) {
            return;
        }

        sendMessage(message);
        mimicOtherMessage(response);

        chatMessage.setContent(response);
        chatMessage.setIsImage(false);
        chatMessage.setIsMine(false);
        DbHelper.getInstance(this).insertMessage(chatMessage);

        //Upload messages to firebase
        boolean isAnswered = true;
        if (response.equals(MagicStrings.default_bot_response)) {
            isAnswered = false;
        }

        //mDatabaseReference.child("messages").push().setValue(new FirebaseMessage(message, response, isAnswered));
        saveMessageToLog(new FirebaseMessage(message, response, isAnswered));

        //
        mEditTextMessage.setText("");
        mListView.setSelection(mAdapter.getCount() - 1);


    }

    public String getBotResponse(String message) {
        return chat.multisentenceRespond(message);
    }

    public static void hideSoftKeyboard(Activity activity) {

        try {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) activity.getSystemService(
                            Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            Log.e("rmy", e.getMessage());
        }

    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            Animatoo.animateSlideLeft(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(myReceiver ,filter);

        mAdapter = new ChatMessageAdapter(this, DbHelper.getInstance(this).getAllMessages());
        mListView.setAdapter(mAdapter);

        mListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        mListView.setStackFromBottom(true);

        setBackgroundImage();
        loadMessageLog();
        loadFeedbackLog();
        loadReportLog();
    }

    public void setBackgroundImage() {
        String backgroundString = getSharedPreferences(Const.THEME_PREFERENCES, MODE_PRIVATE).getString(Const.BACKGROUND_SAVED, Const.LIGHTTHEME);
        byte[] imageAsBytes = Base64.decode(backgroundString.getBytes(), Base64.DEFAULT);
        //RelativeLayout chatBackground = findViewById(R.id.chatBackground);
        ImageView background_image = findViewById(R.id.background_image);
        Bitmap backgroundBitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
        BitmapDrawable backgroundBitmapDrawable = new BitmapDrawable(getResources(), backgroundBitmap);
        //chatBackground.setBackground(backgroundBitmapDrawable);
        background_image.setImageDrawable(backgroundBitmapDrawable);
    }

    private void showReportDialog(final FirebaseReport firebaseReport) {

        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("مشكلة في هذه الرسالة؟")
                .setConfirmText("تبليغ عن خطأ").setCancelButton("الغاء", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation();
            }
        }).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                //mDatabaseReference.child("reports").push().setValue(firebaseReport);
                saveReportToLog(firebaseReport);
                sweetAlertDialog.dismissWithAnimation();
                Toast.makeText(ChatActivity.this, "تم الابلاغ عن الرسالة بنجاح", Toast.LENGTH_SHORT).show();
            }
        })
                .show();

    }

    @Override
    public void onBackPressed() {

        if (guideDialog != null && guideDialog.isShowing()) {
            guideDialog.dismiss();
        } else {
            Boolean rating = sharedPreferences_log.getBoolean("isRating", false);

            if (!rating) {

                showFeedbackDialog();
            } else {

                super.onBackPressed();
            }
        }


    }

    private void showFeedbackDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("تقييم")
                .setNeutralButtonText("التقييم لاحقا")
                .setNegativeButtonText("عدم الاظهار مرة اخرى")
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
                .create(ChatActivity.this)
                .show();
    }

    @Override
    public void onNegativeButtonClicked() {

        sharedPreferencesEditor_log.putBoolean("isRating", true);
        sharedPreferencesEditor_log.apply();
        sharedPreferencesEditor_log.commit();
        finish();
    }

    @Override
    public void onNeutralButtonClicked() {

        ChatActivity.super.onBackPressed();

    }

    @Override
    public void onPositiveButtonClicked(int rating, String feedback) {

        saveFeedbackToLog(new FirebaseFeedback(rating, feedback));
        //databaseReference.child("feedbacks").push().setValue(new FirebaseFeedback(i, s));
        Toast.makeText(this, "تم التقييم بنجاح", Toast.LENGTH_SHORT).show();

        sharedPreferencesEditor_log.putBoolean("isRating", true);
        sharedPreferencesEditor_log.apply();
        sharedPreferencesEditor_log.commit();
        ChatActivity.super.onBackPressed();
    }

    public void loadMessageLog() {
        messageLogArrayList = new ArrayList<>();
        Gson gson = new Gson();
        String json = sharedPreferences_log.getString("MessageLog", null);
        Type type = new TypeToken<ArrayList<FirebaseMessage>>() {
        }.getType();
        messageLogArrayList = gson.fromJson(json, type);
        if (messageLogArrayList == null) {
            messageLogArrayList = new ArrayList<FirebaseMessage>();
        }
    }


    public void saveMessageToLog(FirebaseMessage firebaseMessage) {
        messageLogArrayList.add(firebaseMessage);
        Gson gson = new Gson();
        String json = gson.toJson(messageLogArrayList);
        sharedPreferencesEditor_log.putString("MessageLog", json);
        sharedPreferencesEditor_log.commit();
        sharedPreferencesEditor_log.apply();
    }

    public void clearMessageLog() {
        if (messageLogArrayList != null) {
            messageLogArrayList.clear();
        }

        sharedPreferencesEditor_log.remove("MessageLog");
        sharedPreferencesEditor_log.commit();
        sharedPreferencesEditor_log.apply();

    }

    public void loadFeedbackLog() {
        if (feedbackLogArrayList == null) {
            feedbackLogArrayList = new ArrayList<FirebaseFeedback>();
        }
        Gson gson = new Gson();
        String json = sharedPreferences_log.getString("FeedbackLog", null);
        Type type = new TypeToken<ArrayList<FirebaseFeedback>>() {
        }.getType();
        feedbackLogArrayList = gson.fromJson(json, type);

        if (feedbackLogArrayList == null) {
            feedbackLogArrayList = new ArrayList<FirebaseFeedback>();
        }

    }


    public void saveFeedbackToLog(FirebaseFeedback firebaseFeedback) {
        feedbackLogArrayList.add(firebaseFeedback);
        Gson gson = new Gson();
        String json = gson.toJson(feedbackLogArrayList);
        sharedPreferencesEditor_log.putString("FeedbackLog", json);
        sharedPreferencesEditor_log.commit();
        sharedPreferencesEditor_log.apply();
    }

    public void clearFeedbackLog() {
        if (feedbackLogArrayList != null) {
            feedbackLogArrayList.clear();
        }

        sharedPreferencesEditor_log.remove("FeedbackLog");
        sharedPreferencesEditor_log.commit();
        sharedPreferencesEditor_log.apply();

    }

    public void loadReportLog() {
        if (reportLogArrayList == null) {
            reportLogArrayList = new ArrayList<FirebaseReport>();
        }
        Gson gson = new Gson();
        String json = sharedPreferences_log.getString("ReportLog", null);
        Type type = new TypeToken<ArrayList<FirebaseReport>>() {
        }.getType();
        reportLogArrayList = gson.fromJson(json, type);

        if (reportLogArrayList == null) {
            reportLogArrayList = new ArrayList<FirebaseReport>();
        }

    }


    public void saveReportToLog(FirebaseReport firebaseReport) {
        reportLogArrayList.add(firebaseReport);
        Gson gson = new Gson();
        String json = gson.toJson(reportLogArrayList);
        sharedPreferencesEditor_log.putString("ReportLog", json);
        sharedPreferencesEditor_log.commit();
        sharedPreferencesEditor_log.apply();
    }

    public void clearReportLog() {
        if (reportLogArrayList != null) {
            reportLogArrayList.clear();
        }

        sharedPreferencesEditor_log.remove("ReportLog");
        sharedPreferencesEditor_log.commit();
        sharedPreferencesEditor_log.apply();

    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(myReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
         uploadMessagesToFirebase();
         uploadFeedbackToFirebase();
         uploadReportsToFirebase();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.e("rmy", DbHelper.getInstance(this).saveLastNMessages() + "");
    }

    boolean uploadedSuccessfully = true;
    int position;

    public void uploadMessagesToFirebase() {
        uploadedSuccessfully = true;
        position = messageLogArrayList.size() - 1;
        while (position != -1) {
            mDatabaseReference.child("messages").push().setValue(messageLogArrayList.get(position))
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            uploadedSuccessfully = false;
                        }
                    });
            if (!uploadedSuccessfully) {
                break;
            }

            position--;
        }
        if (uploadedSuccessfully) {
            clearMessageLog();
        }
    }

    public void uploadFeedbackToFirebase() {
        uploadedSuccessfully = true;
        position = feedbackLogArrayList.size() - 1;

        while (position != -1) {
            mDatabaseReference.child("feedbacks").push().setValue(feedbackLogArrayList.get(position)).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    uploadedSuccessfully = false;
                }
            });
            if (!uploadedSuccessfully) {
                break;
            }

            position--;
        }
        if (uploadedSuccessfully) {
            clearFeedbackLog();
        }
    }

    public void uploadReportsToFirebase() {
        uploadedSuccessfully = true;
        position = reportLogArrayList.size() - 1;

        while (position != -1) {
            mDatabaseReference.child("reports").push().setValue(reportLogArrayList.get(position)).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    uploadedSuccessfully = false;
                }
            });
            if (!uploadedSuccessfully) {
                break;
            }

            position--;
        }
        if (uploadedSuccessfully) {
            clearReportLog();
        }
    }

    public void showGuide() {

        hideSoftKeyboard(ChatActivity.this);

//        Intent guideIntent = new Intent(ChatActivity.this,GuideActivity.class);
//
//        startActivityForResult(guideIntent,Const.GUIDE_INTENT_CODE);

        if (!guideFileDirectory.exists()) {
            try {
                copyFile(getResources().getAssets().open("guide.html"), new FileOutputStream(guideFileDirectory));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        guideDialog = DialogPlus.newDialog(this)
                .setContentHolder(new ViewHolder(R.layout.guide_layout))
                .setGravity(Gravity.BOTTOM)
                //.setMargin(100, 200, 100, 200)
                .setCancelable(true)
                .create();
        guideDialog.show();

        ImageView back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        guideWebView = findViewById(R.id.guideWebView);
//        dismiss_guide_button = findViewById(R.id.dismiss_guide_button);
//
//        dismiss_guide_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (guideDialog.isShowing()) {
//                    guideDialog.dismiss();
//                }
//            }
//        });

        try {
            guideWebView.loadUrl("file:///" + guideFileDirectory);

            guideWebView.loadUrl("file:///" + guideFileDirectory);
            guideWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {

                    String[] a = url.split("Fitbot/");
                    mEditTextMessage.setText(decodeValue(a[1]));
                    guideDialog.dismiss();

                    return super.shouldOverrideUrlLoading(view, url);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String decodeValue(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile(File fl) throws Exception {
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    public String formatRepetition(String message) {

        String regex = "(\\p{InARABIC})(\\1+)";
        Pattern r = Pattern.compile(regex);
        Matcher m = r.matcher(message);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, m.group(1) + m.group(1));
        }
        m.appendTail(sb);

        return sb.toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (Const.GUIDE_INTENT_CODE):
                try {
                    mEditTextMessage.setText(data.getStringExtra(Const.GUIDE_MESSAGE_TEXT));
                } catch (Exception e) {
                }
        }
    }
}
