package com.logicoverflow.fitbot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.logicoverflow.fitbot.Adapter.ChatMessageAdapter;
import com.logicoverflow.fitbot.Model.ChatMessage;
import com.logicoverflow.fitbot.Model.FirebaseMessage;
import com.logicoverflow.fitbot.Model.FirebaseReport;
import com.logicoverflow.fitbot.Util.AppInternetStatus;
import org.alicebot.ab.AIMLProcessor;
import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.ab.Graphmaster;
import org.alicebot.ab.MagicBooleans;
import org.alicebot.ab.MagicNumbers;
import org.alicebot.ab.MagicStrings;
import org.alicebot.ab.PCAIMLProcessorExtension;
import org.alicebot.ab.Timer;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import cn.pedant.SweetAlert.SweetAlertDialog;


public class ChatActivity extends AppCompatActivity {

    public static final String THEME_PREFERENCES = "THEME_PREFERENCES";
    public static final String THEME_SAVED = "THEME_SAVED";
    public static final String BACKGROUND_SAVED = "BACKGROUND_SAVED";
    public static final String LIGHTTHEME = "LIGHTTHEME";
    public static final String DARKTHEME = "DARKTHEME";

    private ListView mListView;
    private FloatingActionButton mButtonSend;
    private static TextView connectivity_text;
    private static ImageView connectivity_circle;
    private EditText mEditTextMessage;
    public Bot bot;
    public static Chat chat;
    private ChatMessageAdapter mAdapter;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private  DatabaseReference mDatabaseReferemce_2;

    private SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        String theme = getSharedPreferences(ChatActivity.THEME_PREFERENCES, MODE_PRIVATE).getString(ChatActivity.THEME_SAVED, ChatActivity.LIGHTTHEME);
        if (theme.equals(ChatActivity.LIGHTTHEME)) {
            setTheme(R.style.AppThemeLight);
        } else {
            setTheme(R.style.AppThemeDark);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ScrollView sv = findViewById(R.id.scroll);
        sv.setEnabled(false);

        mListView = findViewById(R.id.listView);
        mButtonSend = findViewById(R.id.btn_send);
        mEditTextMessage = findViewById(R.id.et_message);
        connectivity_text = findViewById(R.id.toolbar_connectivity_text);
        connectivity_circle = findViewById(R.id.toolbar_connectivity_circle);
        Toolbar actionToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(actionToolbar);

        if (!AppInternetStatus.getInstance(ChatActivity.this).isOnline()) {
            connectivity_circle.setImageResource(R.drawable.offline_circle);
            connectivity_text.setText("Offline");
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
                if(position%2==0){
                    showReportDialog(new FirebaseReport(mAdapter.getItem(position).getContent(),mAdapter.getItem(position+1).getContent()) );
                    Toast.makeText(ChatActivity.this, "usermessage position "+position, Toast.LENGTH_SHORT).show();
                }else{
                    showReportDialog(new FirebaseReport(mAdapter.getItem(position-1).getContent(),mAdapter.getItem(position).getContent()) );
                    Toast.makeText(ChatActivity.this, "chatbot position "+position, Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        MagicStrings.default_bot_response = "مش فاهم عليك, بتقدر تعيد؟";

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


        //get the working directory
        MagicStrings.root_path = Environment.getExternalStorageDirectory().toString() + "/FITChatbot";
        System.out.println("Working Directory = " + MagicStrings.root_path);
        AIMLProcessor.extension = new PCAIMLProcessorExtension();
        //Assign the AIML files to bot for processing
        bot = new Bot("Fitbot", MagicStrings.root_path, "chat");
        chat = new Chat(bot);
        bot.writeAIMLIFFiles();
        String[] args = null;

        mainFunction(args);


        onSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                recreate();
            }
        };
        getSharedPreferences(ChatActivity.THEME_PREFERENCES, MODE_PRIVATE).registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);


    }

    public static void checkConnectivity(Context context) {
        if (!AppInternetStatus.getInstance(context).isOnline()) {
            connectivity_circle.setImageResource(R.drawable.offline_circle);
            connectivity_text.setText("Offline");
            Toast.makeText(context, "offline", Toast.LENGTH_SHORT).show();
        } else {
            connectivity_circle.setImageResource(R.drawable.online_circle);
            connectivity_text.setText("Online");
            Toast.makeText(context, "online", Toast.LENGTH_SHORT).show();
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
    public static void mainFunction(String[] args) {
        MagicBooleans.trace_mode = false;
        System.out.println("trace mode = " + MagicBooleans.trace_mode);
        Graphmaster.enableShortCuts = true;
        Timer timer = new Timer();
        String request = "Hello.";
        String response = chat.multisentenceRespond(request);

        System.out.println("Human: " + request);
        System.out.println("Robot: " + response);
    }

    public void sendMessageButton() {
        String message = mEditTextMessage.getText().toString();
        message = message.replace("؟", " ");
        message = message.replace("?", " ");
        //bot
        String response = chat.multisentenceRespond(message);
        if (TextUtils.isEmpty(message)) {
            return;
        }
        sendMessage(mEditTextMessage.getText().toString());
        mimicOtherMessage(response);

        //Upload messages to firebase
        boolean isAnswered = true;
        if (response.equals(MagicStrings.default_bot_response)) {
            isAnswered = false;
        }

        mDatabaseReference.child("messages").push().setValue(new FirebaseMessage(message, response, isAnswered))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Toast.makeText(ChatActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(ChatActivity.this, "Failed: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //
        mEditTextMessage.setText("");
        mListView.setSelection(mAdapter.getCount() - 1);


    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
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
        setBackgroundImage();
    }

    public void setBackgroundImage() {
        String backgroundString = getSharedPreferences(ChatActivity.THEME_PREFERENCES, MODE_PRIVATE).getString(ChatActivity.BACKGROUND_SAVED, ChatActivity.LIGHTTHEME);
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
                .setTitleText("Report about this message ?")
                .setConfirmText("Report").setCancelButton("cancle", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation();
            }
        }).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        mDatabaseReference.child("reports").push().setValue(firebaseReport);
                        sweetAlertDialog.dismissWithAnimation();
                    }
                })
                .show();

    }

}
