package com.aftersoft.projecthawksnest;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        makeTestItems();
        final ArrayList<GalleryItem> galleryItems = getGallaryItems();
        final GridView gridView = (GridView) this.findViewById(R.id.gridview);
        final Button nextButton = (Button) this.findViewById(R.id.next_button);

        ArrayAdapter gridViewAdapter = new GalleryAdapter(getApplicationContext(), galleryItems);

        gridView.setAdapter(gridViewAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), DetailPhotoActivity.class);
                intent.putExtra("EXTRA", galleryItems.get(position));
                startActivity(intent);
            }
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                GalleryItem galleryItem = galleryItems.get(position);
                galleryItem.setChecked(!galleryItem.isChecked());
                galleryItems.set(position, galleryItem);
                gridView.invalidateViews();
                return true;
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File imagesFolder = new File(getFilesDir(), "images");
                for (int count = imagesFolder.listFiles().length - 1; count > -1; count--) {
                    if (!galleryItems.get(count).isChecked()){
                        imagesFolder.listFiles()[count].delete();
                    }
                }
            }
        });

    }

    public ArrayList<GalleryItem> getGallaryItems() {
        ArrayList<GalleryItem> galleryItems = new ArrayList<>();
        File imagesFolder = new File(getFilesDir(), "images");

        if (!imagesFolder.exists())
            return galleryItems;

        for (int count = 0; count < imagesFolder.listFiles().length; count++) {
            try {
                FileInputStream fileInputStream = new FileInputStream(imagesFolder.listFiles()[count]);
                Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                galleryItems.add(new GalleryItem(byteArrayOutputStream.toByteArray()));
                fileInputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return galleryItems;
    }

    public void makeTestItems() {
        for (int count = 0; count < 10; count++){
            Resources res = getResources();
            Drawable drawable = res.getDrawable(R.drawable.checked_image);
            Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] data = stream.toByteArray();

            Bitmap bitmap2 = BitmapFactory.decodeByteArray(data, 0, data.length);
            if (bitmap2 != null) {
                File imagesFolder = new File(getFilesDir(), "images");
                imagesFolder.mkdirs();
                String imageName = "" + count;
                File image = new File(imagesFolder, imageName);

                try  {
                    FileOutputStream fileOutputStream = new FileOutputStream(image);
                    bitmap2.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}
