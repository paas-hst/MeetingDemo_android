package com.hst.meetingdemo.business;

import android.text.TextUtils;
import android.util.Pair;
import android.view.SurfaceView;

import com.hst.fsp.FspEngine;
import com.hst.fsp.FspUserInfo;
import com.hst.fsp.FspEngineConfigure;
import com.hst.fsp.IFspEngineEventHandler;
import com.hst.fsp.IFspSignalingEventHandler;
import com.hst.fsp.VideoProfile;
import com.hst.fsp.VideoStatsInfo;
import com.hst.fsp.tools.FspToken;
import com.hst.meetingdemo.MeetingDemoApplication;
import com.hst.meetingdemo.bean.EventMsgEntity;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;


public class FspManager implements IFspEngineEventHandler, IFspSignalingEventHandler {

    private FspEngine m_fspEngine = null;
    private boolean m_haveInitEngine = false;

    private boolean m_strAppConfig;
    private String m_strAppid;
    private String m_strAppSecrectKey;
    private String m_strAppSecrectAddr;

    private String m_SelfUserId = null;
    private String m_SelfGroupId = null;

    private int m_LocalVideoState = FspConstants.LOCAL_VIDEO_CLOSED;
    private boolean m_isVideoPublished = false;
    private boolean m_isAudioPublished = false;

    private VideoProfile m_profile = FspConstants.DEFAULT_PROFILE;

    private HashSet<String> m_remoteAudios = new HashSet<>();
    private HashSet<Pair<String, String>> m_remoteVideos = new HashSet<>();

    private List<String> m_groupUserIds = new LinkedList<>();

    private static volatile FspManager s_instance = null;

    private FspManager() {
    }

    public static FspManager getInstance() {
        if (s_instance == null) {
            synchronized (FspManager.class) {
                if (s_instance == null) {
                    s_instance = new FspManager();
                }
            }
        }
        return s_instance;
    }

    // --------------------------- get start --------------------------
    public void setSelfUserId(String selfUserId) {
        m_SelfUserId = selfUserId;
    }

    public String getSelfUserId() {
        return m_SelfUserId == null ? "" : m_SelfUserId;
    }

    public String getSelfGroupId() {
        return m_SelfGroupId == null ? "" : m_SelfGroupId;
    }

    public void setSelfGroupId(String selfGroupId) {
        m_SelfGroupId = selfGroupId;
    }

    public HashSet<Pair<String, String>> getRemoteVideos() {
        return m_remoteVideos;
    }

    public HashSet<String> getRemoteAudios() {
        return m_remoteAudios;
    }

    public boolean HaveUserVideo(String userid) {
        for (Pair<String, String> pair : m_remoteVideos){
            if (pair.first.equals(userid) && !pair.second.equals(FspEngine.RESERVED_VIDEOID_SCREENSHARE)) {
                return true;
            }
        }
        return false;
    }

    public boolean HaveUserScreenShare(String userid) {
        for (Pair<String, String> pair : m_remoteVideos){
            if (pair.first.equals(userid) && pair.second.equals(FspEngine.RESERVED_VIDEOID_SCREENSHARE)) {
                return true;
            }
        }
        return false;
    }

    public boolean HaveUserAudio(String userid) {
        return m_remoteAudios.contains(userid);
    }

    public String getVersion() {
        return m_fspEngine == null ? null : m_fspEngine.getVersion();
    }

    public int getSpeakerEnergy() {
        return m_fspEngine == null ? 0 : m_fspEngine.getSpeakerEnergy();
    }

    public int getMicrophoneEnergy() {
        return m_fspEngine == null ? 0 : m_fspEngine.getMicrophoneEnergy();
    }

    public int getRemoteAudioEnergy(String userId) {
        return m_fspEngine == null ? 0 : m_fspEngine.getRemoteAudioEnergy(userId);
    }

    // --------------------------- get end --------------------------


    // --------------------------- checkAppConfigChange start --------------------------

