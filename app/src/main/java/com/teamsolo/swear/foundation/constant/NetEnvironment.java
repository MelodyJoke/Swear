package com.teamsolo.swear.foundation.constant;

import com.teamsolo.base.util.LogUtility;

/**
 * description: values about base url
 * author: Melody
 * date: 2016/8/22
 * version: 0.0.0.1
 */
@SuppressWarnings("unused, WeakerAccess")
public final class NetEnvironment {

    // to prevent new instance
    private NetEnvironment() {

    }

    /**
     * values in {@link LogUtility#MODE_EAGER} or {@link LogUtility#MODE_DEVELOP}
     */
    public static class Dev {

        public static final String BASE_HTTP_URL = "wenx.dev.xweisoft.com";

        public static final String BASE_HTTP_URL_FOR_APPLY = "papi.wenx.dev.xweisoft.com";

        public static final String BASE_HTTP_URL_FOR_KNOWLEDGE = "video.wenx.dev.xweisoft.com";

        public static final String BASE_HTTP_URL_FOR_KNOWLEDGE_RES = "v.wenx.dev.xweisoft.com";

        public static final String BASE_HTTPS_URL = "wenxdev.webprj.xweisoft.com:2222";

        public static final String BASE_UPLOAD_URL = "wenx.dev.xweisoft.com";

        public static final String MINA_URL = "prj.xweisoft.com";

        public static final int MINA_PORT = 2224;
    }

    /**
     * values in {@link LogUtility#MODE_TEST}
     */
    public static class Test {

        public static final String BASE_HTTP_URL = "wenxue.test.xweisoft.com";

        public static final String BASE_HTTP_URL_FOR_APPLY = "wenxue.test.xweisoft.com";

        public static final String BASE_HTTP_URL_FOR_KNOWLEDGE = "video.wenxue.test.xweisoft.com";

        public static final String BASE_HTTP_URL_FOR_KNOWLEDGE_RES = "v.wenxue.test.xweisoft.com";

        public static final String BASE_HTTPS_URL = "wenxtest.webprj.xweisoft.com:2223";

        public static final String BASE_UPLOAD_URL = "wenxue.test.xweisoft.com";

        public static final String MINA_URL = "push.xweisoft.com";

        public static final int MINA_PORT = 2223;
    }

    /**
     * values in {@link LogUtility#MODE_SLUGGISH} or {@link LogUtility#MODE_RELEASE}
     */
    public static class Release {

        public static final String BASE_HTTP_URL = "papi.xuewendao.com";

        public static final String BASE_HTTP_URL_FOR_APPLY = "papi.xuewendao.com";

        public static final String BASE_HTTP_URL_FOR_KNOWLEDGE = "video.xuewendao.com";

        public static final String BASE_HTTP_URL_FOR_KNOWLEDGE_RES = "v.xuewendao.com";

        public static final String BASE_HTTPS_URL = "papi.xuewendao.com";

        public static final String BASE_UPLOAD_URL = "pic.xuewendao.com";

        public static final String MINA_URL = "push.xuewendao.com";

        public static final int MINA_PORT = 20009;
    }
}
