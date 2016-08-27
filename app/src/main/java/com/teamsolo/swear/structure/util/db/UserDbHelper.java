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

import static com.teamsolo.swear.foundation.constant.DbConst.TABLE_USER_FIELDS;

/**
 * description: user db helper for login page
 * author: Melody
 * date: 2016/8/27
 * version: 0.0.0.1
 */
@SuppressWarnings("unused")
public class UserDbHelper extends BaseDbHelper {

    public UserDbHelper(Context context) {
        super(context, DbConst.TABLE_USER);
        initDBHelper();
    }

    public boolean save(String phone, String password, String portrait, boolean remember) {
        if (TextUtils.isEmpty(phone)) return false;

        ContentValues values = new ContentValues();
        values.put(TABLE_USER_FIELDS[1][0], phone);
        values.put(TABLE_USER_FIELDS[2][0], password);
        values.put(TABLE_USER_FIELDS[3][0], portrait);
        values.put(TABLE_USER_FIELDS[4][0], remember ? 1 : 0);

        boolean result = db.replace(tableName, values) != -1;
        closeDB();

        return result;
    }

    public Map<String, Map<String, String>> load() {
        Cursor cursor = db.query(tableName, "1=1");

        if (cursor == null) {
            closeDB();
            return null;
        }

        Map<String, Map<String, String>> result = new HashMap<>();
        if (cursor.moveToFirst()) {
            do {
                try {
                    Map<String, String> temp = new HashMap<>();
                    String id = cursor.getString(cursor.getColumnIndex(TABLE_USER_FIELDS[1][0]));
                    temp.put(TABLE_USER_FIELDS[1][0], id);
                    temp.put(TABLE_USER_FIELDS[2][0], cursor.getString(cursor.getColumnIndex(TABLE_USER_FIELDS[2][0])));
                    temp.put(TABLE_USER_FIELDS[3][0], cursor.getString(cursor.getColumnIndex(TABLE_USER_FIELDS[3][0])));
                    temp.put(TABLE_USER_FIELDS[4][0], cursor.getString(cursor.getColumnIndex(TABLE_USER_FIELDS[4][0])));
                    result.put(id, temp);
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
            db.delete(tableName, TABLE_USER_FIELDS[1][0] + "='" + key + "'");
        else db.delete(tableName, "1=1");
        closeDB();
    }
}
