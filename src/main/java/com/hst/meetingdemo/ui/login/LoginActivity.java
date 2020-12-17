package com.hst.meetingdemo.ui.login;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.support.v7.app.AppCompatDelegate;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hst.meetingdemo.R;
import com.hst.meetingdemo.base.BaseActivity;
import com.hst.meetingdemo.business.FspEvents;
import com.hst.meetingdemo.business.FspManager;
import com.hst.meetingdemo.ui.login_config.AppConfigActivity;
import com.hst.meetingdemo.ui.online.OnlineActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

/**
 * 登录
 */
@RuntimePermissions
public class LoginActivity extends BaseActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @BindView(R.id.login_et_userid)
    EditText m_loginEtUserId;
    @BindView(R.id.login_et_customname)
    EditText m_loginEtCustomName;
    @BindView(R.id.login_tv_sdkversion)
    TextView m_loginTvSdkVersion;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void init() {
        if (FspManager.getInstance().getVersion() != null) {
            m_loginTvSdkVersion.setText("SdkVersion:  " + FspManager.getInstance().getVersion());
        }
    }

    @OnClick(R.id.login_iv_setting)
    public void onSettingIvClick() {
        startActivity(new Intent(LoginActivity.this, AppConfigActivity.class));
    }

    @OnClick(R.id.login_btn_login)
    public void onLoginBtnClick() {
        //使用com.github.hotchemi:permissionsdispatcher 处理运行时权限问题。
        //权限都申请到后， 开始doJoinGroup 加入组
        LoginActivityPermissionsDispatcher.doLoginWithPermissionCheck(this);
    }

    //LoginActivity统一先分配好运行时权限，  不同开发者根据自身情况处理动态权限问题
    //需要的两个权限：  Manifest.permission.RECORD_AUDIO 在 FspEngine.init 时需要
    //Manifest.permission.CAMERA 需要在视频相关操作时分配
    @NeedsPermission({Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE})
    void doLogin() {
        String userid = m_loginEtUserId.getText().toString();
        if (userid.isEmpty()) {
            m_loginEtUserId.requestFocus();
            return;
        }

        showLoading(R.string.login_state_ing, R.string.sure);

        if (FspManager.getInstance().checkAppConfigChange()) {
            if (!FspManager.getInstance().init()) {
                setErrorLoading("init failed");
                return;
            }
        } else {
            setErrorLoading("请输入配置信息");
            return;
        }

        boolean result = FspManager.getInstance().login(userid, m_loginEtCustomName.getText().toString());
        if (!result) {
            setErrorLoading(R.string.login_state_fail);
        }

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        } else {
            audioManager.setMode(AudioManager.MODE_IN_CALL);
        }
        audioManager.setSpeakerphoneOn(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginResult(FspEvents.LoginResult result) {
        if (result.isSuccess) {
            dismissLoading();
            startActivity(new Intent(this, OnlineActivity.class));
        } else {
            setErrorLoading(result.desc);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        FspManager.getInstance().destroy();
        System.exit(0);
    }
}
