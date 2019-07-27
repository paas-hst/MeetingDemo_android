package com.hst.meetingdemo.bean;

/**
 * 通知主界面显示事件信息的event
 */
public class EventMsgEntity {
    private String userId;
    private String msg;

    public EventMsgEntity(String userId, String msg) {
        this.userId = userId;
        this.msg = msg;
    }

    public String getUserId() {
        return userId;
    }

    public String getMsg() {
        return msg;
    }
}
