package com.inpor.meetingdemo.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.inpor.com.meetingdemo.R;
import com.inpor.fsp.FspEngine;
import com.inpor.fsp.VideoStatsInfo;
import com.inpor.meetingdemo.business.FspManager;
import com.inpor.meetingdemo.utils.FspUtils;

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

    private String m_userid;
    private String m_videoid;

    private boolean m_haveAudio;

    private int m_renderMode = FspEngine.RENDER_MODE_FIT_CENTER;

    public FspUserView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.fsp_user_view, this);
        ButterKnife.bind(this);
    }

    public String getUserId() {
        return m_userid;
    }

    public String getVideoId() {
        return m_videoid;
    }

    public void useToRender(String userid, String videoid) {
        m_userid = userid;
        m_videoid = videoid;

        m_surfaceView.setVisibility(View.VISIBLE);
        m_btnMore.setVisibility(View.VISIBLE);
        m_tvInfo.setVisibility(View.VISIBLE);
    }

    public void freeRender() {
        m_surfaceView.setVisibility(View.GONE);
        m_btnMore.setVisibility(View.GONE);

        m_videoid = null;
        requestLayout();
        invalidate();
        m_surfaceView.invalidate();

        checkUserState();
    }

    public void openAudio(String userid) {
        m_userid = userid;
        m_haveAudio = true;

        m_pbAudioEnergy.setVisibility(View.VISIBLE);
        m_ivMicState.setVisibility(View.VISIBLE);
        m_tvInfo.setVisibility(View.VISIBLE);
    }

    public void closeAudio() {
        m_haveAudio = false;

        m_pbAudioEnergy.setVisibility(View.GONE);
        m_ivMicState.setVisibility(View.GONE);
        checkUserState();
    }

    public SurfaceView getSurfaceView() {
        return m_surfaceView;
    }

    public void onOneSecondTimer() {
        if (!FspUtils.isEmptyText(m_userid)) {
            FspManager fspM = FspManager.instatnce();
            if (m_haveAudio) {
                m_pbAudioEnergy.setProgress(fspM.getFspEngine().getRemoteAudioEnergy(m_userid));
            }

            String strInfo = m_userid;

            if (!FspUtils.isEmptyText(m_videoid) || FspUtils.isSameText(m_userid, fspM.getSelfUserId())) {
                VideoStatsInfo statsInfo = fspM.getVideoStats(m_userid, m_videoid);
                strInfo = strInfo + ":" + m_videoid + String.format("(%dx%d %dF %s)", statsInfo.width, statsInfo.height, statsInfo.framerate, FspUtils.convertBytes2HumanSize(statsInfo.bitrate));
            }

            m_tvInfo.setText(strInfo);
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
                    FspManager.instatnce().setRemoteRenderMode(m_userid, m_videoid, m_renderMode);
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


