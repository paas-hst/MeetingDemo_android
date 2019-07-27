package com.hst.meetingdemo.ui.main.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.TextView;

import com.hst.meetingdemo.R;
import com.hst.meetingdemo.base.BaseRecycleViewHolder;
import com.hst.meetingdemo.base.BaseRecyclerAdapter;
import com.hst.meetingdemo.bean.EventMsgEntity;
import com.hst.meetingdemo.business.FspConstants;

import java.util.List;

public class EventMsgAdapter extends BaseRecyclerAdapter<EventMsgEntity> {

    public EventMsgAdapter(Context context, List<EventMsgEntity> datas) {
        super(context, datas, R.layout.item_event_msg, false);
    }

    @Override
    protected void onBindViewData(BaseRecycleViewHolder holder, int position, final EventMsgEntity item) {
        TextView tv_userId = holder.getView(R.id.event_msg_item_tv_userid);
        if (!TextUtils.isEmpty(item.getUserId())) {
            tv_userId.setText(item.getUserId());
            tv_userId.setTextColor(ContextCompat.getColor(mContext, R.color.color_BCCCFF));

            holder.setText(R.id.event_msg_item_tv_msg, item.getMsg());

            tv_userId.setCompoundDrawables(null, null, null, null);
            tv_userId.setCompoundDrawablePadding(0);//设置图片和text之间的间距
        } else {
            tv_userId.setText(mContext.getString(R.string.system_notification));
            tv_userId.setTextColor(ContextCompat.getColor(mContext, R.color.color_FFAB00));
            holder.setText(R.id.event_msg_item_tv_msg, item.getUserId() + " " + item.getMsg());

            Drawable drawable= mContext.getResources().getDrawable(R.drawable.main_item_system_call);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tv_userId.setCompoundDrawables(drawable, null, null, null);
            tv_userId.setCompoundDrawablePadding(4);//设置图片和text之间的间距
        }
    }
}
