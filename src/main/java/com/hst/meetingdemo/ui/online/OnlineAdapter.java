package com.hst.meetingdemo.ui.online;

import android.content.Context;
import android.support.v4.util.ArraySet;
import android.view.View;
import android.widget.ImageView;

import com.hst.fsp.FspEngine;
import com.hst.fsp.FspUserInfo;
import com.hst.meetingdemo.R;
import com.hst.meetingdemo.base.BaseRecycleViewHolder;
import com.hst.meetingdemo.base.BaseRecyclerAdapter;
import com.hst.meetingdemo.business.FspManager;
import com.hst.meetingdemo.utils.FspUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class OnlineAdapter extends BaseRecyclerAdapter<FspUserInfo> {

    public OnlineAdapter(Context context, List<FspUserInfo> datas) {
        this(context, datas, true);
    }

    public OnlineAdapter(Context context, List<FspUserInfo> datas, boolean canSelect) {
        this(context, datas, canSelect, null);
    }

    public OnlineAdapter(Context context, List<FspUserInfo> datas, boolean canSelect, ArraySet<FspUserInfo> selectedItem) {
        super(context, datas, R.layout.item_online_select, canSelect, selectedItem);
    }

    @Override
    protected void onBindViewData(BaseRecycleViewHolder holder, final int position, final FspUserInfo item) {
        final ImageView iv_select = holder.getView(R.id.online_item_ck_select);
        if (!mCanSelect) {
            iv_select.setVisibility(View.GONE);
        }
        String strUserName = FspUtils.isEmptyText(item.getCustomInfo()) ? item.getUserId() : item.getCustomInfo();
        if (isMe(item.getUserId())) {
            holder.setImageResource(R.id.online_item_iv_status, R.drawable.online_item_circle_shape_online);
            holder.setText(R.id.online_item_tv_status, R.string.online);
            holder.setText(R.id.online_item_tv_groupId, strUserName  + "  （我）");
            iv_select.setEnabled(false);
            iv_select.setImageResource(R.drawable.online_item_ck_normal);
            iv_select.setOnClickListener(null);
        } else if (item.getStatus() == FspUserInfo.FSP_USER_STATUS_ONLINE) {
            holder.setImageResource(R.id.online_item_iv_status, R.drawable.online_item_circle_shape_online);
            holder.setText(R.id.online_item_tv_status, R.string.online);
            holder.setText(R.id.online_item_tv_groupId, strUserName);
            iv_select.setEnabled(true);
            if (mCanSelect) {
                if (getIsSelected().get(position)) {
                    iv_select.setImageResource(R.drawable.online_item_ck_select);
                } else {
                    iv_select.setImageResource(R.drawable.online_item_ck_normal_shape);
                }
                iv_select.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!getIsSelected().get(position)) {
                            getIsSelected().put(position, true);
                            iv_select.setImageResource(R.drawable.online_item_ck_select);
                            if (getSelectedItem().size() == 0) {
                                inviteSelectListener(true);
                            }
                            if (!getSelectedItem().contains(item)) {
                                getSelectedItem().add(item);
                            }

                        } else {
                            getIsSelected().put(position, false);
                            iv_select.setImageResource(R.drawable.online_item_ck_normal_shape);
                            getSelectedItem().remove(item);
                            if (getSelectedItem().size() == 0) {
                                inviteSelectListener(false);
                            }
                        }
                    }
                });
            }
        } else if (item.getStatus() == FspUserInfo.FSP_USER_STATUS_OFFLINE) {
            holder.setImageResource(R.id.online_item_iv_status, R.drawable.online_item_circle_shape_offline);
            holder.setText(R.id.online_item_tv_status, R.string.offline);
            holder.setText(R.id.online_item_tv_groupId, strUserName);
            iv_select.setEnabled(false);
            iv_select.setImageResource(R.drawable.online_item_ck_normal);
            iv_select.setOnClickListener(null);
        }
    }

    protected abstract void inviteSelectListener(boolean hasInvite);

    public int getSelectedItemCount() {
        return getSelectedItem() == null ? 0 : getSelectedItem().size();
    }


    public String[] getSelectedItemArray() {
        if (getSelectedItem() == null || getSelectedItem().isEmpty()) {
            return null;
        }
        int len = getSelectedItem().size();
        String[] array = new String[len];
        int i = 0;
        for (; i < len; i++) {
            array[i] = getSelectedItem().valueAt(i).getUserId();
        }
        return array;
    }

    private boolean isMe(String userId) {
        return FspManager.getInstance().getSelfUserId() != null && FspManager.getInstance().getSelfUserId().equals(userId);
    }

    public boolean selectAll() {
        if (mDatas != null && !mDatas.isEmpty()) {
            for (int i = 0; i < mDatas.size(); i++) {
                if (isMe(mDatas.get(i).getUserId())) {
                    continue;
                }
                if (mDatas.get(i).getStatus() != FspUserInfo.FSP_USER_STATUS_ONLINE) {
                    continue;
                }
                if (!getIsSelected().get(i)) {
                    getIsSelected().put(i, true);
                    if (!getSelectedItem().contains(mDatas.get(i))) {
                        getSelectedItem().add(mDatas.get(i));
                    }
                }
            }
            this.notifyDataSetChanged();
            return true;
        }
        return false;
    }

    public boolean unSelectAll() {
        if (mDatas != null && !mDatas.isEmpty()) {
            for (int i = 0; i < mDatas.size(); i++) {
                if (isMe(mDatas.get(i).getUserId())) {
                    continue;
                }
                if (mDatas.get(i).getStatus() != FspUserInfo.FSP_USER_STATUS_ONLINE) {
                    continue;
                }
                if (getIsSelected().get(i)) {
                    getIsSelected().put(i, false);
                    if (getSelectedItem().contains(mDatas.get(i))) {
                        getSelectedItem().remove(mDatas.get(i));
                    }
                }
            }
            this.notifyDataSetChanged();
            return true;
        }
        return false;
    }

    public void searchGroupId(String groupId, List<FspUserInfo> data) {
        if (data == null || data.isEmpty()) {
            return;
        }
        if (groupId == null || groupId.equals("")) {
            List<FspUserInfo> newList = new ArrayList();
            for (FspUserInfo item : data) {
                newList.add(item);
            }
            setData(newList);
        } else {
            List<FspUserInfo> newList = new ArrayList();
            for (FspUserInfo item : data) {
                if (item.getUserId().equals(groupId)) {
                    newList.add(item);
                }
            }
            setData(newList);
        }
    }
}
