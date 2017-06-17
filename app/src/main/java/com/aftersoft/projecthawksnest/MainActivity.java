package com.aftersoft.projecthawksnest;

import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.client.result.WifiParsedResult;
import com.google.zxing.client.result.WifiResultParser;

import java.io.File;
import java.util.Collections;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler, WifiHandler.WifiStateListener {
    public final static String TAG = "MainActivity";
    private ZXingScannerView scannerView;
    private boolean resultHandled;
    private WifiHandler wifiHandler;
    private AlertDialog qrLoadingDialog;
    BroadcastReceiver wifiBroadcastReveiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater factory = getLayoutInflater();
        final View view = factory.inflate(R.layout.introduction, null);
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Scanning QR-Code")
                .setView(view)
                .setMessage("Scan the QR-code near your seat by positioning the code within the rectangle.")
                .setCancelable(false)
                .setNeutralButton("I understand", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        })
                .show();
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
        scannerView.setFormats(Collections.singletonList(BarcodeFormat.QR_CODE));
        scannerView.setAutoFocus(true);

        if (PermissionHandler.requestAllPermissions(this)) {
            scannerView.startCamera();
        }
        wifiHandler = WifiHandler.getInstance(getApplicationContext());
        wifiHandler.addOnWifiStateListener(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.activity_qrloading, null);
        builder.setView(mView);
        qrLoadingDialog = builder.create();
        qrLoadingDialog.setCanceledOnTouchOutside(false);
        qrLoadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams wmlp = qrLoadingDialog.getWindow().getAttributes();
        wmlp.y = (int) (getResources().getDisplayMetrics().heightPixels * 0.3);

//        Uncomment line below to skip QR-Code
//        onConnected();
    }

    @Override
    protected void onResume() {
        super.onResume();
        wifiBroadcastReveiver = new WifiBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(wifiBroadcastReveiver, filter);
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(wifiBroadcastReveiver);
        qrLoadingDialog.dismiss();
        scannerView.stopCamera();
        if (wifiHandler != null) {
//            wifiHandler.forget();
        }
    }

    @Override
    public void onBackPressed() {
        if (!resultHandled) {
            wifiHandler.forget();
            super.onBackPressed();
        } else {
            resultHandled = false;
            scannerView.resumeCameraPreview(this);
        }
    }

    @Override
    public void handleResult(Result result) {
        resultHandled = true;
        qrLoadingDialog.show();
        Log.v(TAG, result.getText()); // Prints scan results
        WifiParsedResult parsedResult = (WifiParsedResult) WifiResultParser.parseResult(result);
        wifiHandler.connect(parsedResult.getSsid(), parsedResult.getPassword(), parsedResult.getNetworkEncryption(), parsedResult.isHidden());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PermissionHandler.PERMISSIONS_ALL) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                    finishAffinity();
            }
        }
        scannerView.startCamera();
    }

    @Override
    public void onConnected() {
        Log.v(TAG, "onConnected");
        qrLoadingDialog.cancel();
        scannerView.stopCameraPreview();
        scannerView.stopCamera();
        startActivity(new Intent(getApplicationContext(), LiveViewActivity.class));
    }

    @Override
    public void onConnectedFail() {
        Log.v(TAG, "onConnectedFail");
        qrLoadingDialog.cancel();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(scannerView.getContext(), "Verbinding maken mislukt", Toast.LENGTH_LONG).show();
                scannerView.resumeCameraPreview(MainActivity.this);
            }
        });
    }

    @Override
    public void onDisconnected() {
        Log.v(TAG, "onDisconnected");

    }

    public boolean hasUsableSpace() {
        File imagesFolder = new File(getFilesDir(), "images");
        if (imagesFolder.getUsableSpace() >= 104857600)
            return true;
        else
            return false;
    }
}