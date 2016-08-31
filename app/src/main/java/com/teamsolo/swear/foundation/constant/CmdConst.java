package com.teamsolo.swear.foundation.constant;

/**
 * description: cmd values
 * author: Melody
 * date: 2016/8/22
 * version: 0.0.0.1
 */
@SuppressWarnings("unused")
public interface CmdConst {

    // load
    String CMD_GET_START = "getStartFigure";

    // attention grade
    String CMD_ATTENTION = "getDefaultGradeAndAttention";

    // name modify
    String CMD_NAME = "updatePersonalMessage";

    // appellation modify
    String CMD_APPELLATION = "updateAppellation";

    // attention grade
    String CMD_ATTENTION_GRADE = "attentionGrade";

    // get accounts
    String CMD_GET_ACCOUNTS = "getParentList";

    // add an account
    String CMD_ADD_ACCOUNT = "saveParent";

    // update an account
    String CMD_UPDATE_ACCOUNT = "updateParent";

    // delete an account
    String CMD_DELETE_ACCOUNT = "deleteParent";

    // get schedule
    String CMD_SCHEDULE = "getClassinfoLesson";

    // get school activities
    String CMD_GET_ACTIVITIES = "getActivityList";

    // get orders
    String CMD_GET_ORDERS = "getorderlist";
}
