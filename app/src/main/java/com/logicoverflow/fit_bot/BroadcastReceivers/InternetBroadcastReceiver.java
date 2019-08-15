package com.logicoverflow.fit_bot.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.logicoverflow.fit_bot.ChatActivity;

import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.logicoverflow.fit_bot.ChatActivity;
import com.logicoverflow.fit_bot.R;
import com.logicoverflow.fit_bot.Util.AppInternetStatus;

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
            ImageView view = ((ChatActivity)context).getWindow().getDecorView().findViewById(R.id.toolbar_connectivity_circle);

            //Log.e(TAG, "what");
            if(AppInternetStatus.isOnline()) {

                view.setImageResource(R.drawable.online_circle);
                Toast.makeText(context, "متصل بالشبكة", Toast.LENGTH_LONG).show();

            }else {

                view.setImageResource(R.drawable.offline_circle);
                Toast.makeText(context, "قطع الاتصال بالشبكة", Toast.LENGTH_LONG).show();

            }

        }

}
