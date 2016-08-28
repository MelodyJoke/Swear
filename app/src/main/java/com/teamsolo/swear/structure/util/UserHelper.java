package com.teamsolo.swear.structure.util;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.teamsolo.swear.foundation.bean.Child;
import com.teamsolo.swear.foundation.bean.User;
import com.teamsolo.swear.foundation.constant.DbConst;
import com.teamsolo.swear.structure.util.db.CacheDbHelper;
import com.teamsolo.swear.structure.util.db.UserDbHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;

/**
 * description: handle user info
 * author: Melody
 * date: 2016/8/27
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class UserHelper {

    private static User mUser;

    private static long mUserId;

    private static byte mIsWXUser;

    private static int mAttentionGrade;

    private static ArrayList<Child> mChildren;

    private static Gson gson = new Gson();

    public static void saveUserInfo(@NotNull User user, @NotNull Context context) {
        // memory cache
        mUser = user;
        mUserId = user.userId;
        mIsWXUser = user.isWXUser;
        mAttentionGrade = user.attentionGrade;
        mChildren = user.children;

        // db cache
        CacheDbHelper helper = new CacheDbHelper(context);
        helper.save(DbConst.DB_USER_INFO, gson.toJson(user), "");
        helper.save(DbConst.DB_USER_ID, String.valueOf(user.userId), "");
        helper.save(DbConst.DB_USER_IS_WX_USER, String.valueOf(user.isWXUser), "");
        helper.save(DbConst.DB_USER_ATTENTION_GRADE, String.valueOf(user.attentionGrade), "");
        helper.save(DbConst.DB_USER_CHILDREN, gson.toJson(user.children), "");

        // save last user
        UserDbHelper dbHelper = new UserDbHelper(context);
        dbHelper.save(user.phone, user.password, user.parentPath, true);
    }

    public static User getUser(@NotNull Context context) {
        if (mUser == null) {
            Map<String, String> cacheMap = new CacheDbHelper(context).load(DbConst.DB_USER_INFO);
            if (cacheMap != null) {
                String cacheJson = cacheMap.get(DbConst.TABLE_CACHE_FIELDS[2][0]);
                if (!TextUtils.isEmpty(cacheJson))
                    mUser = new Gson().fromJson(cacheJson, User.class);
            }
        }

        return mUser;
    }

    public static long getUserId(@NotNull Context context) {
        if (mUserId <= 0) {
            Map<String, String> cacheMap = new CacheDbHelper(context).load(DbConst.DB_USER_ID);
            if (cacheMap != null) {
                String userId = cacheMap.get(DbConst.TABLE_CACHE_FIELDS[2][0]);
                if (!TextUtils.isEmpty(userId)) mUserId = Long.parseLong(userId);
            }
        }

        return mUserId;
    }

    public static boolean isWXUser(@NotNull Context context) {
        if (mIsWXUser <= 0) {
            Map<String, String> cacheMap = new CacheDbHelper(context).load(DbConst.DB_USER_IS_WX_USER);
            if (cacheMap != null) {
                String isWXUser = cacheMap.get(DbConst.TABLE_CACHE_FIELDS[2][0]);
                if (!TextUtils.isEmpty(isWXUser)) mIsWXUser = Byte.parseByte(isWXUser);
            }
        }

        return mIsWXUser == 1;
    }

    public static int getAttentionGrade(@NotNull Context context) {
        if (mAttentionGrade <= 0) {
            Map<String, String> cacheMap = new CacheDbHelper(context).load(DbConst.DB_USER_ATTENTION_GRADE);
            if (cacheMap != null) {
                String attentionGrade = cacheMap.get(DbConst.TABLE_CACHE_FIELDS[2][0]);
                if (!TextUtils.isEmpty(attentionGrade))
                    mAttentionGrade = Integer.parseInt(attentionGrade);
            }
        }

        return mAttentionGrade;
    }

    public static ArrayList<Child> getChildren(@NotNull Context context) {
        if (mChildren == null) {
            Map<String, String> cacheMap = new CacheDbHelper(context).load(DbConst.DB_USER_ATTENTION_GRADE);
            if (cacheMap != null) {
                String cacheJson = cacheMap.get(DbConst.TABLE_CACHE_FIELDS[2][0]);
                if (!TextUtils.isEmpty(cacheJson)) {
                    mChildren = gson.fromJson(cacheJson, new TypeToken<ArrayList<Child>>() {
                    }.getType());
                }
            }
        }

        return mChildren;
    }
}
