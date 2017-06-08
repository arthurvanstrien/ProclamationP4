package com.aftersoft.projecthawksnest;

import java.io.File;
import java.io.Serializable;

/**
 * Created by lars on 01-06-17.
 */

public class GalleryItem implements Serializable {

    private File photo;
    private boolean checked;

    public GalleryItem(File photo) {
        this.photo = photo;
        checked = false;
    }

    public File getPhoto() {
        return photo;
    }

    public void setPhoto(File photo) {
        this.photo = photo;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void flipChecked() {
        checked = !checked;
    }
}
