package com.inpor.meetingdemo.business;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Pair;
import android.view.SurfaceView;

import com.inpor.fsp.FspEngine;
import com.inpor.fsp.IFspEngineEventHandler;
import com.inpor.fsp.VideoStatsInfo;
import com.inpor.meetingdemo.MeetingDemoApplication;

import com.inpor.fsp.tools.FspToken;

import org.greenrobot.eventbus.EventBus;

import java.util.HashSet;


public class FspManager implements IFspEngineEventHandler{

    public static final int LOCAL_VIDEO_CLOSED = 0;   ///<本地视频关闭状态
    public static final int LOCAL_VIDEO_BACK_PUBLISHED = 1;  ///<广播了前置摄像头
    public static final int LOCAL_VIDEO_FRONT_PUBLISHED = 2;  ///<广播了后置摄像头


    public static final String PKEY_APPID = "pkey_fspappid";
    public static final String PKEY_SECRETKEY = "pkey_fspsecretkey";
    public static final String PKEY_SERVERADDR = "pkey_fspserveraddr";


    private static final String APP_ID = "";

    //APP_SECRETKEY 不应该保存在客户端，应该保存在你的服务器端，服务器生成token下发
    private static final String APP_SECRETKEY = "";

    private FspEngine m_fspEngine = null;
    private boolean m_haveInitEngine = false;
    private int m_LocalVideoState = LOCAL_VIDEO_CLOSED;
    private boolean m_isAudioPublished = false;
    private String m_SelfGroupId;
    private String m_SelfUserId;
    private HashSet<String> m_remoteAudios = new HashSet<>();
    private HashSet<Pair<String, String>> m_remoteVideos = new HashSet<>();

    private static FspManager s_instance = null;

    public static FspManager instatnce()
    {
        if (null == s_instance) {
            s_instance = new FspManager();
        }
        return s_instance;
    }

    public String getSelfGroupId() {
        return m_SelfGroupId;
    }

    public String getSelfUserId() {
        return m_SelfUserId;
    }

    public void destroy(){
        if (m_fspEngine != null) {
            m_fspEngine.destroy();
            m_fspEngine = null;
        }
        m_remoteVideos.clear();
        m_remoteAudios.clear();
    }

    public HashSet<Pair<String, String>> getRemoteVideos() {
        return m_remoteVideos;
    }

    public HashSet<String> getRemoteAudios() {
        return m_remoteAudios;
    }

    public FspEngine getFspEngine() {
        return m_fspEngine;
    }

    public boolean joinGroup(String groupId, String userId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MeetingDemoApplication.sApplication);
        String appid = prefs.getString(FspManager.PKEY_APPID, "");
        String secretkey = prefs.getString(FspManager.PKEY_SECRETKEY, "");
        String serveraddr = prefs.getString(FspManager.PKEY_SERVERADDR, "");

        if (appid.isEmpty() && secretkey.isEmpty()) {
            appid = APP_ID;
            secretkey = APP_SECRETKEY;
        }

        if (m_fspEngine == null) {
            m_fspEngine = FspEngine.create(MeetingDemoApplication.sApplication, appid, serveraddr, this);
        }

        if (!m_haveInitEngine) {
            int errCode = m_fspEngine.init();
            if (errCode != FspEngine.ERR_OK) {
                return false;
            }
            m_haveInitEngine = true;
        }

        m_SelfGroupId = groupId;
        m_SelfUserId = userId;

        m_remoteAudios.clear();
        m_remoteVideos.clear();

        //生成token的代码应该在服务器， demo中直接生成token不是 正确的做法
        FspToken token = new FspToken();
        token.setAppId(appid);
        token.setSecretKey(secretkey);
        token.setGroupId(groupId);
        token.setUserId(userId);

