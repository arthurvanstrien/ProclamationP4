package com.aftersoft.projecthawksnest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * Created by snick on 16-6-2017.
 */

public class WifiBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NetworkInfo netInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        WifiHandler.getInstance(context).onConnectionChanged(netInfo);
    }
}