    public boolean checkAppConfigChange() {
        boolean appConfig = FspPreferenceManager.getInstance().getAppConfig();
        String strAppid = FspPreferenceManager.getInstance().getAppId();
        String strAppSecrectKey = FspPreferenceManager.getInstance().getAppSecret();
        String strAppSecrectAddr = FspPreferenceManager.getInstance().getAppServerAddr();

        Logger.d("appConfig: " + appConfig + " appId: " + strAppid + " appSecret: " + strAppSecrectKey + " serverAddr: " + strAppSecrectAddr);

        if (!appConfig && (strAppid.isEmpty() || strAppSecrectKey.isEmpty() || strAppSecrectAddr.isEmpty())) {
            return false;
        }

        if (m_haveInitEngine) { // has init
            // default
            if (m_strAppConfig && !appConfig) { // first default ,current nonDefault
                destroyEngine();
            } else if (!m_strAppConfig) {// user
                if (appConfig) {// first nooDefault ,current default
                    destroyEngine();
                } else { // first noDefault ,current noDefault  有一个参数不一致就需要reset，并重新init
                    if (!strAppid.equals(m_strAppid) ||
                            !strAppSecrectKey.equals(m_strAppSecrectKey) ||
                            !strAppSecrectAddr.equals(m_strAppSecrectAddr)) {
                        destroyEngine();
                    }
                }
            }
        }
        return true;
    }

    private void destroyEngine() {
        Logger.d("destroyEngine: ");
        if (m_fspEngine != null) {
            m_fspEngine.destroy();
            m_fspEngine = null;
            m_haveInitEngine = false;
        }
    }
    // --------------------------- checkAppConfigChange end --------------------------

    public boolean init() {
        if (m_haveInitEngine) {
            return true;
        }

        boolean AppConfig = FspPreferenceManager.getInstance().getAppConfig();
        String appId = "";
        String appSecret = "";
        String serverAddr = "";

        if (AppConfig) {
            appId = FspConstants.DEFAULT_APP_ID;
            appSecret = FspConstants.DEFAULT_APP_SECRET;
            serverAddr = FspConstants.DEFAULT_APP_ADDRESS;
        } else {
            appId = FspPreferenceManager.getInstance().getAppId();
            appSecret = FspPreferenceManager.getInstance().getAppSecret();
            serverAddr = FspPreferenceManager.getInstance().getAppServerAddr();
        }
        Logger.d("appId: " + appId + " appSecret: " + appSecret + " serverAddr: " + serverAddr);

        FspEngineConfigure configure = new FspEngineConfigure();
        configure.serverAddr = serverAddr;
        configure.hardwareEncNumber = 1;
        configure.hardwareDecNumber = 0;

        if (m_fspEngine == null) {
            m_fspEngine = FspEngine.create(MeetingDemoApplication.sApplication, appId, configure, this);
        }

        boolean result = m_fspEngine.init() == FspEngine.ERR_OK;
        Logger.d("init is success : " + result);

        if (result) {
            m_haveInitEngine = true;
            m_strAppConfig = AppConfig;
            m_strAppid = appId;
            m_strAppSecrectKey = appSecret;
            m_strAppSecrectAddr = serverAddr;
            m_fspEngine.getFspSignaling().addEventHandler(this);
            return true;
        } else {
            destroyEngine();
            return false;
        }
    }

    public void clear() {
        m_remoteAudios.clear();
        m_remoteVideos.clear();
        m_groupUserIds.clear();
    }

    public void destroy() {
        clear();
        if (m_fspEngine != null) {
            m_fspEngine.getFspSignaling().removeEventHandler(this);
            m_fspEngine.leaveGroup();
            m_fspEngine.destroy();
            m_fspEngine = null;
            m_haveInitEngine = false;
        }
    }

    public boolean login(String userId) {
        if (!m_haveInitEngine) {
            return false;
        }

        String token = FspToken.build(m_strAppid, m_strAppSecrectKey, userId);
        boolean result = m_fspEngine.login(token, userId) == FspEngine.ERR_OK;
        if (result) {
            m_SelfUserId = userId;
        }
        Logger.d("login is success : " + result);
        return result;
    }

