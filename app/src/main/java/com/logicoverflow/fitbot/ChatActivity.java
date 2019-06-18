package com.logicoverflow.fitbot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.logicoverflow.fitbot.Adapter.ChatMessageAdapter;
import com.logicoverflow.fitbot.Model.ChatMessage;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ChatActivity extends AppCompatActivity {

    private ListView mListView;
    private Button mButtonSend;
    private static TextView connectivity_text;
    private static ImageView connectivity_circle;
    private EditText mEditTextMessage;
    public Bot bot;
    public static Chat chat;
    private ChatMessageAdapter mAdapter;
    private View pull_down_menu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        //toolbar.inflateMenu(R.menu.menu);
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

        //pull_down_menu = findViewById(R.id.pull_down_menu);
       // final View toolbar = findViewById(R.id.toolbar);

        //final Animation slide_down_animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down_animation);
       // final Animation slide_up_animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up_animation);

//        toolbar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (pull_down_menu.isShown()) {
//                    pull_down_menu.startAnimation(slide_up_animation);
//                    pull_down_menu.setVisibility(View.INVISIBLE);
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        new Handler().postDelayed(new Runnable() {
//
//                            @Override
//                            public void run() {
//                                toolbar.setBackground(getDrawable(R.drawable.toolbar_shape_rounded_corners));
//                            }
//                        }, 200);
//
//                    }
//                } else {
//                    pull_down_menu.startAnimation(slide_down_animation);
//                    pull_down_menu.setVisibility(View.VISIBLE);
//
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        new Handler().postDelayed(new Runnable() {
//
//                            @Override
//                            public void run() {
//                                toolbar.setBackground(getDrawable(R.drawable.toolbar_shape_straight_corners_primarycolor));
//                            }
//                        }, 300);
//
//                    }
//                }
//            }
//        });


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
            Intent settingsIntent = new Intent(this, SettingActivity.class);
            startActivity(settingsIntent);
            Animatoo.animateSlideLeft(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
