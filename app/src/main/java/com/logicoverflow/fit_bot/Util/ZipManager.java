package com.logicoverflow.fit_bot.Util;

import android.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ZipManager {
    private static int BUFFER_SIZE = 6 * 1024;

    public static void unzip(String zipFile, String location) throws IOException{

        try {
            File locationDirectory = new File(location);
            if (!locationDirectory.isDirectory()) {
                locationDirectory.mkdirs();
            }else{
                    for (File child : locationDirectory.listFiles()){
                        if(child.isDirectory()){
                            FileUtils.deleteDirectory(child);
                        }
                    }
            }
            ZipInputStream zin = new ZipInputStream(new FileInputStream(location + "/" + zipFile));
            try {
                ZipEntry ze = null;
                while ((ze = zin.getNextEntry()) != null) {
                    String path = location + File.separator + ze.getName();

                    if (ze.isDirectory()) {
                        File unzipFile = new File(path);
                        if (!unzipFile.isDirectory()) {
                            unzipFile.mkdirs();
                        }
                    } else {
                        FileOutputStream fout = new FileOutputStream(path, false);

                        try {
                            for (int c = zin.read(); c != -1; c = zin.read()) {
                                fout.write(c);
                            }
                            zin.closeEntry();
                        } finally {
                            fout.close();
                        }
                    }
                }
            } finally {
                zin.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Unzip exception", e);
        }

//        File file = new File(location + "/" + zipFile);
//        file.delete();
    }
}