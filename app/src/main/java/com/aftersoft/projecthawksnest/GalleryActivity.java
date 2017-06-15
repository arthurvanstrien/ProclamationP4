package com.aftersoft.projecthawksnest;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import java.io.File;
import java.io.IOException;
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
                Log.v("GalleryActivity", "nextButton.setOnClickListener");
                File imagesFolderTemp = new File(getFilesDir(), "images");
                File imagesFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath()+"/CoasterCam");
                imagesFolder.mkdirs();
                for (int count = imagesFolderTemp.listFiles().length - 1; count > -1; count--) {
                    GalleryItem image = galleryItems.get(count);
                    File imageFile = image.getPhoto();
                    if (image.isChecked()){
                        File dest = new File(imagesFolder.getPath(), String.valueOf(image.hashCode())+".jpg");
                        FileOps.copyFileOrDirectory(imageFile, dest);
                    }
                    imagesFolderTemp.listFiles()[count].delete();
                }

                Intent intent = new Intent(GalleryActivity.this, MainActivity.class );
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
                startActivity(intent);
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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, QuitPopup.class));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(GalleryActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.quitpopup, null);
                builder.setView(mView);
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        GalleryActivity.super.onBackPressed();
                    }
                });

                WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
                wmlp.y = (int) (getResources().getDisplayMetrics().heightPixels * 0.3);
                dialog.show();
            }
        });
    }
}
