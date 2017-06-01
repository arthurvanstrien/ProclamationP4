package com.aftersoft.projecthawksnest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.client.result.WifiParsedResult;
import com.google.zxing.client.result.WifiResultParser;

import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    public final static String TAG = "MainActivity";
    private static final int PERMISSIONS_REQUEST_ACCESS_CAMERA = 0;
    private ZXingScannerView scannerView;
    private boolean resultHandled;
    private WifiManager wifiManager;
    private int netId = -1;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
        scannerView.setFormats(Collections.singletonList(BarcodeFormat.QR_CODE));
        scannerView.setAutoFocus(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_ACCESS_CAMERA);
        } else {
            scannerView.startCamera();
        }

        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.activity_qrloading, null);
        builder.setView(mView);
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.y = (int) (getResources().getDisplayMetrics().heightPixels * 0.3);
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
        if (netId >= 0)
            wifiManager.removeNetwork(netId);
    }

    @Override
    public void onBackPressed() {
        if (!resultHandled) {
            super.onBackPressed();
        } else {
            resultHandled = false;
            scannerView.resumeCameraPreview(this);
        }
    }

    static int seconds = 0;

    @Override
    public void handleResult(Result result) {
        dialog.show();
        resultHandled = true;
        Log.v(TAG, result.getText()); // Prints scan results
        Log.v(TAG, result.getBarcodeFormat().toString());
        WifiParsedResult parsedResult = (WifiParsedResult) WifiResultParser.parseResult(result);
        connectToWifi(parsedResult.getSsid(), parsedResult.getPassword(), parsedResult.getNetworkEncryption(), parsedResult.isHidden());
        Thread thread = new Thread(){
            @Override
            public void run(){
                Timer timer = new Timer();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        while (seconds < 10000){
                            if (wifiManager.getConnectionInfo().getNetworkId() == netId){
                                dialog.cancel();
                                break;
                            }
                            seconds++;
                        }
//                        Toast.makeText(MainActivity.this, "Failed to connect", Toast.LENGTH_SHORT).show();
                    }
                };
                timer.schedule(task, 0, 10001);
            }
        };
        thread.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_ACCESS_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                scannerView.startCamera();
            }
        }
    }

    private void connectToWifi(String ssid, String password, String networkEncryption, boolean hidden) {
        Log.i("Connecting to", ssid);
        if (!networkEncryption.equals("WPA"))
            return;
        WifiConfiguration wifiConfig = new WifiConfiguration();
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
        wifiManager.reconnect();
        Log.i("Wifi connected: ", String.valueOf(wifiManager.getConnectionInfo()));
    }
}
