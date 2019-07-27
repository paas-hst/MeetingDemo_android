package com.hst.meetingdemo.base;

import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class BaseRecycleViewHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> mViews = new SparseArray<>();

    public BaseRecycleViewHolder(View itemView) {
        super(itemView);
    }

    public View getItemView() {
        return itemView;
    }

    public <T extends View> T getView(@IdRes int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public void setText(@IdRes int viewId, @StringRes int resId) {
        View view = getView(viewId);
        ((TextView) view).setText(view.getContext().getString(resId));
    }

    public void setText(@IdRes int viewId, CharSequence text) {
        View view = getView(viewId);
        ((TextView) view).setText(text);
    }

    public void setGravity(@IdRes int viewId, int gravity) {
        View view = getView(viewId);
        ((TextView) view).setGravity(gravity);
    }

    public void setText(TextView view, @StringRes int resId) {
        view.setText(view.getContext().getString(resId));
    }

    public void setText(TextView view, CharSequence text) {
        view.setText(text);
    }

    public void setTextColor(int viewId, int color) {
        View view = getView(viewId);
        ((TextView) view).setTextColor(color);
    }

    public void setImageResource(@IdRes int viewId, @DrawableRes int resId) {
        View view = getView(viewId);
        ((ImageView) view).setImageResource(resId);
    }

    public void setImageDrawable(int viewId, Drawable drawable) {
        View view = getView(viewId);
        ((ImageView) view).setImageDrawable(drawable);
    }

    public void setVisibility(int visibility, int... viewIds) {
        if (viewIds == null) {
            return;
        }

        for (int viewId : viewIds) {
            getView(viewId).setVisibility(visibility);
        }
    }
}
