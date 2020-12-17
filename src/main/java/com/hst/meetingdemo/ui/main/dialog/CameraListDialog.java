package com.hst.meetingdemo.ui.main.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hst.meetingdemo.R;
import com.hst.meetingdemo.base.BaseDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;

/**
 * 选择摄像头
 */
public class CameraListDialog extends BaseDialog {

    public interface CheckListDialogListener {
        void onItemSelected(int selectedIndex);
    }

    private ArrayList<String> m_Items;
    private int m_SelectedIndex;
    private String m_Title;
    private CheckListDialogListener m_Listener;

    @BindView(R.id.check_list_title)
    TextView m_TvTitle;

    @BindViews({
            R.id.check_list_tv_line1, R.id.check_list_tv_line2, R.id.check_list_tv_line3
    })
    List<TextView> m_list_item_tvs;

    @BindViews({
            R.id.check_list_iv_line1, R.id.check_list_iv_line2, R.id.check_list_iv_line3
    })
    List<ImageView> m_list_item_ivs;

    @BindViews({
            R.id.check_list_layout_line1, R.id.check_list_layout_line2, R.id.check_list_layout_line3
    })
    List<View> m_list_item_layouts;

    public CameraListDialog(@NonNull Context context, String title, ArrayList<String> items, int selectedIndex, CheckListDialogListener listener) {
        super(context, R.style.DialogStyleCameraList);

        m_Title = title;
        m_Items = items;
        m_SelectedIndex = selectedIndex;
        m_Listener = listener;

        setCanceledOnTouchOutside(true);
    }

    @Override
    protected void init() {
        for (int i = 0; i < m_Items.size(); i++) {
            m_list_item_tvs.get(i).setText(m_Items.get(i));
            m_list_item_layouts.get(i).setVisibility(View.VISIBLE);
            if (i == m_SelectedIndex) {
                m_list_item_ivs.get(i).setImageResource(R.drawable.check_list_item_checked);
            }
        }

        m_TvTitle.setText(m_Title);
    }

    @Override
    protected void initWindowFlags() {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_check_list;
    }

    @OnClick(R.id.check_list_layout_line1)
    public void onClickLine1() {
        dismiss();
        if (m_Listener != null) {
            m_Listener.onItemSelected(0);
        }
    }

    @OnClick(R.id.check_list_layout_line2)
    public void onClickLine2() {
        dismiss();
        if (m_Listener != null) {
            m_Listener.onItemSelected(1);
        }
    }

    @OnClick(R.id.check_list_layout_line3)
    public void onClickLine3() {
        dismiss();
        if (m_Listener != null) {
            m_Listener.onItemSelected(2);
        }
    }
}