    public boolean loginOut() {
        if (m_fspEngine == null) {
            return false;
        }

        boolean result = m_fspEngine.loginOut() == FspEngine.ERR_OK;
        if (result) {
            m_SelfUserId = null;
        }
        Logger.d("loginOut is success : " + result);
        return result;
    }

    public boolean leaveGroup() {
        if (m_fspEngine == null) {
            return false;
        }
        boolean result = m_fspEngine.leaveGroup() == FspEngine.ERR_OK;
        Logger.d("leaveGroup is success : " + result);
        if (result) {
            clear();
        }
        return result;
    }

    public boolean joinGroup(String groupId) {
        if (m_fspEngine == null) {
            return false;
        }
        boolean result = m_fspEngine.joinGroup(groupId) == FspEngine.ERR_OK;
        if (result) {
            m_SelfGroupId = groupId;
        }
        Logger.d("joinGroup is success : " + result);
        return result;
    }

    public boolean publishVideo(boolean isFrontCamera, SurfaceView previewRender) {
        if (m_fspEngine == null) {
            return false;
        }

        int fspErrCode = m_fspEngine.setVideoProfile(m_profile);
        if (fspErrCode != FspEngine.ERR_OK) {
            return false;
        }

        fspErrCode = m_fspEngine.startPreviewVideo(previewRender);
        if (fspErrCode != FspEngine.ERR_OK) {
            return false;
        }

        if (m_fspEngine.isFrontCamera() != isFrontCamera) {
            m_fspEngine.switchCamera();
        }

        if (isFrontCamera) {
            m_LocalVideoState = FspConstants.LOCAL_VIDEO_FRONT_PUBLISHED;
        } else {
            m_LocalVideoState = FspConstants.LOCAL_VIDEO_BACK_PUBLISHED;
        }

        fspErrCode = m_fspEngine.startPublishVideo();
        if (fspErrCode != FspEngine.ERR_OK) {
            return false;
        }
        m_isVideoPublished = true;
        return true;
    }

    public void switchCamera() {
        if (m_fspEngine == null) {
            return;
        }
        m_fspEngine.switchCamera();
        if (m_LocalVideoState != FspConstants.LOCAL_VIDEO_CLOSED) {
            if (m_fspEngine.isFrontCamera()) {
                m_LocalVideoState = FspConstants.LOCAL_VIDEO_FRONT_PUBLISHED;
            } else {
                m_LocalVideoState = FspConstants.LOCAL_VIDEO_BACK_PUBLISHED;
            }
        }
    }

    public boolean stopVideoPublish() {
        if (m_fspEngine == null) {
            return false;
        }

        m_fspEngine.stopPublishVideo();
        m_fspEngine.stopPreviewVideo();

        m_LocalVideoState = FspConstants.LOCAL_VIDEO_CLOSED;
        m_isVideoPublished = false;
        return true;
    }

    public VideoStatsInfo getVideoStats(String userid, String videoid) {
        return m_fspEngine != null ? m_fspEngine.getVideoStats(userid, videoid) : null;
    }

    /**
     * 当前本地视频状态
     *
     * @return LOCAL_VIDEO_CLOSED or LOCAL_VIDEO_BACK_PUBLISHED or LOCAL_VIDEO_FRONT_PUBLISHED
     */
    public int currentVideState() {
        return m_LocalVideoState;
    }


    public boolean setRemoteVideoRender(String userId, String videoId,
                                        SurfaceView renderView, int renderMode) {
        if (m_fspEngine == null) {
            return false;
        }
        int fspErrCode = m_fspEngine.setRemoteVideoRender(userId, videoId, renderView, renderMode);

        return fspErrCode == FspEngine.ERR_OK;
    }

    public boolean startPublishAudio() {
        if (m_fspEngine == null) {
            return false;
        }
        int fspErrCode = m_fspEngine.startPublishAudio();
        if (fspErrCode == FspEngine.ERR_OK) {
            m_isAudioPublished = true;
        }
        return fspErrCode == FspEngine.ERR_OK;
    }

    public boolean stopPublishAudio() {
        if (m_fspEngine == null) {
            return false;
        }
        int fspErrCode = m_fspEngine.stopPublishAudio();
        if (fspErrCode == FspEngine.ERR_OK) {
            m_isAudioPublished = false;
        }
        return fspErrCode == FspEngine.ERR_OK;
    }

