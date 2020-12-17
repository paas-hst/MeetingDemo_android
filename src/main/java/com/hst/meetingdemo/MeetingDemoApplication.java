package com.hst.meetingdemo;

import android.app.Application;
import android.content.Context;

import com.hst.meetingdemo.business.FspManager;
import com.hst.meetingdemo.utils.SystemUtils;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

public class MeetingDemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;

        Logger.addLogAdapter(new AndroidLogAdapter());

       /*
        // 启动延时太久，不再此处init
        String processAppName = SystemUtils.getAppName(this);
        if (processAppName == null || !processAppName.equals(this.getPackageName())) {
            return;
        }
        FspManager.getInstance().init();*/
    }

    public static MeetingDemoApplication sApplication;
}
