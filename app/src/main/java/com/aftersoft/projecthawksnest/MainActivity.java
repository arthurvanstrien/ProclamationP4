package com.aftersoft.projecthawksnest;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.client.result.WifiParsedResult;
import com.google.zxing.client.result.WifiResultParser;

import java.util.Collections;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler, WifiHandler.WifiStateListener {
    public final static String TAG = "MainActivity";
    private static final int PERMISSIONS_REQUEST_ACCESS_CAMERA = 0;
    private ZXingScannerView scannerView;
    private boolean resultHandled;
    private WifiManager wifiManager;
    private int netId = -1;
    private WifiHandler wifiHandler;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater factory = getLayoutInflater();
        final View view = factory.inflate(R.layout.introduction, null);
        builder1.setTitle("Bracket Instructions");
        builder1.setView(view);
        builder1.setMessage("Scan the QR-code near your seat by positioning the code within the rectangle.");
        builder1.setNeutralButton("I understand", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder1.show();
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

        wifiHandler = WifiHandler.getInstance(getApplicationContext());
        wifiHandler.addOnWifiStateListener(this);

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
        if (wifiHandler != null) {
            wifiHandler.forget();
        }
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

    @Override
    public void handleResult(Result result) {
        dialog.show();
        resultHandled = true;
        Log.v(TAG, result.getText()); // Prints scan results
        Log.v(TAG, result.getBarcodeFormat().toString());
        WifiParsedResult parsedResult = (WifiParsedResult) WifiResultParser.parseResult(result);
        wifiHandler.connect(parsedResult.getSsid(), parsedResult.getPassword(), parsedResult.getNetworkEncryption(), parsedResult.isHidden());
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

    @Override
    public void onConnected() {
        dialog.cancel();
    }

    @Override
    public void onConnectedFail() {
        dialog.cancel();
    }

    @Override
    public void onDisconnected() {

    }
}
