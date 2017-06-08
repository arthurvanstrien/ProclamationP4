package com.aftersoft.projecthawksnest;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by snick on 8-6-2017.
 */

public class WifiHandler {
    private static volatile WifiHandler instance;
    private WifiManager wifiManager;
    private ConnectivityManager connManager;
    private WifiConfiguration wifiConfig;
    private boolean connected;
    private int netId = -1;
    private List<WifiStateListener> wifiStateListeners = new ArrayList<>();

    public static WifiHandler getInstance(Context applicationContext) {
        if (instance == null) {
            synchronized (WifiHandler.class) {
                if (instance == null) {
                    instance = new WifiHandler(applicationContext);
                }
            }
        }
        return instance;
    }

    private WifiHandler(Context applicationContext) {
        wifiManager = (WifiManager) applicationContext.getApplicationContext().getSystemService(WIFI_SERVICE);
        connManager = (ConnectivityManager) applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    /**
     * Connect to a new network.
     * @param ssid SSID of the network to connect to
     * @param password Password of the network to connect to
     * @param networkEncryption Encryption type of the network to connect to
     * @param hidden True if the network to connect to is hidden
     * @return Returns true if the connection was successful
     */
    public boolean connect(String ssid, String password, String networkEncryption, boolean hidden) {
        forget();
        Log.i("Connecting to", ssid);
        if (!networkEncryption.equals("WPA"))
            return false;
        wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", ssid);
        wifiConfig.preSharedKey = String.format("\"%s\"", password);
        wifiConfig.hiddenSSID = hidden;
        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);

        netId = wifiManager.addNetwork(wifiConfig);

        if (wifiManager.isWifiEnabled()) {
            wifiManager.disconnect();
        } else {
            wifiManager.setWifiEnabled(true);
        }

        wifiManager.enableNetwork(netId, true);

        return reconnect();
    }

    /**
     * Reconnect to the currently connected to network
     * @return returns true if the reconnection was successful
     */

    int i = 0;

    public boolean reconnect() {
        connected = false;

        final Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                i++;
                if (connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected() && !connected) {
                    if (wifiManager.getConnectionInfo().getSSID().equals(wifiConfig.SSID)) {
                        for (WifiStateListener wifiStateListener : wifiStateListeners) {
                            wifiStateListener.onConnected();
                        }
                        Log.i("Wifi connected: ", String.valueOf(wifiManager.getConnectionInfo()));
                        connected = true;
                    }
                }
                Log.i("i: ", String.valueOf(i));
                if (i == 5){
                    if (!connected) {
                        for (WifiStateListener wifiStateListener : wifiStateListeners) {
                            wifiStateListener.onConnectedFail();
                        }
                    }
                    timer.cancel();
                }
            }
        };
        timer.scheduleAtFixedRate(task, 0, 5000);

        return connected;
    }

    public void disconnect() {
        if (wifiManager.getConnectionInfo().getNetworkId() == netId)
            wifiManager.disableNetwork(netId);
        for (WifiStateListener wifiStateListener : wifiStateListeners) {
            wifiStateListener.onDisconnected();
        }
    }

    public void forget() {
        if (wifiManager.getConnectionInfo().getNetworkId() == netId)
            wifiManager.removeNetwork(netId);

        for (WifiStateListener wifiStateListener : wifiStateListeners) {
            wifiStateListener.onDisconnected();
        }
    }

    public interface WifiStateListener {
        void onConnected();
        void onConnectedFail();
        void onDisconnected();
    }

    public void addOnWifiStateListener(WifiStateListener wifiStateListener) {
        wifiStateListeners.add(wifiStateListener);
    }

    public void removeOnWifiStateListener(WifiStateListener wifiStateListener) {
        wifiStateListeners.remove(wifiStateListener);
    }
}
