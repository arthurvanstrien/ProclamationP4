package com.aftersoft.projecthawksnest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by lars on 01-06-17.
 */

public class GalleryAdapter extends BaseAdapter {

    private File fileDir;
    private Context context;

    public GalleryAdapter(File fileDir, Context context) {
        this.fileDir = fileDir;
        this.context = context;
    }

    @Override
    public int getCount() {
        File imagesFolder = new File(fileDir, "images");
        return imagesFolder.listFiles().length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        File imagesFolder = new File(fileDir, "images");

        if (convertView == null) {

            convertView =layoutInflater.inflate(R.layout.gallery_grid_item, parent, false);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView_image);
        ImageView checkView = (ImageView) convertView.findViewById(R.id.imageView_check);

        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(imagesFolder.listFiles()[position]);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
      //  galleryItems.add(new GalleryItem(byteArrayOutputStream.toByteArray()));

        imageView.setImageBitmap(bitmap);

//        if (item.isChecked())
//            checkView.setImageResource(R.drawable.checked_image);
//        else if (!item.isChecked())
//            checkView.setImageResource(0);

        return convertView;
    }

    public Bitmap getPhoto(int position) {
        Bitmap bitmap = null;


        return bitmap;
    }

    //    public GalleryAdapter(Context context, ArrayList<GalleryItem> galleryItems) {
//       super(context, 0, galleryItems);
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        GalleryItem item = getItem(position);
//
//        if (convertView == null) {
//            convertView = LayoutInflater.from(getContext()).inflate(R.layout.gallery_grid_item, parent, false);
//        }
//
//        ImageView imageView =  (ImageView) convertView.findViewById(R.id.imageView_image);
//        ImageView checkView =  (ImageView) convertView.findViewById(R.id.imageView_check);
//
//        imageView.setImageBitmap(item.getPhoto());
//
//        if (item.isChecked())
//            checkView.setImageResource(R.drawable.checked_image);
//        else if (!item.isChecked())
//            checkView.setImageResource(0);
//
//        return convertView;
//    }
}
