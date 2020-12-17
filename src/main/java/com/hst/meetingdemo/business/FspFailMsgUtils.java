package com.hst.meetingdemo.business;

import com.hst.fsp.FspEngine;


public class FspFailMsgUtils {
    public static String getFspEventDesc(int errCode) {
        switch (errCode) {
            case FspEngine.FSP_EVENT_CONNECT_LOST:
                return "重连失败，fsp连接断开";
            case FspEngine.FSP_EVENT_RECONNECT_START:
                return "网络断开过，开始重连";
            default:
                return "";
        }
    }

    public static String getErrorDesc(int errCode) {
        switch (errCode) {
            case FspEngine.ERR_INVALID_ARG:
                return "非法参数";
            case FspEngine.ERR_INVALID_STATE:
                return "非法状态";
            case FspEngine.ERR_OUTOF_MEMORY:
                return "内存不足";
            case FspEngine.ERR_DEVICE_FAIL:
                return "访问设备失败";
            case FspEngine.ERR_CONNECT_FAIL:
                return "网络连接失败";
            case FspEngine.ERR_NO_GROUP:
                return "没加入组";
            case FspEngine.ERR_TOKEN_INVALID:
                return "认证失败";
            case FspEngine.ERR_APP_NOT_EXIST:
                return "应用不存在";
            case FspEngine.ERR_USERID_CONFLICT:
                return "用户重复登录";
            case FspEngine.ERR_NOT_LOGIN:
                return "没有登录";
            case FspEngine.ERR_NO_BALANCE:
                return "账户余额不足";
            case FspEngine.ERR_NO_VIDEO_PRIVILEGE:
                return "没有视频权限";
            case FspEngine.ERR_NO_AUDIO_PRIVILEGE:
                return "没有音频权限";
            case FspEngine.ERR_SERVER_ERROR:
                return "服务内部错误";
            case FspEngine.ERR_FAIL:
                return "操作失败";
            default:
                return "系统错误";
        }
    }
}
