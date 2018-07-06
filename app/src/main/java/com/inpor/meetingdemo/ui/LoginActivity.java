package com.inpor.meetingdemo.ui;

import android.Manifest;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.inpor.com.meetingdemo.R;
import com.inpor.meetingdemo.business.FspEvents;
import com.inpor.meetingdemo.business.FspManager;

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

    private long m_nTestClickStartTime;
    private int m_nTestClickCount;

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

    @OnClick(R.id.login_tv_company_name)
    public void onClickCompanyName() {
        onClickTest();
    }

    @OnClick(R.id.login_tv_copyright)
    public void onCickCopyRight() {
        onClickTest();
    }

    private void onClickTest() {
        long curTime = System.currentTimeMillis();

        if (curTime - m_nTestClickStartTime <= 1000) {
            m_nTestClickCount++;
        } else {
            m_nTestClickCount = 0;
        }

        m_nTestClickStartTime = curTime;

        if (m_nTestClickCount >= 3) {
            startActivity(new Intent(this, LoginConfigActivity.class));
        } else {
            Toast.makeText(this, "再点 " + (3 - m_nTestClickCount) + " 次进入穿越通道", Toast.LENGTH_LONG);
        }
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

        m_LayoutState.setVisibility(View.VISIBLE);
        m_TvState.setText(R.string.login_state_ing);

        //@todo
        //finish();
        //startActivity(new Intent(this, MainActivity.class));

        boolean isJoinSucess = FspManager.instatnce().joinGroup(groupid, userid);
        if (!isJoinSucess) {
            m_IvState.setImageResource(R.drawable.login_icon_warning);
            m_TvState.setText(R.string.login_state_fail);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventJoinGroupResult(FspEvents.JoinGroupResult joinGroupResult){
        if (joinGroupResult.isSucess) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        } else {
            m_IvState.setImageResource(R.drawable.login_icon_warning);
            m_TvState.setText(R.string.login_state_fail);
        }
    }
}
