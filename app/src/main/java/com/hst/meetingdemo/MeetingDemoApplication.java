package com.hst.meetingdemo;

import android.app.Application;

import com.hst.meetingdemo.business.FspManager;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

/**
 * Created by liujc on 2018/2/6.
 * applicationi
 */

public class MeetingDemoApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;

        Logger.addLogAdapter(new AndroidLogAdapter());

        FspManager.instatnce().init();
    }

    static public MeetingDemoApplication sApplication;
}
