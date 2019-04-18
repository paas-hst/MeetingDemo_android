package com.hst.meetingdemo.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hst.meetingdemo.MeetingDemoApplication;
import com.hst.meetingdemo.business.FspManager;
import com.hst.meetingdemo.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class AppConfigActivity extends Activity {

    boolean m_useDefaultAppConfig = true;

    String m_userAppId;
    String m_userAppSecret;
    String m_userAppServerAddr;

    @BindView(R.id.config_switch)
    TextView m_switchTextView;
    @BindView(R.id.app_id)
    EditText m_appIdEditText;
    @BindView(R.id.app_secret)
    EditText m_appSecretEditText;
    @BindView(R.id.app_serveraddr)
    EditText m_appServerAddrEditText;
    @BindView(R.id.cancel_btn)
    Button m_cancelBtn;
    @BindView(R.id.ok_btn)
    Button m_okBtn;
    @BindView(R.id.switch_label)
    TextView m_switchLabel;
    @BindView(R.id.app_layout_serveraddr)
    View m_layoutServerAddr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_config);

        ButterKnife.bind(this);
        loadAppConfig();
    }

    @OnClick(R.id.config_switch)
    public void onConfigSwitchClick(){
        m_useDefaultAppConfig = !m_useDefaultAppConfig;
        if (m_useDefaultAppConfig){
            setUseDefaultAppConfig();
        } else {
            setUseUserAppConfig();
        }
    }

    @OnClick(R.id.cancel_btn)
    public void onCancelBtnClick(){
        finish();
    }

    @OnClick(R.id.ok_btn)
    public void onOkBtnClick(){
        if (!m_useDefaultAppConfig){
            m_userAppId = m_appIdEditText.getText().toString();
            m_userAppSecret = m_appSecretEditText.getText().toString();
            m_userAppServerAddr = m_appServerAddrEditText.getText().toString();
        }

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MeetingDemoApplication.sApplication).edit();
        editor.putBoolean(FspManager.PKEY_USE_DEFAULT_APPCONFIG, m_useDefaultAppConfig);
        editor.putString(FspManager.PKEY_USER_APPID, m_userAppId);
        editor.putString(FspManager.PKEY_USER_APPSECRET, m_userAppSecret);
        editor.putString(FspManager.PKEY_USER_APPSERVERADDR, m_userAppServerAddr);
        editor.apply();

        finish();
    }

    @OnLongClick(R.id.switch_label)
    public boolean onLongClickSwitchLabel()
    {
        m_layoutServerAddr.setVisibility(View.VISIBLE);
        return true;
    }

    private void loadAppConfig(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MeetingDemoApplication.sApplication);
        m_useDefaultAppConfig = preferences.getBoolean(FspManager.PKEY_USE_DEFAULT_APPCONFIG, true);
        m_userAppId = preferences.getString(FspManager.PKEY_USER_APPID, "");
        m_userAppSecret = preferences.getString(FspManager.PKEY_USER_APPSECRET, "");
        m_userAppServerAddr = preferences.getString(FspManager.PKEY_USER_APPSERVERADDR, "");

        if (m_useDefaultAppConfig){
            setUseDefaultAppConfig();
        }else{
            setUseUserAppConfig();
        }
    }

    private void setUseUserAppConfig(){
        m_switchTextView.setBackgroundResource(R.drawable.app_switch_on_selector);
        m_switchLabel.setText("使用用户App ID和App Secret");
        m_appIdEditText.setText(m_userAppId);
        m_appSecretEditText.setText(m_userAppSecret);
        m_appServerAddrEditText.setText(m_userAppServerAddr);
        m_appIdEditText.setEnabled(true);
        m_appSecretEditText.setEnabled(true);
    }

    private void setUseDefaultAppConfig(){
        m_switchTextView.setBackgroundResource(R.drawable.app_switch_off_selector);
        m_switchLabel.setText("使用默认App ID和App Secret");
        m_appIdEditText.setText(FspManager.DEFAULT_APP_ID);
        m_appSecretEditText.setText(FspManager.DEFAULT_APP_SECRET);
        m_appIdEditText.setEnabled(false);
        m_appSecretEditText.setEnabled(false);
    }
}
