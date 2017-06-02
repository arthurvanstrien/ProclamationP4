package com.aftersoft.projecthawksnest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import java.util.ArrayList;

public class DetailPhotoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_photo);

        GalleryItem galleryItem = (GalleryItem) getIntent().getExtras().get("EXTRA");

        ImageView imageView = (ImageView) findViewById(R.id.imageView);

        //imageView.setImageIcon(galleryItem.getPhoto());

    }
}
