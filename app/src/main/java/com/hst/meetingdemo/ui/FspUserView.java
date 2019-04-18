package com.hst.meetingdemo.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hst.meetingdemo.R;
import com.hst.fsp.FspEngine;
import com.hst.fsp.VideoStatsInfo;
import com.hst.meetingdemo.business.FspManager;
import com.hst.meetingdemo.utils.FspUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FspUserView extends RelativeLayout{

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

    private boolean m_haveAudio;

    private boolean m_isMaximization = false;

    private int m_renderMode = FspEngine.RENDER_MODE_FIT_CENTER;

    public FspUserView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.user_view, this);
        ButterKnife.bind(this);
    }

    public boolean isMaximization() {return m_isMaximization;}

    public void setMaximization(boolean isMaximiaztion) {
        m_isMaximization = isMaximiaztion;
    }

    public String getUserId() {
        return m_userid;
    }

    public String getVideoId() {
        return m_videoid;
    }

    private void showUserName(){
        m_userNameLabel.setVisibility(View.VISIBLE);
        m_userNameText.setVisibility(View.VISIBLE);
        m_userNameText.setText(m_userid.toString());
    }

    private void tryHideUserName(){
        if (!m_haveAudio && m_videoid == null){
            m_userNameLabel.setVisibility(View.GONE);
            m_userNameText.setVisibility(View.GONE);
        }
    }

    public void useToRender(String userid, String videoid) {
        m_userid = userid;
        m_videoid = videoid;

        m_surfaceView.setVisibility(View.VISIBLE);
        m_btnMore.setVisibility(View.VISIBLE);
        m_tvInfo.setVisibility(View.VISIBLE);

        showUserName();
    }

    public void freeRender() {
        m_surfaceView.setVisibility(View.GONE);
        m_btnMore.setVisibility(View.GONE);

        m_videoid = null;
        requestLayout();
        invalidate();
        m_surfaceView.invalidate();

        tryHideUserName();
        checkUserState();
    }

    public void openAudio(String userid) {
        m_userid = userid;
        m_haveAudio = true;

        m_pbAudioEnergy.setVisibility(View.VISIBLE);
        m_ivMicState.setVisibility(View.VISIBLE);
        m_tvInfo.setVisibility(View.VISIBLE);

        showUserName();
    }

    public void closeAudio() {
        m_haveAudio = false;

        m_pbAudioEnergy.setVisibility(View.GONE);
        m_ivMicState.setVisibility(View.GONE);

        tryHideUserName();
        checkUserState();
    }

    public SurfaceView getSurfaceView() {
        return m_surfaceView;
    }

    public int getVideoRenderMode() {
        return m_renderMode;
    }

    public void onOneSecondTimer() {
        if (!FspUtils.isEmptyText(m_userid)) {
            FspManager fspM = FspManager.instatnce();
            if (m_haveAudio) {
                m_pbAudioEnergy.setProgress(fspM.getFspEngine().getRemoteAudioEnergy(m_userid));
            }

            String strInfo = "";

            if (!FspUtils.isEmptyText(m_videoid) || FspUtils.isSameText(m_userid, fspM.getSelfUserId())) {
                VideoStatsInfo statsInfo = fspM.getVideoStats(m_userid, m_videoid);
                strInfo = String.format("%dx%d %dF %s", statsInfo.width, statsInfo.height, statsInfo.framerate, FspUtils.convertBytes2HumanSize(statsInfo.bitrate));
            }

            m_tvInfo.setText(strInfo);
        }
    }

    public void onVisibleChange()
    {
        if (FspUtils.isEmptyText(m_userid) || FspUtils.isEmptyText(m_videoid)) {
            return;
        }

        if (getVisibility() == VISIBLE) {
            FspManager.instatnce().setRemoteVideoRender(m_userid, m_videoid,
                    m_surfaceView, m_renderMode);
        } else {
            FspManager.instatnce().setRemoteVideoRender(m_userid, m_videoid,
                    null, m_renderMode);
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

        CheckListDialog.CheckListDialogListener selectListener = new CheckListDialog.CheckListDialogListener() {
            @Override
            public void onItemSelected(int selectedIndex) {
                int newRenderMode = m_renderMode;
                if (selectedIndex == 0) {
                    newRenderMode = FspEngine.RENDER_MODE_SCALE_FILL;
                } else if (selectedIndex == 1) {
                    newRenderMode = FspEngine.RENDER_MODE_CROP_FILL;
                } else if(selectedIndex == 2) {
                    newRenderMode = FspEngine.RENDER_MODE_FIT_CENTER;
                }

                if (newRenderMode != m_renderMode) {
                    m_renderMode = newRenderMode;
                    FspManager.instatnce().setRemoteVideoRender(m_userid, m_videoid,
                            m_surfaceView, m_renderMode);
                }
            }
        };

        final CheckListDialog dialog = new CheckListDialog(
                this.getContext(), "选择视频显示模式",
                renderModes, nCurSelectedIdnex,
                selectListener);

        dialog.show();
    }

    private void checkUserState() {
        //音频和视频都关闭了， 才不属于某个user
        if (!m_haveAudio && FspUtils.isEmptyText(m_videoid)) {
            m_userid = null;
            m_tvInfo.setVisibility(View.GONE);
        }
    }
}


