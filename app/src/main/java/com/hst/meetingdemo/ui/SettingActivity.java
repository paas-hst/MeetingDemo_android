package com.hst.meetingdemo.ui;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hst.meetingdemo.business.FspManager;
import com.inpor.com.meetingdemo.R;

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

    private boolean m_isResumed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ButterKnife.bind(this);

        FspManager fspM = FspManager.instatnce();
        m_tvLoginInfo.setText(String.format("%s:%s", fspM.getSelfGroupId(), fspM.getSelfUserId()));
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
}
