package com.teamsolo.swear.foundation.constant;

import com.teamsolo.base.util.LogUtility;

/**
 * description: http const values
 * author: Melody
 * date: 2016/8/22
 * version: 0.0.0.1
 */
@SuppressWarnings("unused")
public class NetConst {

    public static final String HTTP = "http://", HTTPS = "https://";

    // path prefix
    public static final String PATH_PRE = "/pc_secureproxy/";

    // upload parent portrait
    public static final String PARENT_PORTRAIT_UPLOAD = "/resource_server/uploadfile/uploadParentPortrait";

    // upload child portrait
    public static final String CHILD_PORTRAIT_UPLOAD = "/resource_server/uploadfile/uploadStudentPortrait";

    // upload file
    public static final String FILE_UPLOAD = "/resource_server/uploadfile/uploadfiles";

    // upload file about IM
    public static final String FILE_IM_UPLOAD = "/resource_server/uploadfile/uploadParentsChatMsg";

    // Login
    public static final String LOGIN_URL = "parent/login";

    // service
    public static final String SERVICE_URL = "service/action";

    // help
    public static final String HELP_CENTER = "/scms/resources/helpdoc/index_web.html";

    // agreement
    public static final String AGREEMENT = "/client_resource/parent/aboutUs/page/about_us.html";

    // choose child
    public static final String CHILD_CHOOSE = "parent/studentSelect";

    // register
    public static final String REGISTER = "regist_resource/mobile/ph_register.html?hiddenTop=1";

    // school register
    public static final String REGISTER_SCHOOL = "regist_resource/mobile/ph_my_register.html";

    // news share url
    public static final String NEWS_SHARE_URL = "resource/page/info_detail.html?";

    // bonus point url
    public static final String BONUS_POINT_URL = "resource/sign/index.html";

    // member index url
    public static final String MEMBER_INDEX_URL = "resource/sign/member.html";

    public static String getBaseHttpUrl() {
        switch (LogUtility.getMode()) {
            case LogUtility.MODE_EAGER:
                return NetEnvironment.Dev.BASE_HTTP_URL;
            case LogUtility.MODE_TEST:
                return NetEnvironment.Test.BASE_HTTP_URL;
            case LogUtility.MODE_SLUGGISH:
                return NetEnvironment.Release.BASE_HTTP_URL;
            default:
                return NetEnvironment.Dev.BASE_HTTP_URL;
        }
    }

    public static String getBaseHttpUrlForApply() {
        switch (LogUtility.getMode()) {
            case LogUtility.MODE_EAGER:
                return NetEnvironment.Dev.BASE_HTTP_URL_FOR_APPLY;
            case LogUtility.MODE_TEST:
                return NetEnvironment.Test.BASE_HTTP_URL_FOR_APPLY;
            case LogUtility.MODE_SLUGGISH:
                return NetEnvironment.Release.BASE_HTTP_URL_FOR_APPLY;
            default:
                return NetEnvironment.Dev.BASE_HTTP_URL_FOR_APPLY;
        }
    }

    public static String getBaseHttpUrlForKnowledge() {
        switch (LogUtility.getMode()) {
            case LogUtility.MODE_EAGER:
                return NetEnvironment.Dev.BASE_HTTP_URL_FOR_KNOWLEDGE;
            case LogUtility.MODE_TEST:
                return NetEnvironment.Test.BASE_HTTP_URL_FOR_KNOWLEDGE;
            case LogUtility.MODE_SLUGGISH:
                return NetEnvironment.Release.BASE_HTTP_URL_FOR_KNOWLEDGE;
            default:
                return NetEnvironment.Dev.BASE_HTTP_URL_FOR_KNOWLEDGE;
        }
    }

    public static String getBaseHttpUrlForKnowledgeRes() {
        switch (LogUtility.getMode()) {
            case LogUtility.MODE_EAGER:
                return NetEnvironment.Dev.BASE_HTTP_URL_FOR_KNOWLEDGE_RES;
            case LogUtility.MODE_TEST:
                return NetEnvironment.Test.BASE_HTTP_URL_FOR_KNOWLEDGE_RES;
            case LogUtility.MODE_SLUGGISH:
                return NetEnvironment.Release.BASE_HTTP_URL_FOR_KNOWLEDGE_RES;
            default:
                return NetEnvironment.Dev.BASE_HTTP_URL_FOR_KNOWLEDGE_RES;
        }
    }

    public static String getBaseHttpUrlForFollow() {
        switch (LogUtility.getMode()) {
            case LogUtility.MODE_EAGER:
                return NetEnvironment.Dev.BASE_HTTP_URL_FOR_FOLLOW;
            case LogUtility.MODE_TEST:
                return NetEnvironment.Test.BASE_HTTP_URL_FOR_FOLLOW;
            case LogUtility.MODE_SLUGGISH:
                return NetEnvironment.Release.BASE_HTTP_URL_FOR_FOLLOW;
            default:
                return NetEnvironment.Dev.BASE_HTTP_URL_FOR_FOLLOW;
        }
    }

    public static String getBaseHttpsUrl() {
        switch (LogUtility.getMode()) {
            case LogUtility.MODE_EAGER:
                return NetEnvironment.Dev.BASE_HTTPS_URL;
            case LogUtility.MODE_TEST:
                return NetEnvironment.Test.BASE_HTTPS_URL;
            case LogUtility.MODE_SLUGGISH:
                return NetEnvironment.Release.BASE_HTTPS_URL;
            default:
                return NetEnvironment.Dev.BASE_HTTPS_URL;
        }
    }

    public static String getUploadUrl() {
        switch (LogUtility.getMode()) {
            case LogUtility.MODE_EAGER:
                return NetEnvironment.Dev.BASE_UPLOAD_URL;
            case LogUtility.MODE_TEST:
                return NetEnvironment.Test.BASE_UPLOAD_URL;
            case LogUtility.MODE_SLUGGISH:
                return NetEnvironment.Release.BASE_UPLOAD_URL;
            default:
                return NetEnvironment.Dev.BASE_UPLOAD_URL;
        }
    }

    public static String getMinaUrl() {
        switch (LogUtility.getMode()) {
            case LogUtility.MODE_EAGER:
                return NetEnvironment.Dev.MINA_URL;
            case LogUtility.MODE_TEST:
                return NetEnvironment.Test.MINA_URL;
            case LogUtility.MODE_SLUGGISH:
                return NetEnvironment.Release.MINA_URL;
            default:
                return NetEnvironment.Dev.MINA_URL;
        }
    }

    public static int getMinaPort() {
        switch (LogUtility.getMode()) {
            case LogUtility.MODE_EAGER:
                return NetEnvironment.Dev.MINA_PORT;
            case LogUtility.MODE_TEST:
                return NetEnvironment.Test.MINA_PORT;
            case LogUtility.MODE_SLUGGISH:
                return NetEnvironment.Release.MINA_PORT;
            default:
                return NetEnvironment.Dev.MINA_PORT;
        }
    }
}
