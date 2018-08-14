package com.hst.meetingdemo.ui;

import android.Manifest;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.inpor.com.meetingdemo.R;
import com.hst.meetingdemo.business.FspEvents;
import com.hst.meetingdemo.business.FspManager;
import com.inpor.meetingdemo.ui.LoginActivityPermissionsDispatcher;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

/**
 * 登录界面activity
 */
@RuntimePermissions
public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.login_et_groupid)
    EditText m_EtGroupId;
    @BindView(R.id.login_et_userid)
    EditText m_EtUserId;
    @BindView(R.id.login_btn_joingroup)
    View m_BtnJoinGroup;
    @BindView(R.id.login_layout_state)
    View m_LayoutState;
    @BindView(R.id.login_iv_login_state)
    ImageView m_IvState;
    @BindView(R.id.login_tv_login_state)
    TextView m_TvState;
    @BindView(R.id.login_btn_rejoin)
    TextView m_rejoinBtn;
    @BindView(R.id.app_config_btn)
    TextView m_appConfigBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick(R.id.login_btn_joingroup)
    public void OnBtnJoinGroup() {
        //使用com.github.hotchemi:permissionsdispatcher 处理运行时权限问题。
        //权限都申请到后， 开始doJoinGroup 加入组
        LoginActivityPermissionsDispatcher.doJoinGroupWithPermissionCheck(this);
    }

    @OnClick(R.id.app_config_btn)
    public void onClickAppConfig() {
        Intent intent = new Intent(LoginActivity.this, AppConfigActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.login_btn_rejoin)
    public void onClickRejoinBtn() {
        m_EtGroupId.setVisibility(View.VISIBLE);
        m_EtUserId.setVisibility(View.VISIBLE);
        m_BtnJoinGroup.setVisibility(View.VISIBLE);

        m_LayoutState.setVisibility(View.GONE);
        m_appConfigBtn.setVisibility(View.VISIBLE);
    }

    //LoginActivity统一先分配好运行时权限，  不同开发者根据自身情况处理动态权限问题
    //需要的两个权限：  Manifest.permission.RECORD_AUDIO 在 FspEngine.init 时需要
    //Manifest.permission.CAMERA 需要在视频相关操作时分配
    @NeedsPermission({Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA})
    void doJoinGroup() {
        String groupid = m_EtGroupId.getText().toString();
        String userid = m_EtUserId.getText().toString();

        if (groupid.isEmpty()) {
            m_EtGroupId.requestFocus();
            return;
        }

        if (userid.isEmpty()) {
            m_EtUserId.requestFocus();
            return;
        }

        m_EtGroupId.setVisibility(View.GONE);
        m_EtUserId.setVisibility(View.GONE);
        m_BtnJoinGroup.setVisibility(View.GONE);
        m_appConfigBtn.setVisibility(View.GONE);

        m_LayoutState.setVisibility(View.VISIBLE);
        m_rejoinBtn.setVisibility(View.GONE);
        m_IvState.setImageResource(R.drawable.login_waiting);
        m_TvState.setText(R.string.login_state_ing);

        Animation waitingAnimation = AnimationUtils.loadAnimation(this, R.anim.join_group);
        m_IvState.startAnimation(waitingAnimation);

        if (!FspManager.instatnce().joinGroup(groupid, userid)) {
            m_IvState.setImageResource(R.drawable.login_icon_warning);
            m_TvState.setText(R.string.login_state_fail);
            m_rejoinBtn.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventJoinGroupResult(FspEvents.JoinGroupResult joinGroupResult){
        m_IvState.clearAnimation();

        if (joinGroupResult.isSucess) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        } else {
            m_IvState.setImageResource(R.drawable.login_icon_warning);
            m_TvState.setText(joinGroupResult.desc);
            m_rejoinBtn.setVisibility(View.VISIBLE);
        }
    }
}
