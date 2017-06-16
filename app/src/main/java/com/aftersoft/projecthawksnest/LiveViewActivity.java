package com.aftersoft.projecthawksnest;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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
    private CameraView mCameraView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_view);
        findViewById(R.id.activityLiveView_fab_toGallery).setOnClickListener(this);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Bracket Placement").setMessage("Place your phone inside of the bracket in front of you. Make sure your phone is fastened tightly.")
                .setView(R.layout.bracketplacement)
                .setNeutralButton("I understand", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        if (PermissionHandler.requestCameraPermission(this)) {
            mCameraView = new CameraView(this);//create a SurfaceView to show camera data
            FrameLayout camera_view = (FrameLayout) findViewById(R.id.camera_view);
            camera_view.addView(mCameraView);//add the SurfaceView to the layout
        }
    }

    @Override
    protected void onPause() {
        Log.v(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.v(TAG, "onResume");
        super.onResume();
    }

    public Bitmap addData(Bitmap bitmap) {
        Typeface plain = Typeface.createFromAsset(getAssets(), "fonts/DK Jambo.ttf");
        Paint paintText = new Paint();
        Paint paint = new Paint();
        paintText.setTypeface(plain);
        paintText.setColor(Color.WHITE);
        paintText.setAntiAlias(true);
        paintText.setTextSize(64);
        paint.setColor(Color.argb(255 / 2, 0, 0, 0));

        Bitmap bitmapEdited = bitmap.copy(Bitmap.Config.ARGB_8888, true);


        Canvas canvas = new Canvas(bitmapEdited);
        canvas.drawText("G-Kracht: ...", 20, 140, paintText);
        canvas.drawText("Essteling", 20, 60, paintText);
        return Bitmap.createBitmap(bitmapEdited,
                0,
                0,
                bitmapEdited.getWidth(),
                bitmapEdited.getHeight());
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
    public void onPictureTaken(byte[] data, Camera camera) {
        Log.v(TAG, "onPictureTaken");
        if (data != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            if (bitmap != null) {
                bitmap = addData(bitmap);
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
        mCameraView.getmCamera().startPreview();
    }

    private void takePicture() {
        Log.v(TAG, "takePicture");
        mCameraView.getmCamera().takePicture(null, null, this);
    }

    @Override
    public void onClick(View v) {
        Log.v(TAG, "onClick");
        startActivity(new Intent(this, GalleryActivity.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PermissionHandler.PERMISSION_CAMERA) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                    finishAffinity();
            }
        }

        mCameraView = new CameraView(this);//create a SurfaceView to show camera data
        FrameLayout camera_view = (FrameLayout) findViewById(R.id.camera_view);
        camera_view.addView(mCameraView);//add the SurfaceView to the layout
    }
}

