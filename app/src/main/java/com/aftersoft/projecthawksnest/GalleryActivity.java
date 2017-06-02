package com.aftersoft.projecthawksnest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;

import java.io.Serializable;
import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        final ArrayList<GalleryItem> galleryItems = getTestItems();
        final GridView gridView = (GridView) this.findViewById(R.id.gridview);
        final Button nextButton = (Button) this.findViewById(R.id.next_button);

        ArrayAdapter gridViewAdapter = new GalleryAdapter(getApplicationContext(), galleryItems);

        gridView.setAdapter(gridViewAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GalleryItem galleryItem = galleryItems.get(position);
                Log.i("Test", "" + galleryItem.isChecked());
                galleryItem.setChecked(!galleryItem.isChecked());
                galleryItems.set(position, galleryItem);
                Log.i("Test", "" + galleryItem.isChecked());
                gridView.invalidateViews();
            }
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), DetailPhotoActivity.class);
                intent.putExtra("EXTRA", galleryItems.get(position));
                startActivity(intent);
                return false;
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    public ArrayList<GalleryItem> getTestItems() {
        ArrayList<GalleryItem> galleryItems = new ArrayList<>();

        galleryItems.add(new GalleryItem(null));
        galleryItems.add(new GalleryItem(null));
        galleryItems.add(new GalleryItem(null));
        galleryItems.add(new GalleryItem(null));
        galleryItems.add(new GalleryItem(null));
        galleryItems.add(new GalleryItem(null));
        galleryItems.add(new GalleryItem(null));
        galleryItems.add(new GalleryItem(null));

        return galleryItems;
    }

}
