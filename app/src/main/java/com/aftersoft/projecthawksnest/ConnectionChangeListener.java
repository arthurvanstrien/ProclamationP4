package com.aftersoft.projecthawksnest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by snick on 16-6-2017.
 */

public class ConnectionChangeListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );
        NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        WifiHandler.getInstance(context).onConnectionChanged(wifiNetInfo);
    }
}
