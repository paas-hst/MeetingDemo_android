package com.hst.meetingdemo.ui.login_config;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hst.meetingdemo.R;
import com.hst.meetingdemo.base.BaseActivity;
import com.hst.meetingdemo.business.FspConstants;
import com.hst.meetingdemo.business.FspManager;
import com.hst.meetingdemo.business.FspPreferenceManager;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * 登录设置
 */
public class AppConfigActivity extends BaseActivity {

    private boolean m_useDefaultAppConfig = true;

    private String m_userAppId;
    private String m_userAppSecret;
    private String m_userAppServerAddr;

    @BindView(R.id.config_switch)
    TextView m_switchTextView;
    @BindView(R.id.app_id)
    EditText m_appIdEditText;
    @BindView(R.id.app_secret)
    EditText m_appSecretEditText;
    @BindView(R.id.app_serveraddr)
    EditText m_appServerAddrEditText;
    @BindView(R.id.switch_label)
    TextView m_switchLabel;
    @BindView(R.id.app_layout_serveraddr)
    View m_layoutServerAddr;
    @BindView(R.id.app_layout_appid)
    LinearLayout m_layoutAppid;
    @BindView(R.id.app_view_appsecret)
    View m_viewAppsecret;
    @BindView(R.id.app_layout_appsecret)
    LinearLayout m_layoutAppsecret;
    @BindView(R.id.app_view_serveraddr)
    View m_viewServeraddr;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_app_config;
    }

    @Override
    protected void init() {
        loadAppConfig();

        m_appIdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence userAppId, int i, int i1, int i2) {
                FspPreferenceManager.getInstance().setAppId(userAppId.toString()).apply();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        m_appSecretEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence userAppSecret, int i, int i1, int i2) {
                FspPreferenceManager.getInstance().setAppSecret(userAppSecret.toString()).apply();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        m_appServerAddrEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence userAppServerAddr, int i, int i1, int i2) {
                FspPreferenceManager.getInstance().setAppServerAddr(userAppServerAddr.toString()).apply();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @OnClick(R.id.config_switch)
    public void onConfigSwitchClick() {
        if (!m_switchTextView.isSelected()) {
            setUseDefaultAppConfig();
        } else {
            setUseUserAppConfig();
        }
        FspPreferenceManager.getInstance().setAppConfig(m_switchTextView.isSelected()).apply();
    }

    @OnClick(R.id.config_iv_back)
    public void onCancelBtnClick() {
        finish();
    }

    @OnLongClick(R.id.switch_label)
    public boolean onLongClickSwitchLabel() {
        m_layoutServerAddr.setVisibility(View.VISIBLE);
        return true;
    }

    private void loadAppConfig() {
        m_useDefaultAppConfig = FspPreferenceManager.getInstance().getAppConfig();
        m_userAppId = FspPreferenceManager.getInstance().getAppId();
        m_userAppSecret = FspPreferenceManager.getInstance().getAppSecret();
        m_userAppServerAddr = FspPreferenceManager.getInstance().getAppServerAddr();

        if (m_useDefaultAppConfig) {
            setUseDefaultAppConfig();
        } else {
            setUseUserAppConfig();
        }
    }

    private void setUseUserAppConfig() {
        m_switchTextView.setBackgroundResource(R.drawable.app_switch_off_selector);
        m_switchTextView.setSelected(false);

        m_appIdEditText.setText(m_userAppId);
        m_appSecretEditText.setText(m_userAppSecret);
        m_appServerAddrEditText.setText(m_userAppServerAddr);

        m_appIdEditText.setEnabled(true);
        m_appSecretEditText.setEnabled(true);
        m_appIdEditText.setSelection(m_userAppId.length());

        m_layoutAppid.setVisibility(View.VISIBLE);
        m_viewAppsecret.setVisibility(View.VISIBLE);
        m_layoutAppsecret.setVisibility(View.VISIBLE);
        m_viewServeraddr.setVisibility(View.VISIBLE);
    }

    private void setUseDefaultAppConfig() {
        m_switchTextView.setBackgroundResource(R.drawable.app_switch_on_selector);
        m_switchTextView.setSelected(true);

        m_appIdEditText.setText(FspConstants.DEFAULT_APP_ID);
        m_appSecretEditText.setText(FspConstants.DEFAULT_APP_SECRET);
        m_appServerAddrEditText.setText(FspConstants.DEFAULT_APP_ADDRESS);

        m_appIdEditText.setEnabled(false);
        m_appSecretEditText.setEnabled(false);

        m_layoutAppid.setVisibility(View.GONE);
        m_viewAppsecret.setVisibility(View.GONE);
        m_layoutAppsecret.setVisibility(View.GONE);
        m_viewServeraddr.setVisibility(View.GONE);
        if (m_layoutServerAddr.getVisibility() == View.VISIBLE) {
            m_layoutServerAddr.setVisibility(View.GONE);
        }
    }
}
