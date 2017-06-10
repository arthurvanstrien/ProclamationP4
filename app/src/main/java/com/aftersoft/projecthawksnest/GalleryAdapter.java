package com.aftersoft.projecthawksnest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by lars on 01-06-17.
 */

public class GalleryAdapter extends BaseAdapter {

    private File fileDir;
    private Context context;
    private List<GalleryItem> galleryItems;

    public GalleryAdapter(File fileDir, Context context, List<GalleryItem> galleryItems) {
        this.fileDir = fileDir;
        this.context = context;
        this.galleryItems = galleryItems;
    }

    @Override
    public int getCount() {
        return galleryItems.size();
    }

    @Override
    public Object getItem(int position) {
        return galleryItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return galleryItems.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        File imagesFolder = new File(fileDir, "images");
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.gallery_grid_item, parent, false);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView_image);

        File imageFile = imagesFolder.listFiles()[position];
        Glide
                .with(parent)
                .load(imageFile)
                .into(imageView);

        ImageView imageViewCheck = (ImageView) convertView.findViewById(R.id.imageView_check);
        if (((GalleryItem) getItem(position)).isChecked()) {
            imageViewCheck.setVisibility(View.VISIBLE);
        } else {
            imageViewCheck.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }
}
