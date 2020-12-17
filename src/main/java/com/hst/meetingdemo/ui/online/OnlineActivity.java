package com.hst.meetingdemo.ui.online;

import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hst.fsp.FspUserInfo;
import com.hst.meetingdemo.R;
import com.hst.meetingdemo.base.BaseActivity;
import com.hst.meetingdemo.bean.OnlineFinishEntity;
import com.hst.meetingdemo.business.FspEvents;
import com.hst.meetingdemo.business.FspManager;
import com.hst.meetingdemo.utils.ActivityUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 在线列表
 */
public class OnlineActivity extends BaseActivity {

    @BindView(R.id.online_et_groupid)
    EditText m_onlineEtGroupId;
    @BindView(R.id.online_rv)
    RecyclerView m_onlineRv;
    @BindView(R.id.online_btn_call)
    Button m_onlineBtnCall;
    @BindView(R.id.online_btn_refresh)
    Button m_onlineBtnRefresh;


    private OnlineAdapter m_onlineAdapter;
    private List<FspUserInfo> m_data = new ArrayList();
    private Handler m_handler = new Handler();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_online;
    }


    @Override
    protected void init() {
        FspManager.getInstance().refreshAllUserStatus();
    }


    @Override
    protected void onResume() {
        super.onResume();
        m_handler.removeCallbacks(m_runnable);
        m_handler.postDelayed(m_runnable, 2000);
    }

    private Runnable m_runnable = new Runnable() {
        @Override
        public void run() {
            if (isPause()) return;

            try {
                m_handler.postDelayed(this, 2000);
                FspManager.getInstance().refreshAllUserStatus();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        m_handler.removeCallbacks(m_runnable);
    }


    @OnClick(R.id.online_btn_refresh)
    public void onRefreshBtnClick() {
        FspManager.getInstance().refreshAllUserStatus();
    }


    @OnClick(R.id.online_btn_call)
    public void onCallBtnClick() {
        String groupId = m_onlineEtGroupId.getText().toString();
        if (groupId.isEmpty()) {
            Toast.makeText(this, "请输入组ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // invite
        if (m_onlineAdapter != null && m_onlineAdapter.getSelectedItemCount() > 0) {
            FspManager.getInstance().invite(m_onlineAdapter.getSelectedItemArray(), m_onlineEtGroupId.getText().toString(), "");
        }

        joinGroup(groupId);
    }


    @Override
    public void onEventRefreshUserStatusFinished(FspEvents.RefreshUserStatusFinished status) {
        if (status.isSuccess) {
            if (m_data.size() > 0) m_data.clear();
            m_data.addAll(Arrays.asList(status.infos));
            m_onlineRv.setLayoutManager(new LinearLayoutManager(this));

            if (m_onlineAdapter == null) {
                m_onlineAdapter = new OnlineAdapter(this, m_data) {
                    @Override
                    protected void inviteSelectListener(boolean hasInvite) {
                        m_onlineBtnCall.setText(hasInvite ? R.string.invite_to_join_group : R.string.join_group);
                    }
                };
            } else {
                m_onlineAdapter = new OnlineAdapter(this, m_data, true,m_onlineAdapter.getSelectedItem()) {
                    @Override
                    protected void inviteSelectListener(boolean hasInvite) {
                        m_onlineBtnCall.setText(hasInvite ? R.string.invite_to_join_group : R.string.join_group);
                    }
                };
            }
            m_onlineBtnCall.setText(m_onlineAdapter.getSelectedItem().isEmpty() ? R.string.join_group : R.string.invite_to_join_group);
            m_onlineRv.setAdapter(m_onlineAdapter);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventFinish(OnlineFinishEntity entity) {
        ActivityUtils.finishActivity(this, entity.isFinish());
    }


    @Override
    public void onBackPressed() {
        finish();
        FspManager.getInstance().destroy();
        System.exit(0);
    }
}