    public List<String> getGroupUsers() {return m_groupUserIds;}

    public boolean isAudioPublishing() {
        return m_isAudioPublished;
    }

    public boolean isVideoPublishing() {
        return m_isVideoPublished;
    }

    public VideoProfile getCurrentProfile() {
        return m_profile;
    }

    public void setProfile(VideoProfile profile) {
        m_profile = profile;
        if (m_fspEngine == null) {
            return;
        }
        m_fspEngine.setVideoProfile(m_profile);
    }


    // ---------------------- signing start ------------------------
    public boolean refreshAllUserStatus() {
        if (m_fspEngine == null) {
            return false;
        }

        int errCode = m_fspEngine.getFspSignaling().refreshAllUserStatus();
        return errCode == FspEngine.ERR_OK;
    }

    public boolean invite(String[] userId, String groupId, String msg) {
        if (m_fspEngine == null) {
            return false;
        }
        return m_fspEngine.getFspSignaling().invite(userId, groupId, msg) == FspEngine.ERR_OK;
    }

    public boolean acceptInvite(String inviterUserId, int inviteId) {
        if (m_fspEngine == null) {
            return false;
        }
        int errCode = m_fspEngine.getFspSignaling().acceptInvite(inviterUserId, inviteId);
        return errCode == FspEngine.ERR_OK;
    }

    public boolean rejectInvite(String inviterUserId, int inviteId) {
        if (m_fspEngine == null) {
            return false;
        }
        return m_fspEngine.getFspSignaling().rejectInvite(inviterUserId, inviteId) == FspEngine.ERR_OK;
    }

    public boolean sendUserMsg(String userId, String msg) {
        if (m_fspEngine == null) {
            return false;
        }
        int errCode = m_fspEngine.getFspSignaling().sendUserMsg(userId, msg);
        Logger.d("sendUserMsg: userId: " + userId + " errCode: " + errCode);
        return errCode == FspEngine.ERR_OK;
    }

    public boolean sendGroupMsg(String msg) {
        if (m_fspEngine == null) {
            return false;
        }
        int errCode = m_fspEngine.getFspSignaling().sendGroupMsg(msg);
        Logger.d("sendGroupMsg: " + " errCode: " + errCode);
        return errCode == FspEngine.ERR_OK;
    }
    // ---------------------- signing end ------------------------


    // -----------------   IFspEngineEventHandler  start  ------------------------
    @Override
    public void onLoginResult(int errCode) {
        Logger.d("errCode:" + errCode);
        if (errCode != FspEngine.ERR_OK) {
            setSelfUserId(null);
        }
        EventBus.getDefault().post(new FspEvents.LoginResult(errCode == FspEngine.ERR_OK, FspFailMsgUtils.getErrorDesc(errCode)));
    }

    @Override
    public void onJoinGroupResult(int errCode) {
        Logger.d("errCode:" + errCode);
        if (errCode != FspEngine.ERR_OK) {
            setSelfGroupId(null);
        }
        EventBus.getDefault().post(new FspEvents.JoinGroupResult(errCode == FspEngine.ERR_OK, FspFailMsgUtils.getErrorDesc(errCode)));
    }

    @Override
    public void onLeaveGroupResult(int errCode) {
        Logger.d("errCode:" + errCode);
        if (errCode == FspEngine.ERR_OK) {
            setSelfGroupId(null);
        }
        EventBus.getDefault().post(new FspEvents.LeaveGroupResult(errCode == FspEngine.ERR_OK, FspFailMsgUtils.getErrorDesc(errCode)));
    }

    @Override
    public void onFspEvent(int eventType) {
        Logger.d("eventType:" + eventType);
        EventBus.getDefault().post(new EventMsgEntity("", FspFailMsgUtils.getFspEventDesc(eventType)));
    }

