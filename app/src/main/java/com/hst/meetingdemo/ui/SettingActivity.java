package com.hst.meetingdemo.ui;

import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hst.meetingdemo.R;
import com.hst.fsp.VideoProfile;
import com.hst.meetingdemo.business.FspManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 设置界面activity
 */
public class SettingActivity extends AppCompatActivity {

    @BindView(R.id.pb_setting_microphone_energy)
    ProgressBar m_pbMicrophoneEnergy;
    @BindView(R.id.pb_setting_speaker_energy)
    ProgressBar m_pbSpeakerEnergy;
    @BindView(R.id.tv_setting_logininfo)
    TextView m_tvLoginInfo;
    @BindView(R.id.sb_setting_fps)
    SeekBar m_sbFps;
    @BindView(R.id.tv_setting_fps)
    TextView m_tvFps;
    @BindView(R.id.tv_set_video_sizeinfo)
    TextView m_tvSizeInfo;

    final String arrSizeStrs[] = {
            "340x240",
            "640x480",
            "1280x720",
            "1920x1080"
    };

    private boolean m_isResumed = false;

    private VideoProfile m_curProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ButterKnife.bind(this);

        FspManager fspM = FspManager.instatnce();
        m_tvLoginInfo.setText(String.format("%s:%s", fspM.getSelfGroupId(), fspM.getSelfUserId()));

        m_curProfile = FspManager.instatnce().getCurrentProfile();
        m_sbFps.setProgress(m_curProfile.framerate);
        m_tvFps.setText(String.format("%d帧/秒", m_curProfile.framerate));

        if (m_curProfile.width <= 320) {
            m_tvSizeInfo.setText(arrSizeStrs[0]);
        } else if (m_curProfile.width <= 640){
            m_tvSizeInfo.setText(arrSizeStrs[1]);
        } else if (m_curProfile.width <= 1280) {
            m_tvSizeInfo.setText(arrSizeStrs[2]);
        } else if (m_curProfile.width <= 1920) {
            m_tvSizeInfo.setText(arrSizeStrs[3]);
        }


        m_sbFps.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                m_tvFps.setText(String.format("%d帧/秒", progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                m_curProfile.framerate = seekBar.getProgress();
                FspManager.instatnce().setProfile(m_curProfile);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        m_handler.removeCallbacks(m_OneSecondRunnable);
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

    private void onOneSecondTimer()
    {
        FspManager fspM = FspManager.instatnce();
        m_pbMicrophoneEnergy.setProgress(fspM.getFspEngine().getMicrophoneEnergy());
        m_pbSpeakerEnergy.setProgress(fspM.getFspEngine().getSpeakerEnergy());
    }

    @OnClick(R.id.set_btn_title_back)
    public void onClickBtnBack() {
        finish();
    }

    @OnClick(R.id.layout_set_video_size)
    public void onClickVideoSize()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog);
        builder.setTitle("分辨率");



        builder.setItems(arrSizeStrs, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_tvSizeInfo.setText(arrSizeStrs[which]);
                if (which == 0) {
                    m_curProfile.width = 320;
                    m_curProfile.height = 240;
                } else if (which == 1){
                    m_curProfile.width = 640;
                    m_curProfile.height = 480;
                } else if (which == 2) {
                    m_curProfile.width = 1280;
                    m_curProfile.height = 720;
                } else if (which == 3) {
                    m_curProfile.width = 1920;
                    m_curProfile.height = 1080;
                }

                FspManager.instatnce().setProfile(m_curProfile);
            }
        });


        AlertDialog r_dialog = builder.create();
        r_dialog.show();

    }
}
