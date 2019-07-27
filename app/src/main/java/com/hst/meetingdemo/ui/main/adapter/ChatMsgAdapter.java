package com.hst.meetingdemo.ui.main.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.widget.TextView;

import com.hst.meetingdemo.R;
import com.hst.meetingdemo.base.BaseRecycleViewHolder;
import com.hst.meetingdemo.base.BaseRecyclerAdapter;
import com.hst.meetingdemo.business.FspConstants;
import com.hst.meetingdemo.business.FspEvents;

import java.util.List;

public class ChatMsgAdapter extends BaseRecyclerAdapter<FspEvents.ChatMsgItem> {

    public ChatMsgAdapter(Context context, List<FspEvents.ChatMsgItem> datas) {
        super(context, datas, R.layout.item_user_msg, false);
    }

    @Override
    protected void onBindViewData(BaseRecycleViewHolder holder, int position, FspEvents.ChatMsgItem item) {
        TextView tv_uid = holder.getView(R.id.user_msg_item_tv_userid);
        TextView tv_msg = holder.getView(R.id.user_msg_item_tv_msg);
        tv_msg.setText(item.msg);
        if (item.isMyselfMsg) {
            tv_uid.setGravity( Gravity.RIGHT);
            if (!item.isGroupMsg) {

                String msg = "我 对 " + item.srcUserId + " 说";
                SpannableStringBuilder style=new SpannableStringBuilder(msg);
                style.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext,R.color.color_FFB06C)),
                        0,2,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                style.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext,R.color.color_FFB06C)),
                        4,msg.length()-1,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                tv_uid.setText(style);

                tv_msg.setBackgroundResource(R.drawable.item_chat_send_me_to_one_shape);
                tv_msg.setTextColor(ContextCompat.getColor(mContext,R.color.color_ff333333));
            } else {

                String msg = "我 对 所有人 说";
                SpannableStringBuilder style=new SpannableStringBuilder(msg);
                style.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext,R.color.color_6A7EFD)),
                        0,2,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                style.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext,R.color.color_6A7EFD)),
                        4,msg.length()-1,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                tv_uid.setText(style);

                tv_msg.setBackgroundResource(R.drawable.item_chat_send_me_to_all_shape);
                tv_msg.setTextColor(ContextCompat.getColor(mContext,R.color.color_FFFFFF));
            }
        }else {
            tv_uid.setGravity( Gravity.LEFT);
            if (!item.isGroupMsg) {

                String msg = item.srcUserId+" 对 我 说";
                SpannableStringBuilder style=new SpannableStringBuilder(msg);
                style.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext,R.color.color_FFB06C)),
                        0,item.srcUserId.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                style.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext,R.color.color_FFB06C)),
                        item.srcUserId.length()+3,msg.length()-2,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                tv_uid.setText(style);

                tv_msg.setBackgroundResource(R.drawable.item_chat_send_one_to_me_shape);
                tv_msg.setTextColor(ContextCompat.getColor(mContext,R.color.color_ff333333));
            } else {

                String msg = item.srcUserId+" 对 所有人 说";
                SpannableStringBuilder style=new SpannableStringBuilder(msg);
                style.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext,R.color.color_6A7EFD)),
                        0,item.srcUserId.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                style.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext,R.color.color_6A7EFD)),
                        item.srcUserId.length()+3,msg.length()-2,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                tv_uid.setText(style);

                tv_msg.setBackgroundResource(R.drawable.item_chat_send_all_to_me_shape);
                tv_msg.setTextColor(ContextCompat.getColor(mContext,R.color.color_ff333333));
            }
        }
    }
}
