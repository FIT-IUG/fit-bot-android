package com.logicoverflow.fit_bot.Util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class AppInternetStatus {

    private static AppInternetStatus instance = new AppInternetStatus();
    static Context context;

    public static AppInternetStatus getInstance(Context ctx) {
        context = ctx.getApplicationContext();
        return instance;
    }

    public static boolean isOnline() {
            ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null &&
                    activeNetwork.isConnected();

    }
}
