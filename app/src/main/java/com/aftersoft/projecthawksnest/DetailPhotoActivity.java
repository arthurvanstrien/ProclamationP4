package com.aftersoft.projecthawksnest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;

public class DetailPhotoActivity extends AppCompatActivity {

    private GalleryItem galleryItem;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_photo);

        galleryItem = (GalleryItem) getIntent().getExtras().get("galleryItem");

        ImageView imageView = (ImageView) findViewById(R.id.imageView);

        Glide
                .with(this)
                .load(galleryItem.getPhoto())
                .into(imageView);
    }

}
