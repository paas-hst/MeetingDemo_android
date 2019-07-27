package com.hst.meetingdemo.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.hst.meetingdemo.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoadingDialog extends Dialog {

    @BindView(R.id.login_iv_login_state)
    ImageView m_loginIvLoginState;
    @BindView(R.id.login_tv_login_state)
    TextView m_loginTvLoginState;
    @BindView(R.id.login_btn_rejoin)
    TextView m_loginBtnRejoin;

    private @StringRes
    int resId_waitTextTv;

    private @StringRes
    int resId_errorTextBtn;


    public LoadingDialog(@NonNull Context context, @StringRes int resId_waitTextTv, @StringRes int resId_errorTextBtn) {
        super(context, R.style.DialogStyleTransparent);
        setCanceledOnTouchOutside(false);
        this.resId_waitTextTv = resId_waitTextTv;
        this.resId_errorTextBtn = resId_errorTextBtn;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);

        ButterKnife.bind(this);

        m_loginIvLoginState.setImageResource(R.drawable.login_waiting);
        Animation waitingAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.join_group);
        m_loginIvLoginState.startAnimation(waitingAnimation);

        m_loginTvLoginState.setText(resId_waitTextTv);
        m_loginBtnRejoin.setText(resId_errorTextBtn);
        m_loginBtnRejoin.setVisibility(View.GONE);

        // 设置window偏右
        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().getAttributes().gravity = Gravity.CENTER;
    }

    public void setErrorStatus(@NonNull String msg) {
        m_loginIvLoginState.clearAnimation();
        m_loginIvLoginState.setImageResource(R.drawable.login_icon_warning);
        if (msg != null) {
            m_loginTvLoginState.setText(msg);
        }
        m_loginBtnRejoin.setVisibility(View.VISIBLE);
    }

    public void setErrorStatus(@StringRes int resId) {
        m_loginIvLoginState.clearAnimation();
        m_loginIvLoginState.setImageResource(R.drawable.login_icon_warning);
        m_loginTvLoginState.setText(resId);
        m_loginBtnRejoin.setVisibility(View.VISIBLE);
    }


    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void dismiss() {
        m_loginTvLoginState.clearAnimation();
        super.dismiss();
    }

    @OnClick(R.id.login_btn_rejoin)
    public void onClickRejoinBtn() {
        this.dismiss();
    }
}
