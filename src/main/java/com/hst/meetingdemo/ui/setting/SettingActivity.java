package com.hst.meetingdemo.ui.setting;

import android.content.DialogInterface;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hst.fsp.VideoProfile;
import com.hst.meetingdemo.R;
import com.hst.meetingdemo.base.BaseActivity;
import com.hst.meetingdemo.bean.MainFinishEntity;
import com.hst.meetingdemo.bean.OnlineFinishEntity;
import com.hst.meetingdemo.bean.SettingFinishEntity;
import com.hst.meetingdemo.business.FspManager;
import com.hst.meetingdemo.business.FspPreferenceManager;
import com.hst.meetingdemo.ui.login.LoginActivity;
import com.hst.meetingdemo.ui.main.MainActivity;
import com.hst.meetingdemo.utils.ActivityUtils;
import com.hst.meetingdemo.utils.ProfileUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 设置
 */
public class SettingActivity extends BaseActivity {

    @BindView(R.id.tv_set_login_info)
    TextView m_tvSetLoginInfo;
    @BindView(R.id.tv_set_video_sizeinfo)
    TextView m_tvSetVideoSizeInfo;
    @BindView(R.id.sb_setting_fps)
    SeekBar m_sbSettingFps;
    @BindView(R.id.tv_setting_fps)
    TextView m_tvSettingFps;
    @BindView(R.id.sb_setting_voicevariant)
    SeekBar m_sbSettingVoiceVariant;
    @BindView(R.id.tv_setting_voicevariant)
    TextView m_tvSettingVoiceVariant;
    @BindView(R.id.sb_setting_video_stream)
    SeekBar m_sbSettingVideoStream;
    @BindView(R.id.tv_setting_video_stream)
    TextView m_tvSettingVideoStream;
    @BindView(R.id.tv_set_video_codec)
    TextView m_tvSetVideoCodec;
    @BindView(R.id.tv_set_open_video)
    ImageView m_tvSetOpenVideo;
    @BindView(R.id.tv_set_open_mic)
    ImageView m_tvSetOpenMic;


    private VideoProfile m_curProfile;
    private int m_nVoiceVariant;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void init() {
        FspManager fspManager = FspManager.getInstance();
        m_curProfile = fspManager.getCurrentProfile();

        m_nVoiceVariant = fspManager.getVoiceVariant();

        // loginInfo
        m_tvSetLoginInfo.setText(String.format("%s:%s", fspManager.getSelfGroupId(), fspManager.getSelfUserId()));

        // videoSize
        m_tvSetVideoSizeInfo.setText(ProfileUtils.getProfileRecently(m_curProfile.width));

        // frameRate
        m_tvSettingFps.setText(String.format("%d帧/秒", m_curProfile.framerate));
        m_sbSettingFps.setProgress(m_curProfile.framerate);
        m_sbSettingFps.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                m_tvSettingFps.setText(String.format("%d帧/秒", progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                m_curProfile.framerate = seekBar.getProgress();
                FspManager.getInstance().setProfile(m_curProfile);
            }
        });

        //voicevariant
        m_tvSettingVoiceVariant.setText(String.format("%d", m_nVoiceVariant));
        m_sbSettingVoiceVariant.setProgress(m_nVoiceVariant);
        m_sbSettingVoiceVariant.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                m_tvSettingVoiceVariant.setText(String.format("%d", progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                m_nVoiceVariant = seekBar.getProgress();
                FspManager.getInstance().setVoiceVariant(m_nVoiceVariant);
            }
        });

        // bitrate
        m_tvSettingVideoStream.setText(String.format("%d K", m_curProfile.framerate));
        m_sbSettingVideoStream.setProgress(m_curProfile.framerate);
        m_sbSettingVideoStream.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                m_tvSettingVideoStream.setText(String.format("%d K", progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                m_curProfile.framerate = seekBar.getProgress();
                FspManager.getInstance().setProfile(m_curProfile);
            }
        });

        loadConfig();
    }

    private void loadConfig() {
        FspPreferenceManager fspPreferenceManager = FspPreferenceManager.getInstance();
        setCameraConfig(!fspPreferenceManager.getDefaultOpenCamera());
        setMICConfig(!fspPreferenceManager.getDefaultOpenMIC());
    }

    @OnClick(R.id.iv_set_back)
    public void onClickBtnBack() {
        finish();
    }

    @OnClick(R.id.layout_set_video_size)
    public void onClickVideoSize() {
        ProfileUtils.showProfileDialog(this, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int pos) {
                m_tvSetVideoSizeInfo.setText(ProfileUtils.getProfileList(pos));
                FspManager.getInstance().setProfile(ProfileUtils.setProfile(m_curProfile, pos));
            }
        });
    }

    @OnClick(R.id.layout_set_video_codec)
    public void onClickVideoCodec() {
       /* ProfileUtils.showProfileDialog(this, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int pos) {
                m_tvSetVideoCodec.setText(ProfileUtils.getProfileList(pos));
                FspManager.getInstance().setProfile(ProfileUtils.setProfile(m_curProfile, pos));
            }
        });*/
    }

    @OnClick(R.id.tv_set_open_video)
    public void onClickOpenCamera() {
        setCameraConfig(m_tvSetOpenVideo.isSelected());
        FspPreferenceManager.getInstance().setDefaultOpenCamera(m_tvSetOpenVideo.isSelected()).apply();
    }


    @OnClick(R.id.tv_set_open_mic)
    public void onClickOpenMIC() {
        setMICConfig(m_tvSetOpenMic.isSelected());
        FspPreferenceManager.getInstance().setDefaultOpenMIC(m_tvSetOpenMic.isSelected()).apply();
    }

    private void setCameraConfig(boolean isSelected) {
        if (isSelected) {
            m_tvSetOpenVideo.setSelected(false);
            m_tvSetOpenVideo.setBackgroundResource(R.drawable.app_switch_off_selector);
        } else {
            m_tvSetOpenVideo.setSelected(true);
            m_tvSetOpenVideo.setBackgroundResource(R.drawable.app_switch_on_selector);
        }
    }

    private void setMICConfig(boolean isSelected) {
        if (isSelected) {
            m_tvSetOpenMic.setSelected(false);
            m_tvSetOpenMic.setBackgroundResource(R.drawable.app_switch_off_selector);
        } else {
            m_tvSetOpenMic.setSelected(true);
            m_tvSetOpenMic.setBackgroundResource(R.drawable.app_switch_on_selector);
        }
    }

    @OnClick(R.id.btn_set_login_out)
    public void onClickBtnLoginOut() {
        leaveGroup();
    }

    @Override
    protected void LeaveGroupResultSuccess() {
        if (FspManager.getInstance().loginOut()) {
            finish();
            EventBus.getDefault().post(new MainFinishEntity(true));
            EventBus.getDefault().post(new OnlineFinishEntity(true));
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventFinish(SettingFinishEntity entity) {
        ActivityUtils.finishActivity(this, entity.isFinish());
    }
}
