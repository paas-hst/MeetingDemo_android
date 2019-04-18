package com.hst.meetingdemo.ui;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.hst.meetingdemo.R;
import com.hst.fsp.FspEngine;
import com.hst.meetingdemo.business.FspEvents;
import com.hst.meetingdemo.business.FspManager;
import com.hst.meetingdemo.utils.FspUtils;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 登录后进来的主界面， 这里完成大部分音视频通讯相关操作
 */
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_layout_main)
    View m_layoutToolbar;

    @BindView(R.id.toolbar_btn_video)
    ImageView m_ivToolbarBtnVideo;
    @BindView(R.id.toolbar_btn_microphone)
    ImageView m_ivToolbarBtnAudio;
    @BindView(R.id.main_layout_user_row1)
    View m_layoutUserRow1;
    @BindView(R.id.main_layout_user_row2)
    View m_layoutUserRow2;

    @BindViews({
            R.id.main_user_view1, R.id.main_user_view2, R.id.main_user_view3,
            R.id.main_user_view4, R.id.main_user_view5, R.id.main_user_view6
    })
    List<FspUserView> m_list_userviews;

    private boolean m_isQuiting = false;
    private boolean m_isResumed = false;

    //userview 的双击检测
    FspUserView m_lastClickUserView = null;
    long m_lastClickUserViewTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        startTimingToolbarHide();

        EventBus.getDefault().register(this);

        //可能在LoginActivity 切换 到 Mainactivtiy期间， 收到了 sdk的onRemoteVideoEvent
        //将保存的视频列表逐一打开
        m_handler.post(new Runnable() {
            @Override
            public void run() {
                FspManager fsm = FspManager.instatnce();

                for (Pair<String, String> remote_video_info : fsm.getRemoteVideos()) {
                    FspUserView videoView = ensureUserView(remote_video_info.first, remote_video_info.second);
                    if (videoView == null) {
                        Logger.e("oncreate no releative userview: %s, %s", remote_video_info.first, remote_video_info.second);
                    } else {
                        videoView.useToRender(remote_video_info.first, remote_video_info.second);
                        if (!fsm.setRemoteVideoRender(remote_video_info.first, remote_video_info.second,
                                videoView.getSurfaceView(), videoView.getVideoRenderMode())) {
                            videoView.freeRender();
                        }
                    }
                }

                for (String remote_audio_userid : fsm.getRemoteAudios()) {
                    FspUserView videoView = ensureUserView(remote_audio_userid, null);
                    if (videoView == null) {
                        Logger.e("oncreate no releative videoview: %s", remote_audio_userid);
                    } else {
                        videoView.openAudio(remote_audio_userid);
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimingToolbarHide();
        FspManager.instatnce().destroy();

        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        m_isResumed = true;

        m_handler.postDelayed(m_OneSecondRunnable, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        m_isResumed = false;
    }

    @Override
    public void onBackPressed() {
        if (!m_isQuiting) {
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
            m_isQuiting = true;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(900);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        m_isQuiting = false;
                    }
                }
            }).start();

        } else {
            finish();
            System.exit(0);
        }
    }

    @OnClick(R.id.toolbar_btn_microphone)
    public void onBtnToolbarMicrophone()
    {
        FspManager fsp = FspManager.instatnce();
        if (fsp.isAudioPublishing()) {
            if (fsp.stopPublishAudio()) {
                m_ivToolbarBtnAudio.setImageResource(R.drawable.toolbar_audio_closed_selector);
            }
        } else {
            if (fsp.startPublishAudio()) {
                m_ivToolbarBtnAudio.setImageResource(R.drawable.toolbar_audio_opened_selector);
            }
        }
    }

    @OnClick(R.id.toolbar_btn_video)
    public void onBtnToolbarVideo()
    {
        stopTimingToolbarHide();

        final FspManager fspManger = FspManager.instatnce();
        final int curVideoState = fspManger.currentVideState();

        final ArrayList<String> videoDevices = new ArrayList<>();
        videoDevices.add("前置摄像头");
        videoDevices.add("后置摄像头");
        videoDevices.add("关闭视频");

        int nCurrentSelectedIndex = -1;
        if (curVideoState == FspManager.LOCAL_VIDEO_FRONT_PUBLISHED) {
            nCurrentSelectedIndex = 0;
        } else if (curVideoState == FspManager.LOCAL_VIDEO_BACK_PUBLISHED) {
            nCurrentSelectedIndex = 1;
        } else if (curVideoState == FspManager.LOCAL_VIDEO_CLOSED) {
            nCurrentSelectedIndex = 2;
        }

        CheckListDialog.CheckListDialogListener selectListener = new CheckListDialog.CheckListDialogListener() {
            @Override
            public void onItemSelected(int selectedIndex) {
                if (selectedIndex == 0) {
                    //选择前置摄像头
                    if (curVideoState != FspManager.LOCAL_VIDEO_FRONT_PUBLISHED) {
                        if (curVideoState == FspManager.LOCAL_VIDEO_CLOSED) {
                            publishLocalVideo(true);
                        } else {
                            fspManger.switchCamera();
                        }
                    }
                } else if (selectedIndex == 1) {
                    //选择后置摄像头
                    if (curVideoState != FspManager.LOCAL_VIDEO_BACK_PUBLISHED) {
                        if (curVideoState == FspManager.LOCAL_VIDEO_CLOSED) {
                            publishLocalVideo(false);
                        } else {
                            fspManger.switchCamera();
                        }
                    }
                } else if (selectedIndex == 2) {
                    //关闭摄像头
                    fspManger.stopVideoPublish();
                    FspUserView videoView = ensureUserView(fspManger.getSelfUserId(), "");
                    if (videoView != null) {
                        videoView.freeRender();
                    }

                    m_ivToolbarBtnVideo.setImageResource(R.drawable.toolbar_video_closed_selector);
                }
            }
        };

        final CheckListDialog dialog = new CheckListDialog(
                this, "选择摄像头",
                videoDevices, nCurrentSelectedIndex,
                selectListener);

        dialog.show();
    }

    @OnClick(R.id.toolbar_btn_setting)
    public void onBtnToolbarSetting()
    {
        startActivity(new Intent(this, SettingActivity.class));
    }

    @OnClick({
            R.id.main_user_view1, R.id.main_user_view2, R.id.main_user_view3,
            R.id.main_user_view4, R.id.main_user_view5, R.id.main_user_view6
    })
    public void onUserViewClick(FspUserView view)
    {
        long curTime = System.currentTimeMillis();
        if (curTime - m_lastClickUserViewTime < 300 && view == m_lastClickUserView) {
            //dbclick
            if (view.isMaximization()) {
                view.setMaximization(false);
                m_layoutUserRow1.setVisibility(View.VISIBLE);
                m_layoutUserRow2.setVisibility(View.VISIBLE);
                for (FspUserView iterView : m_list_userviews) {
                    if (iterView == view) {
                        continue;
                    }
                    iterView.setVisibility(View.VISIBLE);
                    iterView.onVisibleChange();
                }
            } else {
                int viewIdx = -1;
                for (int i = 0; i < m_list_userviews.size(); i++) {
                    FspUserView iterView = m_list_userviews.get(i);
                    if (iterView != view) {
                        iterView.setVisibility(View.GONE);
                        iterView.onVisibleChange();
                    } else {
                        viewIdx = i;
                    }
                }

                if (viewIdx >= 3) {
                    m_layoutUserRow1.setVisibility(View.GONE);
                } else {
                    m_layoutUserRow2.setVisibility(View.GONE);
                }
                view.setMaximization(true);
            }
        }else {
            //单击，显示工具栏
            m_handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    long curTime = System.currentTimeMillis();
                    if (curTime - m_lastClickUserViewTime > 300) {
                        m_layoutToolbar.setVisibility(View.VISIBLE);
                        stopTimingToolbarHide();
                        startTimingToolbarHide();
                    }
                }
            }, 300);
        }

        m_lastClickUserViewTime = curTime;
        m_lastClickUserView = view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventRemoteVideo(FspEvents.RemoteVideoEvent event) {
        FspManager fspManager = FspManager.instatnce();
        FspUserView videoView = ensureUserView(event.userid, event.videoid);
        if (videoView == null) {
            Logger.e("onEventRemoteVideo no releative userview: %s, %s", event.userid, event.videoid);
            return;
        }

        if (event.eventtype == FspEngine.REMOTE_VIDEO_PUBLISH_STARTED) {
            videoView.useToRender(event.userid, event.videoid);
            Log.e("testv", "renderview : " + videoView.getSurfaceView());
            if (!fspManager.setRemoteVideoRender(event.userid, event.videoid,
                    videoView.getSurfaceView(), videoView.getVideoRenderMode())) {
                videoView.freeRender();
            }
        } else if (event.eventtype == FspEngine.REMOTE_VIDEO_PUBLISH_STOPED) {
            videoView.freeRender();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventRemoteAudio(FspEvents.RemoteAudioEvent event) {
        FspUserView videoView = ensureUserView(event.userid, null);
        if (videoView == null) {
            Logger.e("onEventRemoteAudio no releative videoview: %s", event.userid);
            return;
        }

        if (event.eventtype == FspEngine.REMOTE_AUDIO_PUBLISH_STARTED) {
            videoView.openAudio(event.userid);
        } else if (event.eventtype == FspEngine.REMOTE_AUDIO_PUBLISH_STOPED) {
            videoView.closeAudio();
        }
    }

    private void publishLocalVideo(boolean isFront) {
        FspManager fspManager = FspManager.instatnce();
        FspUserView videoView = ensureUserView(fspManager.getSelfUserId(), "");
        if (videoView != null) {
            videoView.useToRender(fspManager.getSelfUserId(), "");
            if (fspManager.publishVideo(isFront, videoView.getSurfaceView())) {
                m_ivToolbarBtnVideo.setImageResource(R.drawable.toolbar_video_opened_selector);
            } else {
                videoView.freeRender();
            }
        } else {
            Logger.e("not have more free video view");
        }
    }

    private FspUserView ensureUserView(String userid, String videoid) {
        FspUserView firstFreeView = null;
        FspUserView firstUserView = null;
        for (FspUserView userView : m_list_userviews) {
            if (FspUtils.isSameText(userid, userView.getUserId())) {
                if (FspUtils.isSameText(videoid, userView.getVideoId())) {
                    return userView;
                } else if (firstUserView == null &&
                        (FspUtils.isEmptyText(videoid) || FspUtils.isEmptyText(userView.getVideoId()))){
                    firstUserView = userView;
                }
            }

            if (firstFreeView == null && FspUtils.isEmptyText(userView.getUserId())) {
                firstFreeView = userView;
            }
        }

        if (firstUserView != null) {
            return firstUserView;
        }

        return firstFreeView;
    }

    private Handler m_handler = new Handler();

    private Runnable m_OneSecondRunnable = new Runnable() {
        @Override
        public void run() {
            if (!m_isResumed) {
                return;
            }
            try {
                m_handler.postDelayed(this, 1000);
                onOneSecondTimer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void onOneSecondTimer() {
        for (FspUserView userView : m_list_userviews) {
            userView.onOneSecondTimer();
        }
    }

    private Runnable m_toolBarTimingRunnable = new Runnable() {
        @Override
        public void run() {
            m_layoutToolbar.setVisibility(View.GONE);
        }
    };

    private void stopTimingToolbarHide() {
        m_handler.removeCallbacks(m_toolBarTimingRunnable);
    }

    private void startTimingToolbarHide() {
        m_handler.postDelayed(m_toolBarTimingRunnable, 5000);
    }
}
