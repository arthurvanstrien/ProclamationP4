package com.aftersoft.projecthawksnest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
    private static final int PERMISSIONS_REQUEST_ACCESS_CAMERA = 0;
    private ZXingScannerView scannerView;
    private boolean resultHandled;
    private WifiHandler wifiHandler;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
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

        wifiHandler = WifiHandler.getInstance(getApplicationContext());
        wifiHandler.addOnWifiStateListener(this);

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
        Log.v(TAG, "onResume");
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    @Override
    protected void onPause() {
        Log.v(TAG, "onPause");
        super.onPause();
        scannerView.stopCamera();
        if (wifiHandler != null) {
//            wifiHandler.forget();
        }
    }

    @Override
    public void onBackPressed() {
        Log.v(TAG, "onBackPressed");
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
        Log.v(TAG, "handleResult");
        dialog.show();
        resultHandled = true;
        Log.v(TAG, result.getText()); // Prints scan results
        WifiParsedResult parsedResult = (WifiParsedResult) WifiResultParser.parseResult(result);
        wifiHandler.connect(parsedResult.getSsid(), parsedResult.getPassword(), parsedResult.getNetworkEncryption(), parsedResult.isHidden());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.v(TAG, "onRequestPermissionResult");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_ACCESS_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                scannerView.startCamera();
            }
        }
    }

    @Override
    public void onConnected() {
        Log.v(TAG, "onConnected");
        dialog.cancel();
        scannerView.stopCameraPreview();
        scannerView.stopCamera();
        startActivity(new Intent(getApplicationContext(), LiveViewActivity.class));
    }

    @Override
    public void onConnectedFail() {
        Log.v(TAG, "onConnectedFail");
        dialog.cancel();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(scannerView.getContext(), "Verbinding maken mislukt", Toast.LENGTH_LONG).show();
            }
        });
        scannerView.startCamera();
    }

    @Override
    public void onDisconnected() {
        Log.v(TAG, "onDisconnected");

    }

    public void showBracketIntroduction() {
        AlertDialog.Builder BracketDialog = new AlertDialog.Builder(this);
        BracketDialog.setTitle("Bracket Placement").setMessage("Place your phone inside of the bracket in front of you. Make sure your phone is fastened tightly.").show();
    }

    public boolean hasUsableSpace() {
        File imagesFolder = new File(getFilesDir(), "images");
        if (imagesFolder.getUsableSpace() >= 104857600)
        return true;

        return false;
    }
}
