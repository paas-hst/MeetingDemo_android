package com.hst.meetingdemo.base;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.hst.meetingdemo.business.FspEvents;

import butterknife.ButterKnife;

public abstract class BaseDialog extends Dialog {

    private OnDialogDismissListener m_onDismissListener;
    private boolean m_cancel;

    public BaseDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        if (canButterKnife()) ButterKnife.bind(this);
        initWindowFlags();
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (m_onDismissListener != null) {
                    m_onDismissListener.onDismiss();
                }
            }
        });

        init();
    }

    protected abstract void init();

    protected abstract int getLayoutId();

    protected boolean canButterKnife() {
        return true;
    }

    protected void initWindowFlags() {
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().getAttributes().gravity = Gravity.BOTTOM;
    }

    public void notifyDataSetChanged() {

    }
    public void notifyDataSetChanged(FspEvents.RefreshUserStatusFinished status) {

    }

    public BaseDialog setOnDialogDismissListener(OnDialogDismissListener onDismissListener) {
        m_onDismissListener = onDismissListener;
        return this;
    }


    @Override
    public void setCanceledOnTouchOutside(boolean cancel) {
        this.m_cancel = cancel;
        super.setCanceledOnTouchOutside(cancel);
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (!m_cancel&&keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public interface OnDialogDismissListener {
        void onDismiss();
    }
}
