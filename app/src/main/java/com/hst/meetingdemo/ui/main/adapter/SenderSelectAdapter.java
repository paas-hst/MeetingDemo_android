package com.hst.meetingdemo.ui.main.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.hst.meetingdemo.R;
import com.hst.meetingdemo.base.BaseRecycleViewHolder;
import com.hst.meetingdemo.base.BaseRecyclerAdapter;
import com.hst.meetingdemo.bean.EventMsgEntity;
import com.hst.meetingdemo.business.FspConstants;

import java.util.List;

public abstract class SenderSelectAdapter extends BaseRecyclerAdapter<String> {

    private String m_selectedUserId;

    public SenderSelectAdapter(Context context, List<String> datas, String selectedItem) {
        super(context, datas, R.layout.item_sender_select, false);
        m_selectedUserId = selectedItem;
    }

    @Override
    protected boolean haveHeaderItem()
    {
        return true;
    }

    @Override
    protected void onBindViewData(BaseRecycleViewHolder holder, int position, final String item) {
        TextView tv_userId = holder.getView(R.id.sender_select_item_tv_userid);

        if (position == 0) {
            tv_userId.setTextColor(ContextCompat.getColor(mContext, R.color.color_5780FF));
            if (m_selectedUserId == null)
                holder.setVisibility(View.VISIBLE, R.id.sender_select_item_iv_select);
            else
                holder.setVisibility(View.GONE, R.id.sender_select_item_iv_select);
            tv_userId.setText("所有人");
        } else {
            tv_userId.setText(item);
            tv_userId.setTextColor(ContextCompat.getColor(mContext, R.color.color_ff231916));
            if (m_selectedUserId != null && m_selectedUserId.equals(item))
                holder.setVisibility(View.VISIBLE, R.id.sender_select_item_iv_select);
            else
                holder.setVisibility(View.GONE, R.id.sender_select_item_iv_select);
            holder.setVisibility(View.GONE, R.id.sender_select_item_iv_select);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_selectedUserId = item;
                userIdSelect(item);
            }
        });
    }

    public abstract void userIdSelect(String userId);
}
