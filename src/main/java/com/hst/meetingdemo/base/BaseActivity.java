package com.hst.meetingdemo.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.hst.meetingdemo.R;
import com.hst.meetingdemo.bean.OnlineFinishEntity;
import com.hst.meetingdemo.business.FspEvents;
import com.hst.meetingdemo.business.FspManager;
import com.hst.meetingdemo.ui.invite_income.InviteIncomeActivity;
import com.hst.meetingdemo.ui.main.MainActivity;
import com.hst.meetingdemo.ui.online.OnlineActivity;
import com.hst.meetingdemo.ui.setting.SettingActivity;
import com.hst.meetingdemo.utils.LoadingDialog;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    private LoadingDialog m_loadingDialog;
    private boolean m_isPause = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindowFlags();
        setContentView(getLayoutId());
        if (canButterKnife()) ButterKnife.bind(this);
        if (canEventBus()) EventBus.getDefault().register(this);
        init();
    }

    protected abstract int getLayoutId();

    protected abstract void init();

    protected void initWindowFlags() {

    }

    protected boolean canEventBus() {
        return true;
    }

    protected boolean canButterKnife() {
        return true;
    }

    public boolean isPause() {
        return m_isPause;
    }

    @Override
    protected void onResume() {
        super.onResume();
        m_isPause = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        m_isPause = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissLoading();
        if (canEventBus()) EventBus.getDefault().unregister(this);
    }


    protected void showLoading(@StringRes int resId_waitTextTv, @StringRes int resId_errorTextBtn) {
        m_loadingDialog = new LoadingDialog(this, resId_waitTextTv, resId_errorTextBtn);
        m_loadingDialog.show();
    }

    protected void setErrorLoading(@NonNull String msg) {
        if (m_loadingDialog != null) {
            m_loadingDialog.setErrorStatus(msg);
        }
    }

    protected void setErrorLoading(@StringRes int resId) {
        if (m_loadingDialog != null) {
            m_loadingDialog.setErrorStatus(resId);
        }
    }

    protected void dismissLoading() {
        if (m_loadingDialog != null && m_loadingDialog.isShowing()) {
            m_loadingDialog.dismiss();
        }
    }

    protected boolean joinGroup(String groupId) {
        // join group
        boolean result = FspManager.getInstance().joinGroup(groupId);
        if (result) {
            showLoading(R.string.join_group, R.string.rejoin_group);
        } else{
            setErrorLoading(R.string.join_group_fail);
        }
        return result;
    }

    protected void joinGroupResultSuccess() {
        dismissLoading();
        if (getClass().getSimpleName().equals(InviteIncomeActivity.class.getSimpleName())) {
            this.finish();
        }
        startActivity(new Intent(this, MainActivity.class));
    }

    protected boolean leaveGroup() {
        // join group
        boolean result = FspManager.getInstance().leaveGroup();
        if (!result) {
            Toast.makeText(getApplicationContext(), "离开组失败",
                    Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    protected void LeaveGroupResultSuccess() {
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventJoinGroupResult(FspEvents.JoinGroupResult result) {
        if (m_isPause) return;
        dismissLoading();

        if (result.isSuccess) {
            joinGroupResultSuccess();
        } else {
            showLoading(R.string.invite_to_join_group, R.string.rejoin_group);
            setErrorLoading(result.desc);
        }
    }

    // leaveGroup
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventLeaveGroupResult(FspEvents.LeaveGroupResult result) {
        if (m_isPause) return;

        if (result.isSuccess) {
            LeaveGroupResultSuccess();
        } else {
            Toast.makeText(getApplicationContext(), "离开组失败",
                    Toast.LENGTH_SHORT).show();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventInviteIncome(FspEvents.InviteIncome result) {
        if (m_isPause) return;

        if (getClass().getSimpleName().equals(OnlineActivity.class.getSimpleName()) ||
                getClass().getSimpleName().equals(MainActivity.class.getSimpleName()) ||
                getClass().getSimpleName().equals(SettingActivity.class.getSimpleName())) {
            Intent intent = new Intent(this, InviteIncomeActivity.class);
            intent.putExtra(InviteIncomeActivity.class.getSimpleName(), result);
            startActivity(intent);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventRefreshUserStatusFinished(FspEvents.RefreshUserStatusFinished status) {
        if (m_isPause) return;

    }
}
