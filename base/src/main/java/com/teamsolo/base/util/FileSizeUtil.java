package com.teamsolo.base.util;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;

/**
 * description Calc size of file or dir
 * author Melo Chan
 * date 2016/11/11
 * version 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused, ResultOfMethodCallIgnored")
public class FileSizeUtil {

    public static final String TAG = FileSizeUtil.class.getSimpleName();

    public static final int SIZE_TYPE_B = 1, SIZE_TYPE_KB = 2, SIZE_TYPE_MB = 3, SIZE_TYPE_GB = 4;

    public static double getSize(String filePath, int sizeType) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) blockSize = getDirSize(file);
            else blockSize = getFileSize(file);
        } catch (Exception e) {
            LogUtility.e(TAG, "Failed, caused by: " + e.getMessage());
        }

        return formatSize(blockSize, sizeType);
    }

    public static String getSize(String filePath) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) blockSize = getDirSize(file);
            else blockSize = getFileSize(file);
        } catch (Exception e) {
            LogUtility.e(TAG, "Failed, caused by: " + e.getMessage());
        }

        return formatSize(blockSize);
    }

    public static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
            LogUtility.e(TAG, "Invalid file!");
        }

        return size;
    }

    public static long getDirSize(File dir) throws Exception {
        long size = 0;
        File files[] = dir.listFiles();
        for (File file : files)
            if (file.isDirectory()) size = size + getDirSize(file);
            else size = size + getFileSize(file);

        return size;
    }

    public static String formatSize(long size) {
        if (size == 0) return "0B";

        DecimalFormat format = new DecimalFormat("#.00");
        String dest;

        if (size < 1024) dest = format.format((double) size) + "B";
        else if (size < 1048576) dest = format.format((double) size / 1024) + "KB";
        else if (size < 1073741824) dest = format.format((double) size / 1048576) + "MB";
        else dest = format.format((double) size / 1073741824) + "GB";

        return dest;
    }

    public static double formatSize(long size, int sizeType) {
        DecimalFormat format = new DecimalFormat("#.00");
        double dest = 0;

        switch (sizeType) {
            case SIZE_TYPE_B:
                dest = Double.valueOf(format.format((double) size));
                break;

            case SIZE_TYPE_KB:
                dest = Double.valueOf(format.format((double) size / 1024));
                break;

            case SIZE_TYPE_MB:
                dest = Double.valueOf(format.format((double) size / 1048576));
                break;

            case SIZE_TYPE_GB:
                dest = Double.valueOf(format.format((double) size / 1073741824));
                break;

            default:
                break;
        }

        return dest;
    }
}