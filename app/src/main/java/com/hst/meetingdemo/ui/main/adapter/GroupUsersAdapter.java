package com.hst.meetingdemo.ui.main.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hst.meetingdemo.R;
import com.hst.meetingdemo.base.BaseRecycleViewHolder;
import com.hst.meetingdemo.base.BaseRecyclerAdapter;
import com.hst.meetingdemo.bean.EventMsgEntity;
import com.hst.meetingdemo.business.FspConstants;
import com.hst.meetingdemo.business.FspManager;

import java.util.List;

public class GroupUsersAdapter extends BaseRecyclerAdapter<String> {

    public GroupUsersAdapter(Context context) {
        super(context, FspManager.getInstance().getGroupUsers(), R.layout.item_group_user, false);
    }

    @Override
    protected void onBindViewData(BaseRecycleViewHolder holder, int position, final String item) {
        TextView tv_uid = holder.getView(R.id.group_user_item_tv_userid);
        tv_uid.setText(item);
        ImageView iv_audio = holder.getView(R.id.group_user_item_iv_audio);
        ImageView iv_video = holder.getView(R.id.group_user_item_iv_video);
        ImageView iv_screenshare = holder.getView(R.id.group_user_item_iv_screenshare);
        if (FspManager.getInstance().HaveUserAudio(item))
        {
            iv_audio.setImageResource(R.drawable.group_user_audio_open);
        } else {
            iv_audio.setImageResource(R.drawable.group_user_audio_close);
        }

        if (FspManager.getInstance().HaveUserVideo(item))
        {
            iv_video.setImageResource(R.drawable.group_user_video_open);
        } else {
            iv_video.setImageResource(R.drawable.group_user_video_close);
        }

        if (FspManager.getInstance().HaveUserScreenShare(item))
        {
            iv_screenshare.setImageResource(R.drawable.group_user_screenshare_open);
        } else {
            iv_screenshare.setImageResource(R.drawable.group_user_screenshare_close);
        }

    }
}
