package com.hst.meetingdemo.ui.main.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hst.meetingdemo.R;
import com.hst.meetingdemo.base.BaseDialog;
import com.hst.meetingdemo.business.FspConstants;
import com.hst.meetingdemo.business.FspEvents;
import com.hst.meetingdemo.business.FspManager;
import com.hst.meetingdemo.ui.main.adapter.ChatMsgAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 聊天框
 */
public class ChatMsgDialog extends BaseDialog {

    @BindView(R.id.dialog_rv_msg)
    RecyclerView dialogRvMsg;
    @BindView(R.id.dialog_btn_send)
    Button dialogBtnSend;
    @BindView(R.id.dialog_et_msg)
    EditText dialogEtMsg;
    @BindView(R.id.dialog_tv_send_select)
    TextView dialogTvSendSelect;

    private List<FspEvents.ChatMsgItem> m_data;
    private ChatMsgAdapter m_chatMsgAdapter;
    private String m_remoteUserId;
    private onDialogItemSenderSelectListener m_onDialogItemSenderSelectListener;


    public ChatMsgDialog(@NonNull Context context, List<FspEvents.ChatMsgItem> data, String remoteUserId) {
        super(context, R.style.DialogStyleBottomTranslucent);
        setCanceledOnTouchOutside(false);
        this.m_data = data;
        this.m_remoteUserId = remoteUserId;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_chat_msg;
    }

    protected void init() {
        dialogRvMsg.setLayoutManager(new LinearLayoutManager(getContext()));
        m_chatMsgAdapter = new ChatMsgAdapter(getContext(), m_data);
        dialogRvMsg.setAdapter(m_chatMsgAdapter);
        dialogRvMsg.smoothScrollToPosition(dialogRvMsg.getAdapter().getItemCount());

        dialogTvSendSelect.setText(m_remoteUserId == null ? "发送给：所有人" : "发送给：" + m_remoteUserId);
    }

    @OnClick(R.id.dialog_iv_close)
    public void onClickClose(View view){
        this.dismiss();
    }

    @OnClick(R.id.dialog_btn_send)
    public void onClickSend(View view){
        String msg = dialogEtMsg.getText().toString();
        if (!msg.isEmpty()) {
            if (m_remoteUserId == null) { // groupMsg
                if (FspManager.getInstance().sendGroupMsg(msg)) {
                    m_data.add(new FspEvents.ChatMsgItem(true,
                            null, -1, msg, true));
                    notifyDataSetChanged();
                }
            } else { // userMsg
                if (FspManager.getInstance().sendUserMsg(m_remoteUserId, msg)) {
                    m_data.add(new FspEvents.ChatMsgItem(false,
                            m_remoteUserId, -1, msg, true));
                    notifyDataSetChanged();
                }
            }
        }
    }

    @OnClick(R.id.dialog_tv_send_select)
    public void onClickSelectReceivers(View view) {
        SenderSelectDialog senderSelectDialog = new SenderSelectDialog(getContext(),m_remoteUserId);
        senderSelectDialog.setListener(new SenderSelectDialog.onDialogItemSelectedListener() {
            @Override
            public void onItemSelected(String uidSel) {
                m_remoteUserId = uidSel;
                dialogTvSendSelect.setText(m_remoteUserId == null ? "发送给：所有人" : "发送给：" + m_remoteUserId);
                if (m_onDialogItemSenderSelectListener != null) {
                    m_onDialogItemSenderSelectListener.onItemSelected(uidSel);
                }
            }
        });
        senderSelectDialog.show();
    }


    @Override
    public void notifyDataSetChanged() {
        if (m_chatMsgAdapter != null && dialogRvMsg != null) {
            m_chatMsgAdapter.notifyDataSetChanged();
            dialogRvMsg.smoothScrollToPosition(dialogRvMsg.getAdapter().getItemCount());
        }
    }

    public ChatMsgDialog setOnDialogItemSenderSelectListener(onDialogItemSenderSelectListener onDialogItemSenderSelectListener) {
        m_onDialogItemSenderSelectListener = onDialogItemSenderSelectListener;
        return this;
    }

    public interface onDialogItemSenderSelectListener {
        void onItemSelected(String uidSel);
    }
}
