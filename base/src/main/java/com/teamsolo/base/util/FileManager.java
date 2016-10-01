package com.teamsolo.base.util;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

/**
 * description: file util
 * author: Melody
 * date: 2016/6/4
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused, deprecation")
public final class FileManager {

    /**
     * tag
     */
    private static final String TAG = "FileManager";

    /**
     * the path of sdcard
     */
    public static final String SD_PATH =
            Environment.getExternalStorageDirectory().getPath() + File.separator;

    /**
     * the base path
     */
    public static final String BASE_PATH = SD_PATH + "Swear" + File.separator;

    /**
     * log path
     */
    public static final String LOG_PATH = BASE_PATH + "log" + File.separator;

    /**
     * database path
     */
    public static final String DB_PATH = BASE_PATH + "database" + File.separator;

    /**
     * cache path
     */
    public static final String CACHE_PATH = BASE_PATH + "cache" + File.separator;

    /**
     * min free size of sdcard
     */
    public static final long MIN_FREE_SIZE = 1024 * 1024;

    /**
     * the max size of log file
     */
    public static final int LOG_MAX_SIZE = 9;

    static {
        // create log dir
        createDir(LOG_PATH);

        // create db dir
        createDir(DB_PATH);

        // create cache
        createDir(CACHE_PATH);
    }

    /**
     * create dir with path
     *
     * @param dir dir
     */
    public static boolean createDir(String dir) {
        return !dirIsExist(dir) && new File(dir).mkdirs();
    }

    /**
     * judge the dir exists or not
     *
     * @param dir dir
     * @return exists or not
     */
    public static boolean dirIsExist(String dir) {
        File file = new File(dir);

        return file.exists() && file.isDirectory();
    }

    /**
     * create file with filename
     *
     * @param fileName filename
     * @return success or fail
     */
    public static boolean createFile(String fileName) {
        return createFile(BASE_PATH, fileName);
    }

    /**
     * create file with path and filename
     *
     * @param path     path
     * @param fileName filename
     * @return success or fail
     */
    public static boolean createFile(String path, String fileName) {
        if (!dirIsExist(path) && !createDir(path)) return false;

        File file = new File(path + File.separator + fileName);

        if (!fileIsExist(path, fileName)) {
            try {
                return file.createNewFile();
            } catch (IOException e) {
                LogUtility.e(TAG, e.getMessage());
            }
        }

        return false;
    }

    /**
     * judge the file exists or not
     *
     * @param path     path
     * @param fileName fileName
     * @return exists or not
     */
    public static boolean fileIsExist(String path, String fileName) {
        File file = new File(path + File.separator + fileName);

        return file.exists() && file.isFile();
    }

    /**
     * get log print writer
     *
     * @return the log print writer
     */
    @Nullable
    public static PrintWriter getLogPrintWriter() {
        releaseSpace(LOG_PATH, LOG_MAX_SIZE);

        String fileName = "exception_" + System.currentTimeMillis() + ".txt";
        if (createFile(LOG_PATH, fileName)) {
            try {
                return new PrintWriter(
                        new File(LOG_PATH, fileName));
            } catch (FileNotFoundException e) {
                LogUtility.e(TAG, e.getMessage());
            }
        }

        return null;
    }

    public static boolean checkAvailable() throws Exception {
        return checkAvailable(0);
    }

    /**
     * check if sdcard is available
     *
     * @param needSize sdcard free size in need
     * @return available or not
     * @throws Exception
     */
    public static boolean checkAvailable(long needSize) throws Exception {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            throw new Exception("UnMountedMediaException: check if has a sdcard");

        if (getFreeSpace() < MIN_FREE_SIZE + needSize)
            throw new Exception("SpaceNotEnoughException: check if the sdcard has enough space");

        return true;
    }

    /**
     * get free space of sdcard
     *
     * @return the size
     */
    public static long getFreeSpace() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());

        if (BuildUtility.isRequired(Build.VERSION_CODES.JELLY_BEAN_MR2)) {
            return stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
        }

        return stat.getBlockSize() * stat.getAvailableBlocks();
    }

    /**
     * release sdcard space from aim dir
     *
     * @param aimDir the aim dir
     */
    public static void releaseSpace(String aimDir, int maxSize) {
        if (dirIsExist(aimDir)) {
            File[] files = new File(aimDir).listFiles();

            if (files.length > maxSize) {
                for (File file : files) {
                    if (file.isFile()) if (!file.delete()) file.deleteOnExit();
                }
            }
        }
    }

    /**
     * get bytes from stream
     *
     * @param inputStream input stream
     * @return the bytes
     * @throws IOException
     */
    public static byte[] getBytesFromStream(InputStream inputStream) throws IOException {
        int len;
        int size = 1024;

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[size];
        while ((len = inputStream.read(buf, 0, size)) != -1) bos.write(buf, 0, len);
        buf = bos.toByteArray();

        return buf;
    }

    /**
     * save file
     *
     * @param bytes source bytes
     * @param path  file path
     */
    public static void saveBytesToFile(byte[] bytes, String path) {
        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(path);
            fileOutputStream.write(bytes);
        } catch (IOException e) {
            LogUtility.e(TAG, e.getMessage());
        } finally {
            if (fileOutputStream != null) try {
                fileOutputStream.close();
            } catch (IOException e) {
                LogUtility.e(TAG, e.getMessage());
            }
        }
    }

    public static String getUriPath(Context context, Uri uri) {
        if (uri == null) return null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                && DocumentsContract.isDocumentUri(context, uri)) {
            if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type))
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type))
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                else if ("video".equals(type))
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                else if ("audio".equals(type))
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            if ("com.google.android.apps.photos.content".equals(uri.getAuthority()))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme()))
            return uri.getPath();

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) cursor.close();
        }

        return null;
    }

}
