package com.hst.meetingdemo.base;

import android.content.Context;
import android.support.v4.util.ArraySet;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hst.fsp.FspUserInfo;

import java.util.List;

public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseRecycleViewHolder> {
    protected Context mContext;
    protected List<T> mDatas;
    private int mItemLayoutId;

    private boolean mCanClear; // setData ，第一次数据不被删除，后续查找的数据都是新数据，须被删除
    protected boolean mCanSelect;

    private SparseBooleanArray mIsSelected;
    private ArraySet<T> mSelectedItem;


    public BaseRecyclerAdapter(Context context, List<T> datas, int itemLayoutId,boolean canSelect) {
        this(context, datas, itemLayoutId, canSelect, null);
    }

   public BaseRecyclerAdapter(Context context, List<T> datas, int itemLayoutId, boolean canSelect,ArraySet<T> selectedItem) {
        this.mContext = context;
        this.mDatas = datas;
        this.mItemLayoutId = itemLayoutId;

        this.mCanSelect = canSelect;
        this.mCanClear = false;
        if(canSelect)initSelect(selectedItem);
    }

    private void initSelect(ArraySet<T> selectedItem) {
        mIsSelected = new SparseBooleanArray();
        if (selectedItem == null) {
            mSelectedItem = new ArraySet<T>();
            if (mDatas != null && !mDatas.isEmpty()) {
                for (int i = 0; i < mDatas.size(); i++) {
                    mIsSelected.put(i, false);
                }
            }
        } else {
            mSelectedItem = selectedItem;
            if (mDatas != null && !mDatas.isEmpty()) {
                for (int i = 0; i < mDatas.size(); i++) {
                    T d = mDatas.get(i);
                    if (d instanceof FspUserInfo) {
                        FspUserInfo info = (FspUserInfo) mDatas.get(i);
                        for (T it : mSelectedItem) {
                            FspUserInfo item = (FspUserInfo) it;
                            if (item.getUserId().equals(info.getUserId())) {
                                mIsSelected.put(i, true);
                                break;
                            }
                        }
                    }else {
                        if (mSelectedItem.contains(d)) {
                            mIsSelected.put(i, true);
                        } else {
                            mIsSelected.put(i, false);
                        }
                    }
                }
            }
        }
    }


    public void setData(List<T> d) {
        if (mCanClear && mDatas != null && !mDatas.isEmpty()) {
            mDatas.clear();
        }
        this.mDatas = d;
        this.mCanClear = true;
        if (mCanSelect) {
            mIsSelected.clear();
            if (mDatas != null && !mDatas.isEmpty()) {
                for (int i = 0; i < mDatas.size(); i++) {
                    if (mSelectedItem.contains(mDatas.get(i))) {
                        mIsSelected.put(i, true);
                    } else {
                        mIsSelected.put(i, false);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public BaseRecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(mItemLayoutId, parent, false);
        return new BaseRecycleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BaseRecycleViewHolder holder, int position) {
        if (haveHeaderItem()) {
            onBindViewData(holder, position, position == 0 ? null : mDatas.get(position-1));
        } else {
            onBindViewData(holder, position, mDatas.get(position));
        }
    }

    @Override
    public int getItemCount() {
        int nempty = haveHeaderItem() ? 1 : 0;
        return mDatas == null ? nempty : mDatas.size()+nempty;
    }


    protected abstract void onBindViewData(BaseRecycleViewHolder holder, int position, T item);

    protected boolean haveHeaderItem() {return false;}

    protected SparseBooleanArray getIsSelected() {
        return mIsSelected;
    }

    public ArraySet<T> getSelectedItem() {
        return mSelectedItem;
    }

}
