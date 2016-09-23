package com.teamsolo.swear.foundation.constant;

/**
 * description: database constant values
 * author: Melody
 * date: 2016/8/12
 * version: 0.0.0.1
 */
@SuppressWarnings("unused")
public interface DbConst {

    /**
     * database version
     * ver1: cache table
     * ver2: user table
     */
    int DB_VERSION = 2;

    String DB_USER_INFO = "db_user_info";

    String DB_USER_ID = "db_user_id";

    String DB_USER_IS_WX_USER = "db_user_is_wx_user";

    String DB_USER_ATTENTION_GRADE = "db_user_grade_id";

    String DB_USER_CHILDREN = "db_user_children";

    String DB_CHILD_INFO = "db_child_info";

    String DB_CHILD_ID = "db_child_id";

    String DB_CHILD_ATTENTION_GRADE = "db_child_grade_id";

    String DB_GRADE_TYPES = "db_grade_types";

    String TABLE_CACHE = "table_cache";

    String[][] TABLE_CACHE_FIELDS = {
            {"id", "integer primary key autoincrement"},
            {"name", "text not null unique"},
            {"cache", "text"},
            {"spare", "text"},
            {"reserve", "text"}
    };

    String TABLE_USER = "table_user";

    String[][] TABLE_USER_FIELDS = {
            {"id", "integer primary key autoincrement"},
            {"phone", "text not null unique"},
            {"password", "text"},
            {"portrait", "text"},
            {"remember", "integer default 1"},
            {"reserve", "text"}
    };
}
