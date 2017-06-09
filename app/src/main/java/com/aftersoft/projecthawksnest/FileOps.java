package com.aftersoft.projecthawksnest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by snick on 9-6-2017.
 */

public class FileOps {
    public static void copyFileOrDirectory(File src, File dst) {

    try {
        if (src.isDirectory()) {

            String files[] = src.list();
            int filesLength = files.length;
            for (int i = 0; i < filesLength; i++) {
                File src1 = new File(src, files[i]);;
                copyFileOrDirectory(src1, dst);
            }
        } else {
            copyFile(src, dst);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }
}
