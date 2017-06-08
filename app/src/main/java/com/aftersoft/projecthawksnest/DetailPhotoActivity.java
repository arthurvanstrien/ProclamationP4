package com.aftersoft.projecthawksnest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class DetailPhotoActivity extends AppCompatActivity {

    private GalleryItem galleryItem;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_photo);

        galleryItem = (GalleryItem) getIntent().getExtras().get("EXTRA");
        position = getIntent().getExtras().getInt("POSITION");

        ImageView imageView = (ImageView) findViewById(R.id.imageView);

        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap rotatedBitmap = Bitmap.createBitmap(galleryItem.getPhoto(),
                                                    0,
                                                    0,
                                                    galleryItem.getPhoto().getWidth(),
                                                    galleryItem.getPhoto().getHeight(),
                                                    matrix, true);

        Typeface plain = Typeface.createFromAsset(getAssets(), "fonts/DK Jambo.ttf");
        Paint paintText = new Paint();
        Paint paint = new Paint();
        paintText.setTypeface(plain);
        paintText.setColor(Color.WHITE);
        paintText.setAntiAlias(true);
        paintText.setTextSize(64);
        paint.setColor(Color.argb(255/2, 0, 0, 0));

        Bitmap bitmap = rotatedBitmap.copy(Bitmap.Config.ARGB_8888, true);

        Canvas canvas = new Canvas(bitmap);
        canvas.rotate(90);
        canvas.drawRect(0, -170, 400, 0, paint);
        canvas.drawText("G-Kracht: ...", 20, -20, paintText);
        canvas.drawText("Essteling", 20, -100, paintText);

        imageView.setImageBitmap(bitmap);

        canvas.rotate(-90);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] data = stream.toByteArray();

        galleryItem.setPhoto(data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.i("fsdfsd", "fsdfsdf");
            Intent intent = new Intent();
            intent.putExtra("EXTRA", galleryItem);
            intent.putExtra("POSITION", position);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
        return true;
    }

}
