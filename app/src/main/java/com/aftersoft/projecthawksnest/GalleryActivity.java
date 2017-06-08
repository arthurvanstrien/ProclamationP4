package com.aftersoft.projecthawksnest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {
    private ArrayList<GalleryItem> galleryItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        if (galleryItems == null)
            galleryItems = createGalleryItems();
        else
            galleryItems = (ArrayList<GalleryItem>) savedInstanceState.getSerializable("galleryItems");
        final GridView gridView = (GridView) this.findViewById(R.id.gridview);
        final Button nextButton = (Button) this.findViewById(R.id.next_button);
        BaseAdapter gridViewAdapter = new GalleryAdapter(getFilesDir(), getApplicationContext(), galleryItems);

        gridView.setAdapter(gridViewAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                galleryItems.get(position).flipChecked();
                gridView.invalidateViews();
            }
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), DetailPhotoActivity.class);
                intent.putExtra("galleryItem", galleryItems.get(position));
                startActivity(intent);
                return true;
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File imagesFolder = new File(getFilesDir(), "images");
                for (int count = imagesFolder.listFiles().length - 1; count > -1; count--) {
                    if (galleryItems.get(count).isChecked()){
                        // TODO: 8-6-2017 copy image to DCIM
                    }
                    imagesFolder.listFiles()[count].delete();
                }
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("galleryItems", galleryItems);
    }

    private ArrayList<GalleryItem> createGalleryItems() {
        ArrayList<GalleryItem> galleryItems = new ArrayList<>();
        File imagesFolder = new File(getFilesDir(), "images");

        if (!imagesFolder.exists())
            return galleryItems;

        for (int count = 0; count < imagesFolder.listFiles().length; count++) {
            galleryItems.add(new GalleryItem(imagesFolder.listFiles()[count]));
        }
        return galleryItems;
    }
}
