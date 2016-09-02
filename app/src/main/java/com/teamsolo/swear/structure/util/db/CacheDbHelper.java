package com.teamsolo.swear.structure.util.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.teamsolo.base.util.LogUtility;
import com.teamsolo.swear.foundation.constant.DbConst;
import com.teamsolo.swear.foundation.util.db.BaseDbHelper;

import java.util.HashMap;
import java.util.Map;

import static com.teamsolo.swear.foundation.constant.DbConst.TABLE_CACHE_FIELDS;

/**
 * description: cache database helper
 * author: Melody
 * date: 2016/8/24
 * version: 0.0.0.1
 */
@SuppressWarnings("unused")
public class CacheDbHelper extends BaseDbHelper {

    public CacheDbHelper(Context context) {
        super(context, DbConst.TABLE_CACHE);
        initDBHelper();
    }

    public boolean save(String key, String cache, String spare) {
        if (TextUtils.isEmpty(key)) return false;

        ContentValues values = new ContentValues();
        values.put(TABLE_CACHE_FIELDS[1][0], key);
        values.put(TABLE_CACHE_FIELDS[2][0], cache);
        values.put(TABLE_CACHE_FIELDS[3][0], spare);

        boolean result = db.replace(tableName, values) != -1;
        closeDB();

        return result;
    }

    public Map<String, String> load(String key) {
        if (TextUtils.isEmpty(key)) return null;

        Cursor cursor = db.query(tableName, TABLE_CACHE_FIELDS[1][0] + "='" + key + "'");

        if (cursor == null) {
            closeDB();
            return null;
        }

        Map<String, String> result = new HashMap<>();
        if (cursor.moveToFirst()) {
            do {
                try {
                    result.put(TABLE_CACHE_FIELDS[1][0], cursor.getString(cursor.getColumnIndex(TABLE_CACHE_FIELDS[1][0])));
                    result.put(TABLE_CACHE_FIELDS[2][0], cursor.getString(cursor.getColumnIndex(TABLE_CACHE_FIELDS[2][0])));
                    result.put(TABLE_CACHE_FIELDS[3][0], cursor.getString(cursor.getColumnIndex(TABLE_CACHE_FIELDS[3][0])));
                } catch (Exception e) {
                    LogUtility.e(getClass().getSimpleName(), e.getMessage());
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        closeDB();

        return result;
    }

    public void clear(String key) {
        if (!TextUtils.isEmpty(key))
            db.delete(tableName, TABLE_CACHE_FIELDS[1][0] + "='" + key + "'");
        else db.delete(tableName, "1=1");
        closeDB();
    }
}
