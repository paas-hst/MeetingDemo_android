package com.hst.meetingdemo.ui.main.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;

import com.hst.fsp.FspEngine;
import com.hst.meetingdemo.business.FspEvents;
import com.hst.meetingdemo.business.FspManager;
import com.hst.meetingdemo.utils.FspUtils;
import com.orhanobut.logger.Logger;

public class FspUserViewGroup extends ViewGroup {

    private final int m_margin = 10;

    //userview 的双击检测
    private View m_lastClickUserView = null;
    private long m_lastClickUserViewTime;
    private boolean m_isMax = false;
    private String m_strMaxUserId;
    private String m_strMaxVideoId;


    public FspUserViewGroup(Context context) {
        this(context, null);
        removeAllViews();
    }

    public FspUserViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        removeAllViews();
    }

    public FspUserViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        removeAllViews();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FspUserViewGroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        removeAllViews();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);

        int childrenCount = getChildCount();
        if (childrenCount <= 0) return;
        if (childrenCount <= 2) { // <=2
            for (int i = 0; i < childrenCount; i++) {
                View child = getChildAt(i);
                if (child.isSelected()) {
                    child.measure(widthMeasureSpec, heightMeasureSpec);
                } else {
                    if (m_isMax) {
                        child.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY),
                                MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY));
                        continue;
                    }
                    if (i == 0) {
                        child.measure(widthMeasureSpec, heightMeasureSpec);
                    } else {
                        child.measure(MeasureSpec.makeMeasureSpec(w / 3, MeasureSpec.EXACTLY),
                                MeasureSpec.makeMeasureSpec(h / 3, MeasureSpec.EXACTLY));
                    }
                }
            }
        } else if (childrenCount <= 4) { // <=4
            int cw = (w - m_margin) / 2;
            int ch = (h - m_margin) / 2;
            for (int i = 0; i < childrenCount; i++) {
                View child = getChildAt(i);
                if (child.isSelected()) {
                    child.measure(widthMeasureSpec, heightMeasureSpec);
                } else {
                    if (m_isMax) {
                        child.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY),
                                MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY));
                        continue;
                    }
                    child.measure(MeasureSpec.makeMeasureSpec(cw, MeasureSpec.EXACTLY),
                            MeasureSpec.makeMeasureSpec(ch, MeasureSpec.EXACTLY));
                }
            }
        } else { // <=6
            int cw = (w - m_margin) / 2;
            int ch = (h - 2 * m_margin) / 3;
            for (int i = 0; i < childrenCount; i++) {
                if (i > 6) {
                    break;
                }
                View child = getChildAt(i);
                if (child.isSelected()) {
                    child.measure(widthMeasureSpec, heightMeasureSpec);
                } else {
                    if (m_isMax) {
                        child.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY),
                                MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY));
                        continue;
                    }
                    child.measure(MeasureSpec.makeMeasureSpec(cw, MeasureSpec.EXACTLY),
                            MeasureSpec.makeMeasureSpec(ch, MeasureSpec.EXACTLY));
                }
            }
        }

        setMeasuredDimension(w, h);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childrenCount = getChildCount();
        if (childrenCount <= 0) return;
        if (childrenCount <= 2) { // <=2
            for (int i = 0; i < childrenCount; i++) {
                View child = getChildAt(i);
                if (child.isSelected()) {
                    child.layout(l, t, r, b);
                } else {
                    setChildDoubleClickListener(l, t, r, b, child);
                    if (i == 0) {
                        child.layout(l, t, r, b);
                    } else {
                        child.layout(r * 2 / 3, t, r, b / 3);
                    }
                }
            }
        } else if (childrenCount <= 4) { // <=4
            int cw = (r - m_margin) / 2;
            int ch = (b - m_margin) / 2;
            for (int i = 0; i < childrenCount; i++) {
                View child = getChildAt(i);
                if (child.isSelected()) {
                    child.layout(l, t, r, b);
                } else {
                    setChildDoubleClickListener(l, t, r, b, child);
                    if (i == 0) {
                        child.layout(l, t, cw, ch);
                    } else if (i == 1) {
                        child.layout(cw + m_margin, t, r, ch);
                    } else if (i == 2) {
                        child.layout(l, ch + m_margin, cw, b);
                    } else {
                        child.layout(cw + m_margin, ch + m_margin, r, b);
                    }
                }
            }
        } else { // <=6
            int cw = (r - m_margin) / 2;
            int ch = (b - m_margin * 2) / 3;
            for (int i = 0; i < childrenCount; i++) {
                View child = getChildAt(i);
                if (child.isSelected()) {
                    child.layout(l, t, r, b);
                } else {
                    setChildDoubleClickListener(l, t, r, b, child);
                    if (i == 0) {
                        child.layout(l, t, cw, ch);
                    } else if (i == 1) {
                        child.layout(cw + m_margin, t, r, ch);
                    } else if (i == 2) {
                        child.layout(l, ch + m_margin, cw, 2 * ch + m_margin);
                    } else if (i == 3) {
                        child.layout(cw + m_margin, ch + m_margin, r, 2 * ch + m_margin);
                    } else if (i == 4) {
                        child.layout(l, ch * 2 + m_margin * 2, cw, b);
                    } else if (i == 5) {
                        child.layout(cw + m_margin, ch * 2 + m_margin * 2, r, b);
                    } else {
                        break;
                    }
                }
            }
        }
    }

    private void setChildDoubleClickListener(final int l, final int t, final int r, final int b, View child) {
        child.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                long curTime = System.currentTimeMillis();
                if (curTime - m_lastClickUserViewTime < 300 && v == m_lastClickUserView) {

                    if (v.isSelected()) { // min
                        v.setSelected(false);
                        m_isMax = false;
                        m_strMaxUserId = null;
                        m_strMaxUserId = null;
                        if (v.getTag() != null && v.getTag() instanceof Rect) {
                            Rect rect = (Rect) v.getTag();
                            setVisibleOutside(v, View.VISIBLE);
                            v.measure(MeasureSpec.makeMeasureSpec(rect.right - rect.left, MeasureSpec.EXACTLY),
                                    MeasureSpec.makeMeasureSpec(rect.bottom - rect.top, MeasureSpec.EXACTLY));
                            v.layout(rect.left, rect.top, rect.right, rect.bottom);
                        }

                        //取消最大化，被关掉的视频重新显示
                        int childrenCount = getChildCount();
                        for (int i = 0; i < childrenCount; i++) {
                            FspUserView userview = (FspUserView)getChildAt(i);
                            if (!FspUtils.isSameText(userview.getUserId(), m_strMaxUserId)
                                    || !FspUtils.isSameText(userview.getVideoId(), m_strMaxVideoId)) {
                                userview.resumeVideo();
                            }
                        }

                    } else { // max
                        v.setSelected(true);
                        FspUserView videoView = (FspUserView)v;
                        m_isMax = true;
                        m_strMaxUserId = videoView.getUserId();
                        m_strMaxVideoId = videoView.getVideoId();
                        if (v.getTag() != null && v.getTag() instanceof Rect) {
                            Rect rect = (Rect) v.getTag();
                            rect.set(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                            v.setTag(rect);
                        } else {
                            v.setTag(new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom()));
                        }
                        setVisibleOutside(v, View.GONE);
                        v.measure(MeasureSpec.makeMeasureSpec(r - l, MeasureSpec.EXACTLY),
                                MeasureSpec.makeMeasureSpec(b - t, MeasureSpec.EXACTLY));
                        v.layout(l, t, r, b);

                        //最大化，其他视频都停止接收
                        int childrenCount = getChildCount();
                        for (int i = 0; i < childrenCount; i++) {
                            FspUserView userview = (FspUserView)getChildAt(i);
                            if (!FspUtils.isSameText(userview.getUserId(), m_strMaxUserId)
                                    || !FspUtils.isSameText(userview.getVideoId(), m_strMaxVideoId)) {
                                userview.pauseVideo();
                            }
                        }
                    }
                }

                m_lastClickUserViewTime = curTime;
                m_lastClickUserView = v;

            }
        });
    }

    private void setVisibleOutside(View v, int visible) {
        int childrenCount = getChildCount();
        if (childrenCount <= 0) return;
        for (int i = 0; i < childrenCount; i++) {
            View child = getChildAt(i);
            if (v != child) {
                child.setVisibility(visible);
                if (child != null && child instanceof FspUserView) {
                    ((FspUserView) child).setRemoteVideoChange();
                }
            }
        }
    }

    public void onOneSecondTimer() {
        int childrenCount = getChildCount();
        if (childrenCount <= 0) return;
        for (int i = 0; i < childrenCount; i++) {
            View child = getChildAt(i);
            if (child != null && child instanceof FspUserView) {
                ((FspUserView) child).onOneSecondTimer();
            }
        }
    }

    public void onDestroy() {
        int childrenCount = getChildCount();
        if (childrenCount <= 0) return;
        for (int i = 0; i < childrenCount; i++) {
            View child = getChildAt(i);
            if (child != null && child instanceof FspUserView) {
                FspUserView userView = (FspUserView) child;
                userView.closeAudio();
                userView.closeVideo();
            }
        }
        removeAllViews();
    }


    public void onEventRemoteVideoAndAudio() {
        FspManager fsm = FspManager.getInstance();
        for (Pair<String, String> remote_video_info : fsm.getRemoteVideos()) {
            FspUserView videoView = ensureUserView(remote_video_info.first, remote_video_info.second, true);
            if (videoView == null) {
                Logger.e("videoView == null: %s, %s", remote_video_info.first, remote_video_info.second);
            } else {
                videoView.openVideo();
                if (!m_isMax &&
                        (!FspUtils.isSameText(remote_video_info.first, m_strMaxUserId)
                        || !FspUtils.isSameText(remote_video_info.second, m_strMaxVideoId))) {
                    if (!fsm.setRemoteVideoRender(remote_video_info.first,
                            remote_video_info.second,
                            videoView.getSurfaceView(),
                            videoView.getVideoRenderMode())) {
                        videoView.closeVideo();
                        removeChildView(videoView);
                    }
                }
            }
        }

        for (String remote_audio_userid : fsm.getRemoteAudios()) {
            FspUserView videoView = ensureUserView(remote_audio_userid, null, true);
            if (videoView == null) {
                Logger.e("videoView == null: %s", remote_audio_userid);
            } else {
                videoView.openAudio();
            }
        }
    }


    public void onEventRemoteVideo(FspEvents.RemoteVideoEvent event) {
        FspManager fspManager = FspManager.getInstance();
        FspUserView videoView = ensureUserView(event.userid, event.videoid,
                event.eventtype == FspEngine.REMOTE_VIDEO_PUBLISH_STARTED);
        if (videoView == null) {
            Logger.e("videoView == null  userId: %s, videoId : %s", event.userid, event.videoid);
            return;
        }

        if (event.eventtype == FspEngine.REMOTE_VIDEO_PUBLISH_STARTED) {
            videoView.openVideo();
            Logger.e("openVideo success , surfaceView : " + videoView.getSurfaceView());
            if (!fspManager.setRemoteVideoRender(event.userid, event.videoid,
                    videoView.getSurfaceView(), videoView.getVideoRenderMode())) {
                videoView.closeVideo();
                removeChildView(videoView);
            }
        } else if (event.eventtype == FspEngine.REMOTE_VIDEO_PUBLISH_STOPED) {
            videoView.closeVideo();
            removeChildView(videoView);
        }
    }

    public void onEventRemoteAudio(FspEvents.RemoteAudioEvent event) {
        FspUserView videoView = ensureUserView(event.userid, null, event.eventtype == FspEngine.REMOTE_AUDIO_PUBLISH_STARTED);
        if (videoView == null) {
            Logger.e("videoView == null  userId: %s", event.userid);
            return;
        }

        if (event.eventtype == FspEngine.REMOTE_AUDIO_PUBLISH_STARTED) {
            videoView.openAudio();
        } else if (event.eventtype == FspEngine.REMOTE_AUDIO_PUBLISH_STOPED) {
            videoView.closeAudio();
            if (!videoView.hasVideoAudio())
                removeChildView(videoView);
        }
    }


    public boolean startPublishLocalVideo(boolean isFront) {
        FspManager fspManager = FspManager.getInstance();
        FspUserView videoView = ensureUserView(fspManager.getSelfUserId(), null, true);
        if (videoView != null) {
            videoView.openVideo();
            if (fspManager.publishVideo(isFront, videoView.getSurfaceView())) {
                return true;
            } else {
                videoView.closeVideo();
                removeChildView(videoView);
            }
        }
        Logger.e("videoView == null");
        return false;
    }

    public boolean stopPublishLocalVideo() {
        FspManager fspManager = FspManager.getInstance();
        FspUserView videoView = ensureUserView(fspManager.getSelfUserId(), null, false);
        if (videoView != null) {
            videoView.closeVideo();
            removeChildView(videoView);
            return true;
        }
        Logger.e("videoView == null");
        return false;
    }

    public boolean startPublishLocalAudio() {
        FspManager fspManager = FspManager.getInstance();
        FspUserView videoView = ensureUserView(fspManager.getSelfUserId(), null, false);
        if (videoView != null) {
            videoView.openAudio();
            return true;
        }
        Logger.e("videoView == null");
        return false;
    }

    public boolean stopPublishLocalAudio() {
        FspManager fspManager = FspManager.getInstance();
        FspUserView videoView = ensureUserView(fspManager.getSelfUserId(), null, false);
        if (videoView != null) {
            videoView.closeAudio();
            if (!videoView.hasVideoAudio())
                removeChildView(videoView);
            return true;
        }
        Logger.e("videoView == null");
        return false;
    }

    private void removeChildView(FspUserView videoView) {
        if (!videoView.hasVideoAudio()) removeView(videoView);
    }

    private FspUserView ensureUserView(String userId, String videoId, boolean isCreateFspUserView) {
        FspUserView view = null;
        int childrenCount = getChildCount();
        if (childrenCount > 0) {
            //search
            for (int i = 0; i < childrenCount; i++) {
                View child = getChildAt(i);
                if (child != null && child instanceof FspUserView) {
                    FspUserView userView = ((FspUserView) child);
                    if (FspUtils.isSameText(userId, userView.getUserId()) && FspUtils.isSameText(videoId, userView.getVideoId())) {
                        view = userView;
                        break;
                    }
                }
            }
        }
        // create
        if (view == null) {
            if (childrenCount < 6) {
                if (isCreateFspUserView) {
                    FspUserView userView = new FspUserView(getContext());
                    userView.setUserId(userId);
                    userView.setVideoId(videoId);
                    view = userView;
                    addView(userView);
                }
            } else if (childrenCount == 6) {

            }
        }
        return view;
    }

}
