package com.hst.meetingdemo.ui.invite_income;

import android.content.Intent;
import android.support.v7.app.AppCompatDelegate;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.hst.meetingdemo.R;
import com.hst.meetingdemo.base.BaseActivity;
import com.hst.meetingdemo.bean.MainFinishEntity;
import com.hst.meetingdemo.bean.OnlineFinishEntity;
import com.hst.meetingdemo.bean.SettingFinishEntity;
import com.hst.meetingdemo.business.FspEvents;
import com.hst.meetingdemo.business.FspManager;
import com.hst.meetingdemo.ui.main.MainActivity;
import com.hst.meetingdemo.ui.online.OnlineActivity;
import com.hst.meetingdemo.ui.setting.SettingActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 被呼叫
 */
public class InviteIncomeActivity extends BaseActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @BindView(R.id.call_tv_inviteruserid)
    TextView callTvInviterUserId;

    private FspEvents.InviteIncome m_inviteIncome;
    private boolean acceptInvite_result;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_call;
    }

    @Override
    protected void initWindowFlags() {
//        final Window win = getWindow();
//        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
//                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    @Override
    protected void init() {
        Intent intent = getIntent();
        if (intent.hasExtra(InviteIncomeActivity.class.getSimpleName())) {
            m_inviteIncome = intent.getParcelableExtra(InviteIncomeActivity.class.getSimpleName());
            callTvInviterUserId.setText(m_inviteIncome.inviterUserId);
        }
    }

    @OnClick(R.id.call_tv_reject)
    public void onClickTvReject() {
        if (m_inviteIncome != null) {
            boolean result = FspManager.getInstance().rejectInvite(m_inviteIncome.inviterUserId, m_inviteIncome.inviteId);
            if (result) {
                finish();
            }
        }
    }

    @OnClick(R.id.call_tv_accept)
    public void onClickTvAccept() {
        if (m_inviteIncome == null) {
            return;
        }
        if (!acceptInvite_result) {
            acceptInvite_result = FspManager.getInstance().acceptInvite(m_inviteIncome.inviterUserId, m_inviteIncome.inviteId);
        }
        if (acceptInvite_result) {
            if ("".equals(FspManager.getInstance().getSelfGroupId())) {
                // 没有组就加入组
                joinGroup(m_inviteIncome.groupId);
            } else {
                // 有组就离开组
                leaveGroup();
            }
        }
    }

    @Override
    protected void LeaveGroupResultSuccess() {
        // 离开了组就离开了之前的界面
        EventBus.getDefault().post(new SettingFinishEntity(true));
        EventBus.getDefault().post(new MainFinishEntity(true));
        if (m_inviteIncome == null) {
            return;
        }
        // 加入组
        joinGroup(m_inviteIncome.groupId);
    }
}
