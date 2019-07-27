package com.hst.meetingdemo.utils;

import android.app.ActivityManager;
import android.content.Context;

public class SystemUtils {

    public static String getAppName( Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
            try {
                if (appProcess.pid == pid) {
                    return appProcess.processName;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