        int errCode = m_fspEngine.joinGroup(token.build(), groupId, userId);
        return errCode == FspEngine.ERR_OK;
    }

    public boolean publishVideo(boolean isFrontCamera, SurfaceView previewRender) {
        if (m_fspEngine == null) {
            return false;
        }

        int fspErrCode = m_fspEngine.startPreviewVideo(previewRender);
        if (fspErrCode != FspEngine.ERR_OK) {
            return false;
        }

        if (m_fspEngine.isFrontCamera() != isFrontCamera) {
            m_fspEngine.switchCamera();
        }

        if (isFrontCamera) {
            m_LocalVideoState = LOCAL_VIDEO_FRONT_PUBLISHED;
        } else {
            m_LocalVideoState = LOCAL_VIDEO_BACK_PUBLISHED;
        }

        fspErrCode = m_fspEngine.startPublishVideo();
        if (fspErrCode != FspEngine.ERR_OK) {
            return false;
        }

        return true;
    }

    public void switchCamera() {
        m_fspEngine.switchCamera();
        if (m_LocalVideoState != LOCAL_VIDEO_CLOSED) {
            if (m_fspEngine.isFrontCamera()) {
                m_LocalVideoState = LOCAL_VIDEO_FRONT_PUBLISHED;
            } else {
                m_LocalVideoState = LOCAL_VIDEO_BACK_PUBLISHED;
            }
        }
    }

    public boolean stopVideoPublish() {
        if (m_fspEngine == null) {
            return false;
        }

        m_fspEngine.stopPublidhVideo();
        m_fspEngine.stopPreviewVideo();

        m_LocalVideoState = LOCAL_VIDEO_CLOSED;
        return true;
    }

    public VideoStatsInfo getVideoStats(String userid, String videoid) {
        VideoStatsInfo statsInfo =m_fspEngine.getVideoStats(userid, videoid);
        return statsInfo;
    }

    /**
     * 当前本地视频状态
     * @return LOCAL_VIDEO_CLOSED or LOCAL_VIDEO_BACK_PUBLISHED or LOCAL_VIDEO_FRONT_PUBLISHED
     */
    public int currentVideState() {
        return m_LocalVideoState;
    }


    public boolean setRemoteVideoRender(String userid, String videoid, SurfaceView renderView) {
        int fspErrCode = m_fspEngine.setRemoteVideoRender(userid, videoid, renderView);

        return fspErrCode == FspEngine.ERR_OK;
    }

    public void setRemoteRenderMode(String userid, String videoid, int renderMode) {
        m_fspEngine.setRemoteVideoRenderMode(userid, videoid, renderMode);
    }

    public boolean closeRemoteVideo(String userid, String videoid) {
        int fspErrCode = m_fspEngine.closeRemoteVideo(userid, videoid);
        return fspErrCode == FspEngine.ERR_OK;
    }

    public boolean startPublishAudio() {
        int fspErrCode = m_fspEngine.startPublishAudio();
        if (fspErrCode == FspEngine.ERR_OK) {
            m_isAudioPublished = true;
        }
        return fspErrCode == FspEngine.ERR_OK;
    }

    public boolean stopPublishAudio() {
        int fspErrCode = m_fspEngine.stopPublishAudio();
        if (fspErrCode == FspEngine.ERR_OK) {
            m_isAudioPublished = false;
        }
        return fspErrCode == FspEngine.ERR_OK;
    }

    public boolean isAudioPublishing() {
        return m_isAudioPublished;
    }


    /////   IFspEngineEventHandler

    @Override
    public void onJoinGroupResult(int errCode) {
        EventBus.getDefault().post(new FspEvents.JoinGroupResult(errCode == FspEngine.ERR_OK));
    }

    @Override
    public void onRemoteVideoEvent(String userId, String videoId, int eventType) {
        if (eventType == FspEngine.REMOTE_VIDEO_PUBLISH_STARTED) {
            m_remoteVideos.add(new Pair<String, String>(userId, videoId));
        } else if (eventType == FspEngine.REMOTE_VIDEO_PUBLISH_STOPED) {
            m_remoteVideos.remove(new Pair<String, String>(userId, videoId));
        }
        EventBus.getDefault().post(new FspEvents.RemoteVideoEvent(userId, videoId, eventType));
    }

    @Override
    public void onRemoteAudioEvent(String userId, int eventType) {
        if (eventType == FspEngine.REMOTE_AUDIO_PUBLISH_STARTED) {
            m_remoteAudios.add(userId);
        } else if (eventType == FspEngine.REMOTE_AUDIO_PUBLISH_STOPED) {
            m_remoteAudios.remove(userId);
        }

        EventBus.getDefault().post(new FspEvents.RemoteAudioEvent(userId, eventType));
    }

    ////////// IFspEngineEventHandler

    private FspManager()
    {

    }
}
