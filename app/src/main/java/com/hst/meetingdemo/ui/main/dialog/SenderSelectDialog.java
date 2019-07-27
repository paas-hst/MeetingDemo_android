package com.hst.meetingdemo.ui.main.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.hst.fsp.FspUserInfo;
import com.hst.meetingdemo.R;
import com.hst.meetingdemo.base.BaseDialog;
import com.hst.meetingdemo.business.FspEvents;
import com.hst.meetingdemo.business.FspManager;
import com.hst.meetingdemo.ui.main.adapter.EventMsgAdapter;
import com.hst.meetingdemo.ui.main.adapter.SenderSelectAdapter;
import com.hst.meetingdemo.ui.online.OnlineAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 选择发送人
 */
public class SenderSelectDialog extends BaseDialog {

    @BindView(R.id.dialog_rv_sender)
    RecyclerView dialogRvSender;

    private SenderSelectAdapter m_selectAdapter;
    private String m_selectedUserId;
    private onDialogItemSelectedListener m_listener;


    public SenderSelectDialog(@NonNull Context context,String uid_sel) {
        super(context, R.style.DialogStyleCameraList);
        this.m_selectedUserId = uid_sel;
        setCanceledOnTouchOutside(true);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_sender_select;
    }

    @Override
    protected void initWindowFlags() {
    }

    protected void init() {
        List list = FspManager.getInstance().getGroupUsers();

        dialogRvSender.setLayoutManager(new LinearLayoutManager(getContext()));
        m_selectAdapter = new SenderSelectAdapter(getContext(),list, m_selectedUserId) {
            @Override
            public void userIdSelect(String userId) {
                SenderSelectDialog.this.dismiss();
                if (m_listener != null) {
                    m_listener.onItemSelected(userId);
                }
            }
        };
        dialogRvSender.setAdapter(m_selectAdapter);

    }

    @OnClick(R.id.dialog_iv_close)
    public void onClickClose() {
        dismiss();
    }


    public void setListener(onDialogItemSelectedListener listener) {
        m_listener = listener;
    }

    public interface onDialogItemSelectedListener {
        void onItemSelected(String uidSel);
    }
}
