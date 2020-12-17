package com.hst.meetingdemo.ui.main.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hst.fsp.FspEngine;
import com.hst.fsp.VideoStatsInfo;
import com.hst.meetingdemo.R;
import com.hst.meetingdemo.business.FspManager;
import com.hst.meetingdemo.ui.main.dialog.CameraListDialog;
import com.hst.meetingdemo.utils.FspUtils;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FspUserView extends RelativeLayout {

    @BindView(R.id.fsp_video_surface)
    SurfaceView m_surfaceView;

    @BindView(R.id.fsp_user_tv_info)
    TextView m_tvInfo;
    @BindView(R.id.fsp_video_btn_more)
    View m_btnMore;
    @BindView(R.id.fsp_video_iv_mic_state)
    ImageView m_ivMicState;
    @BindView(R.id.fsp_user_pb_audio_energy)
    ProgressBar m_pbAudioEnergy;
    @BindView(R.id.user_name_label)
    ImageView m_userNameLabel;
    @BindView(R.id.user_name_text)
    TextView m_userNameText;


    private String m_userid;
    private String m_videoid;

    private boolean m_haveVideo;
    private boolean m_haveAudio;

    private int m_renderMode = FspEngine.RENDER_MODE_CROP_FILL;


    public FspUserView(Context context) {
        this(context, null);
    }

    public FspUserView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FspUserView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FspUserView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context) {
        inflate(context, R.layout.user_view, this);
        ButterKnife.bind(this);
    }

    public void setUserId(String userid) {
        m_userid = userid;
    }

    public void setVideoId(String videoid) {
        m_videoid = videoid;
    }

    public String getUserId() {
        return m_userid;
    }

    public String getVideoId() {
        return m_videoid;
    }

    private void showUserName() {
        m_userNameLabel.setVisibility(View.VISIBLE);
        m_userNameText.setVisibility(View.VISIBLE);
        m_userNameText.setText(m_userid);
    }

    public void openVideo() {
        m_haveVideo = true;

        m_surfaceView.setVisibility(View.VISIBLE);
        if (!FspUtils.isSameText(FspManager.getInstance().getSelfUserId(), m_userid)) {
            m_btnMore.setVisibility(View.VISIBLE);
        }
        m_tvInfo.setVisibility(View.VISIBLE);

        showUserName();
    }

    public void closeVideo() {
        if (!FspUtils.isEmptyText(m_userid) && !FspUtils.isEmptyText(m_videoid)) {
            FspManager.getInstance().setRemoteVideoRender(m_userid, m_videoid,
                    null, m_renderMode);
        }
        m_haveVideo = false;

        m_surfaceView.setVisibility(View.GONE);
        m_btnMore.setVisibility(View.GONE);
        m_tvInfo.setVisibility(View.GONE);

        requestLayout();
        invalidate();
        m_surfaceView.invalidate();

        releaseAll();
    }

    public void pauseVideo() {
        if (!m_haveVideo) {
            return;
        }

        FspManager.getInstance().setRemoteVideoRender(m_userid, m_videoid, null, FspEngine.RENDER_MODE_CROP_FILL);
    }

    public void resumeVideo() {
        if (!m_haveVideo) {
            return;
        }

        FspManager.getInstance().setRemoteVideoRender(m_userid, m_videoid, m_surfaceView, FspEngine.RENDER_MODE_CROP_FILL);
    }

    public void openAudio() {
        m_haveAudio = true;

        m_pbAudioEnergy.setVisibility(View.VISIBLE);
        m_ivMicState.setVisibility(View.VISIBLE);

        showUserName();
    }

    public void closeAudio() {
        m_haveAudio = false;

        m_pbAudioEnergy.setVisibility(View.GONE);
        m_ivMicState.setVisibility(View.GONE);


        releaseAll();
    }

    public SurfaceView getSurfaceView() {
        return m_surfaceView;
    }

    public int getVideoRenderMode() {
        return m_renderMode;
    }

    public void onOneSecondTimer() {
        if (!FspUtils.isEmptyText(m_userid)) {
            FspManager fspM = FspManager.getInstance();
            if (m_pbAudioEnergy.getVisibility() == View.VISIBLE) {
                m_pbAudioEnergy.setProgress(FspUtils.isSameText(fspM.getSelfUserId(), m_userid) ?
                        fspM.getMicrophoneEnergy() :
                        fspM.getRemoteAudioEnergy(m_userid));
            }


            if (m_tvInfo.getVisibility() == View.VISIBLE) {
                String strInfo = "";
                if (FspUtils.isSameText(m_userid, fspM.getSelfUserId())) {
                    VideoStatsInfo statsInfo = fspM.getVideoStats(m_userid, m_videoid);
                    strInfo = String.format("%dx%d %dF", statsInfo.width, statsInfo.height, statsInfo.frameRate);
                } else if (!FspUtils.isEmptyText(m_videoid)) {
                    VideoStatsInfo statsInfo = fspM.getVideoStats(m_userid, m_videoid);
                    strInfo = String.format("%dx%d %dF %s", statsInfo.width, statsInfo.height, statsInfo.frameRate, FspUtils.convertBytes2HumanSize(statsInfo.bitrate));
                }
                m_tvInfo.setText(strInfo);
            }
        }
    }

    public void setRemoteVideoChange() {
        //remote
        if (FspUtils.isEmptyText(m_userid) || FspUtils.isEmptyText(m_videoid)) {
            return;
        }

        if (getVisibility() == View.VISIBLE) {
            FspManager.getInstance().setRemoteVideoRender(m_userid, m_videoid,
                    m_surfaceView, m_renderMode);
        } else {
            FspManager.getInstance().setRemoteVideoRender(m_userid, m_videoid,
                    null, m_renderMode);
        }
    }

    public boolean hasVideoAudio() {
        return m_haveAudio || m_haveVideo;
    }

    private void releaseAll() {
        //音频和视频都关闭了， 才不属于某个user
        if (!m_haveAudio && !m_haveVideo) {
            // 释放绑定的视频
            if (!FspUtils.isEmptyText(m_userid) && !FspUtils.isEmptyText(m_videoid)) {
                FspManager.getInstance().setRemoteVideoRender(m_userid, m_videoid,
                        null, m_renderMode);
            }
            
            m_userid = null;
            m_videoid = null;
        }
    }

    @OnClick(R.id.fsp_video_btn_more)
    public void onClickBtnMore() {
        //右上角更多 按钮

        final ArrayList<String> renderModes = new ArrayList<>();
        renderModes.add("视频平铺显示");
        renderModes.add("视频等比裁剪显示");
        renderModes.add("视频等比完整显示");

        int nCurSelectedIdnex = -1;

        if (m_renderMode == FspEngine.RENDER_MODE_FIT_CENTER) {
            nCurSelectedIdnex = 2;
        } else if (m_renderMode == FspEngine.RENDER_MODE_CROP_FILL) {
            nCurSelectedIdnex = 1;
        } else if (m_renderMode == FspEngine.RENDER_MODE_SCALE_FILL) {
            nCurSelectedIdnex = 0;
        }

        CameraListDialog.CheckListDialogListener selectListener = new CameraListDialog.CheckListDialogListener() {
            @Override
            public void onItemSelected(int selectedIndex) {
                int newRenderMode = m_renderMode;
                if (selectedIndex == 0) {
                    newRenderMode = FspEngine.RENDER_MODE_SCALE_FILL;
                } else if (selectedIndex == 1) {
                    newRenderMode = FspEngine.RENDER_MODE_CROP_FILL;
                } else if (selectedIndex == 2) {
                    newRenderMode = FspEngine.RENDER_MODE_FIT_CENTER;
                }

                if (newRenderMode != m_renderMode) {
                    m_renderMode = newRenderMode;
                    FspManager.getInstance().setRemoteVideoRender(m_userid, m_videoid,
                            m_surfaceView, m_renderMode);
                }
            }
        };

        final CameraListDialog dialog = new CameraListDialog(
                this.getContext(), "选择视频显示模式",
                renderModes, nCurSelectedIdnex,
                selectListener);

        dialog.show();
    }
}