    @Override
    public void onRemoteVideoEvent(String userId, String videoId, int eventType) {
        Logger.d("userId:" + userId + " videoId:" + videoId + " eventType:" + eventType);
        if (eventType == FspEngine.REMOTE_VIDEO_PUBLISH_STARTED) {
            m_remoteVideos.add(new Pair<String, String>(userId, videoId));
        } else if (eventType == FspEngine.REMOTE_VIDEO_PUBLISH_STOPED) {
            m_remoteVideos.remove(new Pair<String, String>(userId, videoId));
        }
        EventBus.getDefault().post(new FspEvents.RemoteVideoEvent(userId, videoId, eventType));
    }

    @Override
    public void onRemoteAudioEvent(String userId, int eventType) {
        Logger.d("userId:" + userId + " eventType:" + eventType);
        if (eventType == FspEngine.REMOTE_AUDIO_PUBLISH_STARTED) {
            m_remoteAudios.add(userId);
        } else if (eventType == FspEngine.REMOTE_AUDIO_PUBLISH_STOPED) {
            m_remoteAudios.remove(userId);
        }
        EventBus.getDefault().post(new FspEvents.RemoteAudioEvent(userId, eventType));
    }

    @Override
    public void onGroupUsersRefreshed(String[] userIds) {
        Logger.d("userIds:" + Arrays.toString(userIds));
        m_groupUserIds.clear();
        m_groupUserIds.addAll(Arrays.asList(userIds));
    }

    @Override
    public void onRemoteUserEvent(String userId, int eventType) {
        Logger.d("userId:" + userId + " eventType:" + eventType);
        if (eventType == com.hst.fsp.FspEngine.REMOTE_USER_JOIN_GROUP) {
            if (!m_groupUserIds.contains(userId)) {
                m_groupUserIds.add(userId);
            }
        } else if (eventType == com.hst.fsp.FspEngine.REMOTE_USER_LEAVE_GROUP) {
            m_groupUserIds.remove(userId);
        }
        EventBus.getDefault().post(new FspEvents.RemoteUserEvent(userId, eventType));
    }
    // -----------------   IFspEngineEventHandler  end  ------------------------


    // -----------------   IFspSignalingEventHandler  start  ------------------------
    @Override
    public void onRefreshUserStatusFinished(int errCode, int requestId, FspUserInfo[] infos) {
        EventBus.getDefault().post(new FspEvents.RefreshUserStatusFinished(errCode == FspEngine.ERR_OK, requestId, infos, FspFailMsgUtils.getErrorDesc(errCode)));
    }

    @Override
    public void onUserStatusChange(FspUserInfo changedUserInfo) {

    }

    @Override
    public void onInviteIncome(String inviterUserId, int inviteId, String groupId, String desc) {
        Logger.d("inviterUserId:" + inviterUserId + " inviteId:" + inviteId + " groupId:" + groupId + " desc:" + desc);

        EventBus.getDefault().post(new FspEvents.InviteIncome(inviterUserId, inviteId, groupId, desc));
    }

    @Override
    public void onInviteAccepted(String remoteUserId, int inviteId) {
        Logger.d("remoteUserId:" + remoteUserId + " inviteId:" + inviteId);
        EventBus.getDefault().post(new EventMsgEntity(remoteUserId, " 接受邀请"));
    }

    @Override
    public void onInviteRejected(String remoteUserId, int inviteId) {
        Logger.d("remoteUserId:" + remoteUserId + " inviteId:" + inviteId);
        EventBus.getDefault().post(new EventMsgEntity(remoteUserId, " 拒绝邀请"));
    }

    @Override
    public void onUserMsgIncome(String srcUserId, int msgId, String msg) {
        Logger.d("srcUserId:" + srcUserId + " msgId:" + msgId + " msg:" + msg);
        EventBus.getDefault().post(new FspEvents.ChatMsgItem(false,srcUserId, msgId, msg,false));
    }

    @Override
    public void onGroupMsgIncome(String srcUserId, int msgId, String msg) {
        Logger.d("srcUserId:" + srcUserId + " msgId:" + msgId + " msg:" + msg);
        EventBus.getDefault().post(new FspEvents.ChatMsgItem(true,srcUserId, msgId, msg,false));
    }
    // -----------------   IFspSignalingEventHandler  end  ------------------------

}
