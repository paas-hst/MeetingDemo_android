package com.inpor.meetingdemo.ui;

import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.inpor.com.meetingdemo.R;
import com.inpor.meetingdemo.business.FspManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginConfigActivity extends AppCompatActivity {

    @BindView(R.id.loginconfig_et_appid)
    EditText m_EtAppid;

    @BindView(R.id.loginconfig_et_secretkey)
    EditText m_EtSecretKey;

    @BindView(R.id.loginconfig_et_serveraddr)
    EditText m_EtServerAddr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_config);

        ButterKnife.bind(this);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        m_EtAppid.setText(prefs.getString(FspManager.PKEY_APPID, ""));
        m_EtSecretKey.setText(prefs.getString(FspManager.PKEY_SECRETKEY, ""));
        m_EtServerAddr.setText(prefs.getString(FspManager.PKEY_SERVERADDR, ""));
    }

    @OnClick(R.id.loginconfig_btn_save)
    public void onClickBtnSave() {
        SharedPreferences.Editor prefseditor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        if (m_EtAppid.getText().length() > 0) {
            prefseditor.putString(FspManager.PKEY_APPID, m_EtAppid.getText().toString());
        }
        if (m_EtSecretKey.getText().length() > 0) {
            prefseditor.putString(FspManager.PKEY_SECRETKEY, m_EtSecretKey.getText().toString());
        }
        if (m_EtServerAddr.getText().length() > 0) {
            prefseditor.putString(FspManager.PKEY_SERVERADDR, m_EtServerAddr.getText().toString());
        }

        prefseditor.commit();
        finish();
    }

    @OnClick(R.id.loginconfig_btn_cancel)
    public void onClickBtnCancel() {
        finish();
    }
}
