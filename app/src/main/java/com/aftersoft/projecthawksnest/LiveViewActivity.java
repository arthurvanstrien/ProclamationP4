package com.aftersoft.projecthawksnest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.os.Bundle;
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
    private double force;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_view);

        DataAsyncTask dataAsyncTask = new DataAsyncTask(new DataTaskListener() {
            @Override
            public void onGetDone(Double xAxis, Double yAxis, Double zAxis) {
                double tempForce = Math.sqrt(Math.pow(xAxis, 2) + Math.pow(yAxis, 2));
                force = Math.sqrt(Math.pow(tempForce, 2) + Math.pow(zAxis, 2));
            }

            @Override
            public void hasError() {

            }
        });

        mCameraView = new CameraView(this);//create a SurfaceView to show camera data
        FrameLayout camera_view = (FrameLayout) findViewById(R.id.camera_view);
        camera_view.addView(mCameraView);//add the SurfaceView to the layout

        findViewById(R.id.activityLiveView_fab_toGallery).setOnClickListener(this);
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
//        Matrix matrix = new Matrix();
//        matrix.postRotate(90);
//        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap,
//                0,
//                0,
//                bitmap.getWidth(),
//                bitmap.getHeight(),
//                matrix, true);

        Typeface plain = Typeface.createFromAsset(getAssets(), "fonts/DK Jambo.ttf");
        Paint paintText = new Paint();
        Paint paint = new Paint();
        paintText.setTypeface(plain);
        paintText.setColor(Color.WHITE);
        paintText.setAntiAlias(true);
        paintText.setTextSize(64);
        paint.setColor(Color.argb(255 / 2, 0, 0, 0));

//        Bitmap bitmapEdited = rotatedBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap bitmapEdited = bitmap.copy(Bitmap.Config.ARGB_8888, true);


        Canvas canvas = new Canvas(bitmapEdited);
//        canvas.rotate(90);
//        canvas.drawRect(0, -170, 400, 0, paint);
        canvas.drawText("G-Kracht: " + force, 20, 140, paintText);
        canvas.drawText("Essteling", 20, 60, paintText);

//        matrix = new Matrix();
//        matrix.postRotate(-90);

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
}

