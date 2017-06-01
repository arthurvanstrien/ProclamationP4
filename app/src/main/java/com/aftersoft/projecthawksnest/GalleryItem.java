package com.aftersoft.projecthawksnest;

import android.graphics.drawable.Icon;
import android.media.Image;

/**
 * Created by lars on 01-06-17.
 */

public class GalleryItem {

    private Icon photo;
    private boolean checked;

    public GalleryItem(Icon photo) {
        this.photo = photo;
        checked = false;
    }

    public Icon getPhoto() {
        return photo;
    }

    public void setPhoto(Icon photo) {
        this.photo = photo;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
