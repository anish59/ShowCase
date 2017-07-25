package com.showcase.helper;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FileHelper {


    public static final String PINNED_FOLDER = "pinnedPics";
    private static final String PINNED_IMAGE_PATH = "picSelf/" + PINNED_FOLDER + "/";

    public static String getPinnedPath() {

        return Environment.getExternalStorageDirectory() + File.separator + PINNED_IMAGE_PATH + File.separator;
    }


    public static String getFileSize(String path) {
        String fileSize = "";
        File file = new File(path);
        long fileSizeInBytes = file.length();
        long fileSizeInKB = fileSizeInBytes / 1024;// Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
        long fileSizeInMB = fileSizeInKB / 1024;// Convert the KB to MegaBytes (1 MB = 1024 KBytes)
        if (fileSizeInMB > 0) {
            fileSize = fileSizeInMB + " MB";
        } else {
            if (fileSizeInKB > 0) {
                fileSize = fileSizeInKB + " KB";
            } else {
                fileSize = fileSizeInBytes + " Bytes";
            }
        }
        return fileSize;
    }

    public static void copyFile(String inputPath, String inputFileName, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File(outputPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath);//+ inputFile
            out = new FileOutputStream(outputPath + inputFileName);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

        } catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }


}
