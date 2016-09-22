package com.teamsolo.swear.structure.util;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.teamsolo.swear.foundation.bean.Child;
import com.teamsolo.swear.foundation.bean.User;
import com.teamsolo.swear.foundation.constant.BroadcastConst;
import com.teamsolo.swear.foundation.constant.DbConst;
import com.teamsolo.swear.foundation.constant.SpConst;
import com.teamsolo.swear.structure.util.db.CacheDbHelper;
import com.teamsolo.swear.structure.util.db.UserDbHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

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

    private static Child mChild;

    private static long mStudentId;

    private static int mChildAttentionGrade;

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

    public static void saveChildInfo(@NotNull Child child, @NotNull Context context) {
        // memory cache
        mChild = child;
        mStudentId = child.studentId;

        // db cache
        CacheDbHelper helper = new CacheDbHelper(context);
        helper.save(DbConst.DB_CHILD_INFO, gson.toJson(child), "");
        helper.save(DbConst.DB_CHILD_ID, String.valueOf(child.studentId), "");

        // save last child
        getDefaultSharedPreferences(context).edit()
                .putLong(SpConst.LAST_CHILD, child.studentId).apply();
        helper.save(SpConst.LAST_CHILD, String.valueOf(child.studentId), "");

        setAttentionGrade(child.attentionGrade, context);
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

    private static int getAttentionGrade(@NotNull Context context) {
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
            Map<String, String> cacheMap = new CacheDbHelper(context).load(DbConst.DB_USER_CHILDREN);
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

    public static Child getChild(@NotNull Context context) {
        if (mChild == null) {
            Map<String, String> cacheMap = new CacheDbHelper(context).load(DbConst.DB_CHILD_INFO);
            if (cacheMap != null) {
                String cacheJson = cacheMap.get(DbConst.TABLE_CACHE_FIELDS[2][0]);
                if (!TextUtils.isEmpty(cacheJson))
                    mChild = new Gson().fromJson(cacheJson, Child.class);
            }
        }

        return mChild;
    }

    public static long getLastChildId(@NotNull Context context) {
        long studentId = PreferenceManager.getDefaultSharedPreferences(context).getLong(SpConst.LAST_CHILD, 0);
        if (studentId > 0) return studentId;

        Map<String, String> cacheMap = new CacheDbHelper(context).load(SpConst.LAST_CHILD);
        if (cacheMap != null) {
            String studentIdStr = cacheMap.get(DbConst.TABLE_CACHE_FIELDS[2][0]);
            if (!TextUtils.isEmpty(studentIdStr)) studentId = Long.parseLong(studentIdStr);
        }

        return studentId;
    }

    public static long getStudentId(@NotNull Context context) {
        if (mStudentId <= 0) {
            Map<String, String> cacheMap = new CacheDbHelper(context).load(DbConst.DB_CHILD_ID);
            if (cacheMap != null) {
                String studentId = cacheMap.get(DbConst.TABLE_CACHE_FIELDS[2][0]);
                if (!TextUtils.isEmpty(studentId)) mStudentId = Long.parseLong(studentId);
            }
        }

        return mStudentId;
    }

    private static int getChildAttentionGrade(@NotNull Context context) {
        if (mChildAttentionGrade <= 0) {
            Map<String, String> cacheMap = new CacheDbHelper(context).load(DbConst.DB_CHILD_ATTENTION_GRADE);
            if (cacheMap != null) {
                String attentionGrade = cacheMap.get(DbConst.TABLE_CACHE_FIELDS[2][0]);
                if (!TextUtils.isEmpty(attentionGrade))
                    mChildAttentionGrade = Integer.parseInt(attentionGrade);
            }
        }

        return mChildAttentionGrade;
    }

    public static int getRealAttentionGrade(@NotNull Context context) {
        int childAttentionGrade = getChildAttentionGrade(context);
        if (childAttentionGrade > 0) return childAttentionGrade;

        int attentionGrade = getAttentionGrade(context);
        if (attentionGrade > 0) return attentionGrade;

        return 0;
    }

    private static void setAttentionGrade(int attentionGrade, Context context) {
        int lastOne = getRealAttentionGrade(context);
        if (attentionGrade == lastOne) return;

        mChildAttentionGrade = attentionGrade;

        CacheDbHelper helper = new CacheDbHelper(context);
        helper.save(DbConst.DB_CHILD_ATTENTION_GRADE, String.valueOf(attentionGrade), "");

        Intent intent = new Intent(BroadcastConst.BC_ATTENTION_GRADE_CHANGE);
        intent.putExtra("attention", attentionGrade);
        context.sendBroadcast(intent);
    }

    public static void clear(@NotNull Context context) {
        // clear memory cache
        mUser = null;
        mUserId = 0;
        mIsWXUser = 0;
        mAttentionGrade = 0;
        mChildren = null;

        mChild = null;
        mStudentId = 0;
        mChildAttentionGrade = 0;

        // clear db cache
        CacheDbHelper helper = new CacheDbHelper(context);
        helper.save(DbConst.DB_USER_INFO, "", "");
        helper.save(DbConst.DB_USER_ID, "0", "");
        helper.save(DbConst.DB_USER_IS_WX_USER, "0", "");
        helper.save(DbConst.DB_USER_ATTENTION_GRADE, "0", "");
        helper.save(DbConst.DB_USER_CHILDREN, "", "");

        helper.save(DbConst.DB_CHILD_INFO, "", "");
        helper.save(DbConst.DB_CHILD_ID, "0", "");
        helper.save(DbConst.DB_CHILD_ATTENTION_GRADE, "0", "");
    }
}
