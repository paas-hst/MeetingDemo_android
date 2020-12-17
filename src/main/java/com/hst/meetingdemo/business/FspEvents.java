package com.hst.meetingdemo.business;

import android.os.Parcel;
import android.os.Parcelable;

import com.hst.fsp.FspUserInfo;
import com.hst.fsp.WhiteBoardInfo;

public class FspEvents {

    public static class LoginResult {
        public boolean isSuccess;
        public String desc;

        public LoginResult(boolean isSuccess, String desc) {
            this.isSuccess = isSuccess;
            this.desc = desc;
        }
    }
    public static class JoinGroupResult {
        public boolean isSuccess;
        public String desc;

        public JoinGroupResult(boolean isSuccess, String desc) {
            this.isSuccess = isSuccess;
            this.desc = desc;
        }
    }

  public static class LeaveGroupResult {
        public boolean isSuccess;
        public String desc;

        public LeaveGroupResult(boolean isSuccess, String desc) {
            this.isSuccess = isSuccess;
            this.desc = desc;
        }
    }

    public static class RemoteVideoEvent {
        public String userid;
        public String videoid;
        public int eventtype;

        public RemoteVideoEvent(String userid, String videoid, int eventtype) {
            this.userid = userid;
            this.videoid = videoid;
            this.eventtype = eventtype;
        }
    }

    public static class RemoteAudioEvent {
        public String userid;
        public int eventtype;

        public RemoteAudioEvent(String userid, int eventtype) {
            this.userid = userid;
            this.eventtype = eventtype;
        }
    }

    public static class RemoteUserEvent
    {
        public String userid;
        public int eventtype;

        public RemoteUserEvent(String userid, int eventtype) {
            this.userid = userid;
            this.eventtype = eventtype;
        }
    }

    public static class RefreshUserStatusFinished {
        public boolean isSuccess;
        public int requestId;
        public FspUserInfo[] infos;
        public String desc;

        public RefreshUserStatusFinished(boolean isSuccess, int requestId, FspUserInfo[] infos, String desc) {
            this.isSuccess = isSuccess;
            this.requestId = requestId;
            this.infos = infos;
            this.desc = desc;
        }
    }

    public static class InviteIncome implements Parcelable {
        public String inviterUserId;
        public int inviteId;
        public String groupId;
        public String desc;

        public InviteIncome(String inviterUserId, int inviteId, String groupId, String desc) {
            this.inviterUserId = inviterUserId;
            this.inviteId = inviteId;
            this.groupId = groupId;
            this.desc = desc;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.inviterUserId);
            dest.writeInt(this.inviteId);
            dest.writeString(this.groupId);
            dest.writeString(this.desc);
        }

        protected InviteIncome(Parcel in) {
            this.inviterUserId = in.readString();
            this.inviteId = in.readInt();
            this.groupId = in.readString();
            this.desc = in.readString();
        }

        public static final Parcelable.Creator<InviteIncome> CREATOR = new Parcelable.Creator<InviteIncome>() {
            @Override
            public InviteIncome createFromParcel(Parcel source) {
                return new InviteIncome(source);
            }

            @Override
            public InviteIncome[] newArray(int size) {
                return new InviteIncome[size];
            }
        };
    }

    public static class ChatMsgItem {
        public String srcUserId;
        public int msgId;
        public String msg;
        public boolean isGroupMsg;
        public boolean isMyselfMsg;

        public ChatMsgItem(boolean isGroupMsg, String srcUserId, int msgId, String msg, boolean isMyselfMsg) {
            this.isGroupMsg = isGroupMsg;
            this.srcUserId = srcUserId;
            this.msgId = msgId;
            this.msg = msg;
            this.isMyselfMsg = isMyselfMsg;
        }
    }

    public static class WhiteBoardPublishEvent
    {
        public String boardId;
        public String boardName;
        public boolean isStop;

        public WhiteBoardPublishEvent(boolean isStop, String boardId, String boardName) {
            this.isStop = isStop;
            this.boardId = boardId;
            this.boardName = boardName;
        }
    }

    public static class WhiteBoardInfoUpdateEvent
    {
        public String boardId;
        public WhiteBoardInfo info;

        public WhiteBoardInfoUpdateEvent(String boardId, WhiteBoardInfo info) {
            this.boardId = boardId;
            this.info = info;
        }
    }
}
