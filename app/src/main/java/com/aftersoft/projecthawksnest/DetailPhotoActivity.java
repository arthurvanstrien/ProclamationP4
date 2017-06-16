package com.aftersoft.projecthawksnest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class DetailPhotoActivity extends AppCompatActivity {

    private GalleryItem galleryItem;

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
