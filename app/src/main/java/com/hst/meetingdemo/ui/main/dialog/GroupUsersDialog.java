package com.hst.meetingdemo.ui.main.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.hst.fsp.FspUserInfo;
import com.hst.meetingdemo.R;
import com.hst.meetingdemo.base.BaseDialog;
import com.hst.meetingdemo.ui.main.adapter.GroupUsersAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 参与人弹框
 */
public class GroupUsersDialog extends BaseDialog {

    @BindView(R.id.dialog_rv_msg)
    RecyclerView dialogRvMsg;

    private GroupUsersAdapter m_usersAdapter;
    private List<FspUserInfo> m_data = new ArrayList();

    public GroupUsersDialog(@NonNull Context context) {
        super(context, R.style.DialogStyleBottomTranslucent);
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_participate;
    }

    protected void init() {
        m_usersAdapter = new GroupUsersAdapter(getContext());
        dialogRvMsg.setLayoutManager(new LinearLayoutManager(getContext()));
        dialogRvMsg.setAdapter(m_usersAdapter);
        m_usersAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.dialog_iv_close)
    public void onClickClose() {
        dismiss();
    }


    @Override
    public void notifyDataSetChanged() {
        if (m_usersAdapter != null && dialogRvMsg != null) {
            m_usersAdapter.notifyDataSetChanged();
            dialogRvMsg.smoothScrollToPosition(dialogRvMsg.getAdapter().getItemCount());
        }
    }
}
