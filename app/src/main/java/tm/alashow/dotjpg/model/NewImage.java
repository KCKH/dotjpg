/*
 * Copyright (c) 2015  Alashov Berkeli
 * It is licensed under GNU GPL v. 2 or later. For full terms see the file LICENSE.
 */

package tm.alashow.dotjpg.model;

import android.net.Uri;

import java.io.File;

/**
 * Created by alashov on 04/08/15.
 */
public class NewImage {
    private File originFile;
    private File compressedFile;
    private boolean compressed = false;

    public File getOriginFile() {
        return originFile;
    }

    public NewImage setOriginFile(File originFile) {
        this.originFile = originFile;
        return this;
    }

    public File getCompressedFile() {
        return compressedFile;
    }

    public NewImage setCompressedFile(File compressedFile) {
        this.compressedFile = compressedFile;
        return this;
    }

    public boolean isCompressed() {
        return compressed;
    }

    public NewImage setCompressed(boolean compressed) {
        this.compressed = compressed;
        return this;
    }

    public long getSize() {
        return getFile().length();
    }

    public File getFile() {
        return (isCheckedCompressed() && compressed) ? getCompressedFile() : getOriginFile();
    }

    public Uri getUri() {
        return Uri.fromFile(getFile());
    }

    public boolean isCheckedCompressed() {
        return getCompressedFile() != null && getCompressedFile().exists();
    }
}
