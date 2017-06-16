package com.aftersoft.projecthawksnest;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by snick on 16-6-2017.
 */

public class PermissionHandler {
    public static final int PERMISSIONS_ALL = 0;
    public static final int PERMISSION_CAMERA = 1;
    public static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 2;

    /**
     * Request all needed permissions
     * @param activity the activity that will be used to request the permissions
     * @return returns true if no permissions needed to be requested.
     */
    public static boolean requestAllPermissions(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<>();
            if (activity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                permissions.add(Manifest.permission.CAMERA);
            if (activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissions.size() > 0) {
                activity.requestPermissions(permissions.toArray(new String[permissions.size()]), PERMISSIONS_ALL);
                return false;
            }
        }
        return true;
    }

    public static boolean requestCameraPermission(Activity activity) {
        Log.d("Requesting permission", "Camera");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                activity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA);
            return false;
        }
        return true;
    }

    public static boolean requestWriteExternalStoragePermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_WRITE_EXTERNAL_STORAGE);
            return false;
        }
        return true;
    }
}
