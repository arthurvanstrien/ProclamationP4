package com.aftersoft.projecthawksnest;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by snick on 8-6-2017.
 */

public class WifiHandler {
    private static volatile WifiHandler instance;
    private WifiManager wifiManager;
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
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", ssid);
        wifiConfig.preSharedKey = String.format("\"%s\"", password);
        wifiConfig.hiddenSSID = hidden;
        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);

        if (!wifiManager.isWifiEnabled())
            wifiManager.setWifiEnabled(true);

        netId = wifiManager.addNetwork(wifiConfig);

        if (wifiManager.isWifiEnabled()) {
            wifiManager.disconnect();
        } else {
            wifiManager.setWifiEnabled(true);
        }

        wifiManager.enableNetwork(netId, true);
        if (wifiManager.reconnect()) {
            for (WifiStateListener wifiStateListener : wifiStateListeners) {
                wifiStateListener.onConnect();
            }
            Log.i("Wifi connected: ", String.valueOf(wifiManager.getConnectionInfo()));
            return true;
        } else
            return false;
    }

    /**
     * Reconnect to the currently connected to network
     * @return returns true if the reconnection was successful
     */
    public boolean reconnect() {
        if (wifiManager.getConnectionInfo().getNetworkId() == netId) {
            if (wifiManager.reconnect()) {
                for (WifiStateListener wifiStateListener : wifiStateListeners) {
                    wifiStateListener.onConnect();
                }
                Log.i("Wifi connected: ", String.valueOf(wifiManager.getConnectionInfo()));
                return true;
            } else
                return false;
        } else
            return false;
    }

    public void disconnect() {
        if (wifiManager.getConnectionInfo().getNetworkId() == netId)
            wifiManager.disableNetwork(netId);
        for (WifiStateListener wifiStateListener : wifiStateListeners) {
            wifiStateListener.onDisconnect();
        }
    }

    public void forget() {
        if (wifiManager.getConnectionInfo().getNetworkId() == netId)
            wifiManager.removeNetwork(netId);

        for (WifiStateListener wifiStateListener : wifiStateListeners) {
            wifiStateListener.onDisconnect();
        }
    }

    public interface WifiStateListener {
        void onConnect();
        void onDisconnect();
    }

    public void addOnWifiStateListener(WifiStateListener wifiStateListener) {
        wifiStateListeners.add(wifiStateListener);
    }

    public void removeOnWifiStateListener(WifiStateListener wifiStateListener) {
        wifiStateListeners.remove(wifiStateListener);
    }
}
