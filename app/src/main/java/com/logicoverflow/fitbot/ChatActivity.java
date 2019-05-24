package com.logicoverflow.fitbot;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Environment;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.logicoverflow.fitbot.Adapter.ChatMessageAdapter;
import com.logicoverflow.fitbot.Model.ChatMessage;

import org.alicebot.ab.AIMLProcessor;
import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.ab.Graphmaster;
import org.alicebot.ab.MagicBooleans;
import org.alicebot.ab.MagicNumbers;
import org.alicebot.ab.MagicStrings;
import org.alicebot.ab.PCAIMLProcessorExtension;
import org.alicebot.ab.Timer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class ChatActivity extends AppCompatActivity {


    private ListView mListView;
    private Button mButtonSend;
    private EditText mEditTextMessage;
    private ImageView mImageView;
    public Bot bot;
    public static Chat chat;
    private ChatMessageAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu);
        mListView = (ListView) findViewById(R.id.listView);
        mButtonSend = findViewById(R.id.btn_send);
        mEditTextMessage = (EditText) findViewById(R.id.et_message);

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


//        AssetManager assets = getResources().getAssets();
//        File jayDir = new File(Environment.getExternalStorageDirectory().toString() + "/FITChatbot/bots/Fitbot");
//
//
//
//        boolean b = jayDir.mkdirs();
//        if (jayDir.exists()) {
//
//           //to delete files everytime app is loaded (in case of editting aiml files)
//            for (String subdir:jayDir.list()){
//                File dir = new File(jayDir+"/"+subdir);
//                for (String file: dir.list()){
//                    Log.e("rmy",new File(dir+"/"+file).delete()+"");
//                }
//            }
//          //  */
//
//            //Reading the file
//            try {
//                for (String dir : assets.list("Fitbot")) {
//                    File subdir = new File(jayDir.getPath() + "/" + dir);
//                    boolean subdir_check = subdir.mkdirs();
//                    for (String file : assets.list("Fitbot/" + dir)) {
//                        File f = new File(jayDir.getPath() + "/" + dir + "/" + file);
//                        InputStream in = null;
//                        OutputStream out = null;
//                        in = assets.open("Fitbot/" + dir + "/" + file);
//                        out = new FileOutputStream(jayDir.getPath() + "/" + dir + "/" + file);
//                        //copy file from assets to the mobile's SD card or any secondary memory
//                        copyFile(in, out);
//                        in.close();
//                        in = null;
//                        out.flush();
//                        out.close();
//                        out = null;
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//
//        }
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

    private void sendMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, true, false);
        mAdapter.add(chatMessage);

        //mimicOtherMessage(message);
    }

    private void mimicOtherMessage(String message) {
        ChatMessage chatMessage;
        if(message.contains("gif")){
            message = message.substring(0,message.indexOf("gif"));
            chatMessage = new ChatMessage(message, false, true);
        }else{
            chatMessage = new ChatMessage(message, false, false);
        }

        mAdapter.add(chatMessage);
    }


    //copying the file
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
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
}
