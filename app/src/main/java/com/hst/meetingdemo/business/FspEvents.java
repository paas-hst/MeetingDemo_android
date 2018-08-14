package com.hst.meetingdemo.business;

public class FspEvents {

    public static class JoinGroupResult
    {
        public JoinGroupResult(boolean isSucess, String desc) {
            this.isSucess = isSucess;
            this.desc = desc;
        }
        public boolean isSucess;
        public String desc;
    }

    public static class RemoteVideoEvent
    {
        public RemoteVideoEvent(String userid, String videoid, int eventtype) {
            this.userid = userid;
            this.videoid = videoid;
            this.eventtype = eventtype;
        }
        public String userid;
        public String videoid;
        public int eventtype;
    }

    public static class RemoteAudioEvent
    {
        public RemoteAudioEvent(String userid,int eventtype) {
            this.userid = userid;
            this.eventtype = eventtype;
        }
        public String userid;
        public int eventtype;
    }
}
