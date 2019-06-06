package com.logicoverflow.fitbot.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.logicoverflow.fitbot.ChatActivity;

public class InternetBroadcastReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("ramy","broadcastrecieved");
        ChatActivity.checkConnectivity(context);

    }
}
