package com.aftersoft.projecthawksnest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by lars on 01-06-17.
 */

public class GalleryAdapter extends ArrayAdapter<GalleryItem> {

    public GalleryAdapter(Context context, ArrayList<GalleryItem> galleryItems) {
       super(context, 0, galleryItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GalleryItem item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.gallery_grid_item, parent, false);
        }

        ImageView imageView =  (ImageView) convertView.findViewById(R.id.imageView_image);
        ImageView checkView =  (ImageView) convertView.findViewById(R.id.imageView_check);

        imageView.setBackgroundColor(Color.BLACK);

        if (item.isChecked())
            checkView.setImageResource(R.drawable.checked_image);
        else if (!item.isChecked())
            checkView.setImageResource(0);

        return convertView;
    }
}
