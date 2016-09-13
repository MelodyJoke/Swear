package com.teamsolo.swear.structure;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.teamsolo.base.template.application.BaseApplication;
import com.teamsolo.base.template.application.UncaughtExceptionHandler;
import com.teamsolo.base.util.LogUtility;
import com.teamsolo.swear.foundation.util.RetrofitConfig;

import static okhttp3.logging.HttpLoggingInterceptor.Level;

/**
 * description: application
 * author: Melody
 * date: 2016/8/22
 * version: 0.0.0.2
 */
@SuppressWarnings("unused")
public class Application extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        /**
         * config log util log mode
         * log in tomcat and save log file in {@link LogUtility#MODE_EAGER}
         * save log file only in {@link LogUtility#MODE_TEST}
         * save nothing in {@link LogUtility#MODE_SLUGGISH}
         */
        LogUtility.init(LogUtility.MODE_SLUGGISH);

        /**
         * config retrofit log mode
         * log if not in {@link LogUtility#MODE_SLUGGISH}
         * log if {@link LogUtility#releaseLog} is true while in {@link LogUtility#MODE_SLUGGISH}
         */
        if (LogUtility.getMode() == LogUtility.MODE_SLUGGISH && !LogUtility.releaseLog)
            RetrofitConfig.loggingInterceptor.setLevel(Level.NONE);
        else RetrofitConfig.loggingInterceptor.setLevel(Level.BODY);

        // init fresco
        Fresco.initialize(this);
    }

    @Override
    public UncaughtExceptionHandler initUncaughtExceptionHandler() {
        return new UncaughtExceptionHandler() {
            @Override
            protected void subPerform() {
                // TODO: do nothing now
            }
        };
    }
}
