package com.teamsolo.swear.foundation.bean;

import android.content.Intent;
import android.os.Parcel;
import android.util.SparseArray;

import com.teamsolo.base.bean.Bean;

/**
 * description: school category bean
 * author: Melody
 * date: 2016/9/11
 * version: 0.0.0.1
 */
@SuppressWarnings("WeakerAccess, unused")
public class Category extends Bean {

    public static SparseArray<Category> cache = new SparseArray<>();

    public int key;

    public String title;

    public int resourceId;

    public Intent intent;

    public Category() {

    }

    private Category(Parcel in) {
        key = in.readInt();
        title = in.readString();
        resourceId = in.readInt();
        intent = in.readParcelable(Intent.class.getClassLoader());
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel source) {
            return new Category(source);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(key);
        dest.writeString(title);
        dest.writeInt(resourceId);
        dest.writeParcelable(intent, flags);
    }

    public static Category generateCategory(int key, String title, int resourceId, Intent intent) {
        if (cache.get(key) == null) {
            Category category = new Category();
            category.key = key;
            category.title = title;
            category.resourceId = resourceId;
            category.intent = intent;

            cache.put(key, category);
        }

        return cache.get(key);
    }
}
