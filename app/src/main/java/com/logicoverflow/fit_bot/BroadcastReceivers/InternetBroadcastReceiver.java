package com.logicoverflow.fit_bot.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.logicoverflow.fit_bot.ChatActivity;

public class InternetBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "MyBroadcastReceiver";


//Log.e("ramy","broadcastrecieved");
//        ChatActivity.checkConnectivity(context);

        @Override
        public void onReceive(Context context, Intent intent) {
//            StringBuilder sb = new StringBuilder();
//            sb.append("Action: " + intent.getAction() + "\n");
//            sb.append("URI: " + intent.toUri(Intent.URI_INTENT_SCHEME).toString() + "\n");
//            String log = sb.toString();
            Log.e(TAG, "what");
 //           Toast.makeText(context, log, Toast.LENGTH_LONG).show();
        }

}
