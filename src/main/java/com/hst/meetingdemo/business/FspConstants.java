package com.hst.meetingdemo.business;

import com.hst.fsp.VideoProfile;

public class FspConstants {

    public static final int LOCAL_VIDEO_CLOSED = 0;   ///<本地视频关闭状态
    public static final int LOCAL_VIDEO_BACK_PUBLISHED = 1;  ///<广播了前置摄像头
    public static final int LOCAL_VIDEO_FRONT_PUBLISHED = 2;  ///<广播了后置摄像头


    public static final String PKEY_USER_APPID = "userAppId";
    public static final String PKEY_USER_APPSECRET = "userAppSecret";
    public static final String PKEY_USER_APPSERVERADDR = "userServerAddr";
    public static final String PKEY_USE_DEFAULT_APPCONFIG = "useDefaultAppConfig";
    public static final String PKEY_USE_DEFAULT_OPENCAMERA = "useDefaultOpenCamera";
    public static final String PKEY_USE_DEFAULT_OPENMIC = "useDefaultOpenMic";
    public static final String PKEY_IS_FORCELOGIN = "isForceLogin";
    public static final String PKEY_IS_RECVVOICEVARIANT = "isRecvVoiceVariant";


    // 为安全起见，App Secret最好不要在客户端保存
    public static final String DEFAULT_APP_ID = "925aa51ebf829d49fc98b2fca5d963bc";
    public static final String DEFAULT_APP_SECRET = "d52be60bb810d17e";
    public static final String DEFAULT_APP_ADDRESS = "";


    public static final VideoProfile DEFAULT_PROFILE = new VideoProfile(640, 480, 15);

}
