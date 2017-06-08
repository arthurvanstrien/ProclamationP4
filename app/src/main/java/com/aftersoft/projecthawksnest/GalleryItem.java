package com.aftersoft.projecthawksnest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.Serializable;

/**
 * Created by lars on 01-06-17.
 */

public class GalleryItem implements Serializable {

    private byte[] photo;
    private boolean checked;

    public GalleryItem(byte[] photo) {
        this.photo = photo;
        checked = false;
    }

    public Bitmap getPhoto() {
        return BitmapFactory.decodeByteArray(photo, 0, photo.length, options);
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
