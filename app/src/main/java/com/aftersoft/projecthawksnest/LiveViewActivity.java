package com.aftersoft.projecthawksnest;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by Rick Verstraten on 1-6-2017.
 */

public class LiveViewActivity extends AppCompatActivity implements Camera.PictureCallback, View.OnClickListener {
    public final static String TAG = "LiveViewActivity";
    private static Camera mCamera = null;
    private CameraView mCameraView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_view);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        try {
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        if (mCamera != null) {
            mCameraView = new CameraView(this, mCamera);//create a SurfaceView to show camera data
            FrameLayout camera_view = (FrameLayout) findViewById(R.id.camera_view);
            camera_view.addView(mCameraView);//add the SurfaceView to the layout
        }

            findViewById(R.id.activityLiveView_fab_toGallery).setOnClickListener(this);
    }

    /**
     * For the time being a picture will be taken on back press.
     */
    @Override
    public void onBackPressed() {
        Log.v(TAG, "onBackPressed");
        takePicture();
    }

    @Override
    protected void onPause() {
        Log.v(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Log.v(TAG, "onPictureTaken");
        if (data != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            if (bitmap != null) {
                File imagesFolder = new File(getFilesDir(), "images");
                imagesFolder.mkdirs();
                String imageName = Calendar.getInstance().getTime().toString();
                File image = new File(imagesFolder, imageName);

                try  {
                    FileOutputStream fileOutputStream = new FileOutputStream(image);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        mCamera.startPreview();
    }

    private void takePicture() {
        Log.v(TAG, "takePicture");
        mCamera.takePicture(null, null, this);
    }

    @Override
    public void onClick(View v) {
        Log.v(TAG, "onClick");
        startActivity(new Intent(this, GalleryActivity.class));
    }
}

