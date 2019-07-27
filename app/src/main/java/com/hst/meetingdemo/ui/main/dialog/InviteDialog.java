package com.hst.meetingdemo.ui.main.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.hst.fsp.FspUserInfo;
import com.hst.meetingdemo.R;
import com.hst.meetingdemo.base.BaseDialog;
import com.hst.meetingdemo.business.FspEvents;
import com.hst.meetingdemo.business.FspManager;
import com.hst.meetingdemo.ui.main.adapter.ChatMsgAdapter;
import com.hst.meetingdemo.ui.online.OnlineAdapter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 呼叫邀请弹框
 */
public class InviteDialog extends BaseDialog {

    @BindView(R.id.dialog_rv_msg)
    RecyclerView dialogRvMsg;
    @BindView(R.id.dialog_btn_select_all)
    ImageView dialogBtnSelectAll;
    @BindView(R.id.dialog_btn_call)
    Button dialogBtnCall;

    private OnlineAdapter m_onlineAdapter;
    private List<FspUserInfo> m_data = new ArrayList();

    public InviteDialog(@NonNull Context context) {
        super(context, R.style.DialogStyleBottomTranslucent);
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_invite_income;
    }

    protected void init() {
        dialogBtnCall.setEnabled(false);
        FspManager.getInstance().refreshAllUserStatus();
    }

    @OnClick(R.id.dialog_iv_close)
    public void onClickClose() {
        dismiss();
    }

    @OnClick(R.id.dialog_btn_select_all)
    public void onClickSelectAll() {
        if (dialogBtnSelectAll.isSelected()) {
            if (m_onlineAdapter != null && m_onlineAdapter.unSelectAll()) {
                dialogBtnSelectAll.setSelected(false);
                dialogBtnSelectAll.setImageResource(R.drawable.online_item_ck_normal_shape);
                dialogBtnCall.setEnabled(false);
                dialogBtnCall.setBackgroundResource(R.drawable.login_btn_unlogin_shape);
            }
        } else {
            if (m_onlineAdapter != null && m_onlineAdapter.selectAll()) {
                dialogBtnSelectAll.setSelected(true);
                dialogBtnSelectAll.setImageResource(R.drawable.online_item_ck_select);
                dialogBtnCall.setEnabled(true);
                dialogBtnCall.setBackgroundResource(R.drawable.login_btn_login_shape);
            }
        }
    }

    @OnClick(R.id.dialog_btn_call)
    public void onClickCall() {
        if (m_onlineAdapter != null && m_onlineAdapter.getSelectedItemCount() > 0) {
            FspManager.getInstance().invite(m_onlineAdapter.getSelectedItemArray(),
                    FspManager.getInstance().getSelfGroupId(), "");
            this.dismiss();
        }
    }


    @Override
    public void notifyDataSetChanged() {
        if (m_onlineAdapter != null && dialogRvMsg != null) {
            m_onlineAdapter.notifyDataSetChanged();
            dialogRvMsg.smoothScrollToPosition(dialogRvMsg.getAdapter().getItemCount());
        }
    }

    @Override
    public void notifyDataSetChanged(FspEvents.RefreshUserStatusFinished status) {
        if (m_onlineAdapter == null && dialogRvMsg == null) {
            return;
        }
        if (status.isSuccess) {
            if (m_data.size() > 0) m_data.clear();
            for (FspUserInfo item : status.infos) {
                m_data.add(item);
            }
            dialogRvMsg.setLayoutManager(new LinearLayoutManager(getContext()));

            if (m_onlineAdapter == null) {
                m_onlineAdapter = new OnlineAdapter(getContext(), m_data) {
                    @Override
                    protected void inviteSelectListener(boolean hasInvite) {
                        dialogBtnCall.setEnabled(hasInvite);
                        dialogBtnCall.setBackgroundResource(hasInvite ? R.drawable.login_btn_login_shape : R.drawable.login_btn_unlogin_shape);
                    }
                };
            } else {
                m_onlineAdapter = new OnlineAdapter(getContext(), m_data, true,m_onlineAdapter.getSelectedItem()) {
                    @Override
                    protected void inviteSelectListener(boolean hasInvite) {
                        dialogBtnCall.setEnabled(hasInvite);
                        dialogBtnCall.setBackgroundResource(hasInvite ? R.drawable.login_btn_login_shape : R.drawable.login_btn_unlogin_shape);
                    }
                };
            }

            dialogRvMsg.setAdapter(m_onlineAdapter);
        }
    }
}
