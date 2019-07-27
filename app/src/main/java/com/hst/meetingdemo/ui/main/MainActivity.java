package com.hst.meetingdemo.ui.main;


import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hst.fsp.FspEngine;
import com.hst.meetingdemo.R;
import com.hst.meetingdemo.base.BaseActivity;
import com.hst.meetingdemo.base.BaseDialog;
import com.hst.meetingdemo.bean.EventMsgEntity;
import com.hst.meetingdemo.bean.MainFinishEntity;
import com.hst.meetingdemo.business.FspConstants;
import com.hst.meetingdemo.business.FspEvents;
import com.hst.meetingdemo.business.FspManager;
import com.hst.meetingdemo.business.FspPreferenceManager;
import com.hst.meetingdemo.ui.main.adapter.EventMsgAdapter;
import com.hst.meetingdemo.ui.main.dialog.CameraListDialog;
import com.hst.meetingdemo.ui.main.dialog.ChatMsgDialog;
import com.hst.meetingdemo.ui.main.dialog.InviteDialog;
import com.hst.meetingdemo.ui.main.dialog.GroupUsersDialog;
import com.hst.meetingdemo.ui.main.view.FspUserViewGroup;
import com.hst.meetingdemo.ui.setting.SettingActivity;
import com.hst.meetingdemo.utils.ActivityUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 主页
 */
public class MainActivity extends BaseActivity {

    @BindView(R.id.toolbar_btn_video)
    TextView m_ivToolbarBtnVideo;
    @BindView(R.id.toolbar_btn_microphone)
    TextView m_ivToolbarBtnAudio;
    @BindView(R.id.toolbar_btn_share)
    TextView m_ivToolbarBtnShare;
    @BindView(R.id.toolbar_btn_user)
    TextView m_ivToolbarBtnUser;
    @BindView(R.id.toolbar_btn_chat)
    TextView m_ivToolbarBtnChat;
    @BindView(R.id.toolbar_btn_more)
    TextView m_ivToolbarBtnMore;

    @BindView(R.id.toolbar_layout_share)
    LinearLayout m_ivToolbarLayoutShare;
    @BindView(R.id.toolbar_btn_share_file)
    TextView m_ivToolbarBtnShareFile;
    @BindView(R.id.toolbar_btn_share_screen)
    TextView m_ivToolbarBtnShareScreen;
    @BindView(R.id.toolbar_btn_share_write)
    TextView m_ivToolbarBtnShareWrite;

    @BindView(R.id.toolbar_layout_more)
    LinearLayout m_ivLayoutBtnMore;
    @BindView(R.id.toolbar_btn_set)
    TextView m_ivToolbarBtnSet;
    @BindView(R.id.toolbar_btn_record)
    TextView m_ivToolbarBtnRecord;
    @BindView(R.id.toolbar_btn_call)
    TextView m_ivToolbarBtnCall;

    @BindView(R.id.main_rv_event_msg)
    RecyclerView m_rvRventMsg;

    @BindView(R.id.fsp_user_view_group)
    FspUserViewGroup m_fspUserViewGroup;

    private Handler m_handler = new Handler();

    private long m_exitTime = 0;

    // eventMsg
    private EventMsgAdapter m_eventMsgAdapter;

    // chat or call or user dialog
    private BaseDialog m_curOperateDialog;

    // save uid senderSelect
    private String m_remoteUserId;

    private List<FspEvents.ChatMsgItem> m_chatMsgLists = new LinkedList<>();
    private LinkedList<EventMsgEntity> m_eventMsgLists = new LinkedList<>();

    @Override
    protected void initWindowFlags() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {

        //可能在LoginActivity 切换 到 Mainactivtiy期间， 收到了 sdk的onRemoteVideoEvent
        //将保存的视频列表逐一打开
        m_handler.post(new Runnable() {
            @Override
            public void run() {
                FspManager fsp = FspManager.getInstance();
                FspPreferenceManager fspPreferenceManager = FspPreferenceManager.getInstance();

                // camera
                if (fspPreferenceManager.getDefaultOpenCamera()) {
                    int curVideoState = fsp.currentVideState();
                    if (curVideoState != FspConstants.LOCAL_VIDEO_CLOSED) {
                        startPublishLocalVideo(curVideoState == FspConstants.LOCAL_VIDEO_BACK_PUBLISHED);
                    }
                }

                // mic
                if (fspPreferenceManager.getDefaultOpenMIC()) {
                    if (!fsp.isAudioPublishing() && fsp.startPublishAudio()) {
                        m_ivToolbarBtnAudio.setSelected(true);
                    }
                }

                // remote video audio
                m_fspUserViewGroup.onEventRemoteVideoAndAudio();
            }
        });

        // initEventMsgAdapter
        m_rvRventMsg.setLayoutManager(new LinearLayoutManager(this));
        m_eventMsgAdapter = new EventMsgAdapter(this,  m_eventMsgLists);
        m_rvRventMsg.setAdapter(m_eventMsgAdapter);
        if (m_eventMsgAdapter.getItemCount() > 0) {
            m_rvRventMsg.smoothScrollToPosition(m_eventMsgAdapter.getItemCount());
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - m_exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次返回在线页面",
                        Toast.LENGTH_SHORT).show();
                m_exitTime = System.currentTimeMillis();
            } else {
                leaveGroup();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void LeaveGroupResultSuccess() {
        destroy();
        super.LeaveGroupResultSuccess();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroy();
    }

    private void destroy() {
        m_fspUserViewGroup.onDestroy();
        dismissBaseDialog();
        if (m_handler != null) {
            m_handler.removeCallbacks(m_OneSecondRunnable);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        m_handler.removeCallbacks(m_OneSecondRunnable);
        m_handler.postDelayed(m_OneSecondRunnable, 1000);
    }

    private int m_nRefreshCount = 5;

    private Runnable m_OneSecondRunnable = new Runnable() {
        @Override
        public void run() {
            if (isPause()) return;

            try {
                m_handler.postDelayed(this, 1000);
                m_fspUserViewGroup.onOneSecondTimer();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (m_nRefreshCount <= 0) {
                FspManager.getInstance().refreshAllUserStatus();
                m_nRefreshCount = 5;
            } else {
                m_nRefreshCount--;
            }
        }
    };

    // ------------------- toolbar click start -----------------------

    @OnClick(R.id.toolbar_btn_microphone)
    public void onBtnToolbarMicrophone() {
        FspManager fsp = FspManager.getInstance();
        if (fsp.isAudioPublishing()) {
            if (fsp.stopPublishAudio()) {
                m_fspUserViewGroup.stopPublishLocalAudio();
                m_ivToolbarBtnAudio.setSelected(false);
            }
        } else {
            if (fsp.startPublishAudio()) {
                m_fspUserViewGroup.startPublishLocalAudio();
                m_ivToolbarBtnAudio.setSelected(true);
            }
        }
    }

    @OnClick(R.id.toolbar_btn_video)
    public void onBtnToolbarVideo() {
        final FspManager fspManger = FspManager.getInstance();
        final int curVideoState = fspManger.currentVideState();

        final ArrayList<String> videoDevices = new ArrayList<>();
        videoDevices.add("前置摄像头");
        videoDevices.add("后置摄像头");
        videoDevices.add("关闭视频");

        int nCurrentSelectedIndex = -1;
        if (curVideoState == FspConstants.LOCAL_VIDEO_FRONT_PUBLISHED) {
            nCurrentSelectedIndex = 0;
        } else if (curVideoState == FspConstants.LOCAL_VIDEO_BACK_PUBLISHED) {
            nCurrentSelectedIndex = 1;
        } else if (curVideoState == FspConstants.LOCAL_VIDEO_CLOSED) {
            nCurrentSelectedIndex = 2;
        }

        CameraListDialog.CheckListDialogListener selectListener = new CameraListDialog.CheckListDialogListener() {
            @Override
            public void onItemSelected(int selectedIndex) {
                if (selectedIndex == 0) {
                    //选择前置摄像头
                    if (curVideoState != FspConstants.LOCAL_VIDEO_FRONT_PUBLISHED) {
                        if (curVideoState == FspConstants.LOCAL_VIDEO_CLOSED) {
                            startPublishLocalVideo(true);
                        } else {
                            fspManger.switchCamera();
                        }
                    }
                } else if (selectedIndex == 1) {
                    //选择后置摄像头
                    if (curVideoState != FspConstants.LOCAL_VIDEO_BACK_PUBLISHED) {
                        if (curVideoState == FspConstants.LOCAL_VIDEO_CLOSED) {
                            startPublishLocalVideo(false);
                        } else {
                            fspManger.switchCamera();
                        }
                    }
                } else if (selectedIndex == 2) {
                    //关闭摄像头
                    if (fspManger.stopVideoPublish()) {
                        stopPublishLocalVideo();
                    }
                }
            }
        };

        final CameraListDialog dialog = new CameraListDialog(
                this, "选择摄像头",
                videoDevices, nCurrentSelectedIndex,
                selectListener);

        dialog.show();
    }


    private void startPublishLocalVideo(boolean isFront) {
        if (m_fspUserViewGroup.startPublishLocalVideo(isFront)) {
            m_ivToolbarBtnVideo.setSelected(true);
        }
    }

    private void stopPublishLocalVideo() {
        if (m_fspUserViewGroup.stopPublishLocalVideo()) {
            m_ivToolbarBtnVideo.setSelected(false);
        }
    }

    @OnClick(R.id.toolbar_btn_share)
    public void onBtnToolbarShare() {
        if (m_ivToolbarBtnShare.isSelected()) {
            m_ivToolbarBtnShare.setSelected(false);
            m_ivToolbarLayoutShare.setVisibility(View.INVISIBLE);
        } else {
            m_ivToolbarBtnShare.setSelected(true);
            m_ivToolbarLayoutShare.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.toolbar_btn_share_write)
    public void onBtnToolbarShareWrite() {
    }

    @OnClick(R.id.toolbar_btn_share_screen)
    public void onBtnToolbarShareScreen() {
    }

    @OnClick(R.id.toolbar_btn_share_file)
    public void onBtnToolbarShareFile() {
    }

    @OnClick(R.id.toolbar_btn_user)
    public void onBtnToolbarUser() {
        if (m_ivToolbarBtnUser.isSelected()) {
            m_ivToolbarBtnUser.setSelected(false);
            dismissBaseDialog();
        } else {
            m_ivToolbarBtnUser.setSelected(true);
            m_curOperateDialog = new GroupUsersDialog(this)
                    .setOnDialogDismissListener(new BaseDialog.OnDialogDismissListener() {
                        @Override
                        public void onDismiss() {
                            m_ivToolbarBtnUser.setSelected(false);
                        }
                    });
            m_curOperateDialog.show();
        }
    }

    @OnClick(R.id.toolbar_btn_chat)
    public void onBtnToolbarChat() {
        if (m_ivToolbarBtnChat.isSelected()) {
            m_ivToolbarBtnChat.setSelected(false);
            dismissBaseDialog();
        } else {
            m_ivToolbarBtnChat.setSelected(true);
            m_curOperateDialog = new ChatMsgDialog(this,
                    m_chatMsgLists, m_remoteUserId)
                    .setOnDialogItemSenderSelectListener(new ChatMsgDialog.onDialogItemSenderSelectListener() {
                        @Override
                        public void onItemSelected(String uidSel) {
                            m_remoteUserId = uidSel;
                        }
                    })
                    .setOnDialogDismissListener(new BaseDialog.OnDialogDismissListener() {
                        @Override
                        public void onDismiss() {
                            m_ivToolbarBtnChat.setSelected(false);
                        }
                    });
            m_curOperateDialog.show();
        }
    }

    @OnClick(R.id.toolbar_btn_more)
    public void onBtnToolbarMore() {
        if (m_ivToolbarBtnMore.isSelected()) {
            m_ivToolbarBtnMore.setSelected(false);
            m_ivLayoutBtnMore.setVisibility(View.INVISIBLE);
        } else {
            m_ivToolbarBtnMore.setSelected(true);
            m_ivLayoutBtnMore.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.toolbar_btn_call)
    public void onBtnToolbarCall() {
        if (m_ivToolbarBtnCall.isSelected()) {
            m_ivToolbarBtnCall.setSelected(false);
            dismissBaseDialog();
        } else {
            m_ivToolbarBtnCall.setSelected(true);
            m_curOperateDialog = new InviteDialog(this)
                    .setOnDialogDismissListener(new BaseDialog.OnDialogDismissListener() {
                        @Override
                        public void onDismiss() {
                            m_ivToolbarBtnCall.setSelected(false);
                        }
                    });
            m_curOperateDialog.show();
        }
    }

    public void dismissBaseDialog() {
        if (m_curOperateDialog != null && m_curOperateDialog.isShowing()) {
            m_curOperateDialog.dismiss();
        }
    }

    @OnClick(R.id.toolbar_btn_record)
    public void onBtnToolbarRecord() {

    }


    @OnClick(R.id.toolbar_btn_set)
    public void onBtnToolbarSetting() {
        startActivity(new Intent(this, SettingActivity.class));
    }

    // ------------------- toolbar click end -----------------------

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventFinish(MainFinishEntity entity) {
        ActivityUtils.finishActivity(this, entity.isFinish());
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMsgIncome(EventMsgEntity eventMsgEntity) {
        m_eventMsgLists.push(eventMsgEntity);

        if (m_rvRventMsg != null && m_eventMsgAdapter != null) {
            m_eventMsgAdapter.notifyDataSetChanged();
            m_rvRventMsg.smoothScrollToPosition(m_eventMsgAdapter.getItemCount());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventRemoteVideo(FspEvents.RemoteVideoEvent event) {
        m_fspUserViewGroup.onEventRemoteVideo(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventRemoteAudio(FspEvents.RemoteAudioEvent event) {
        m_fspUserViewGroup.onEventRemoteAudio(event);
    }

    // userMsg and groupMsg
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMsgIncome(FspEvents.ChatMsgItem msgItem) {
        m_chatMsgLists.add(msgItem);
        if (m_curOperateDialog instanceof ChatMsgDialog && m_curOperateDialog.isShowing()) {
            m_curOperateDialog.notifyDataSetChanged();
        } else {
            m_eventMsgLists.push(new EventMsgEntity(msgItem.srcUserId, msgItem.msg));
            m_eventMsgAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRemoteUserEvent(FspEvents.RemoteUserEvent event)
    {
        if (m_curOperateDialog != null && m_curOperateDialog.isShowing()) {
            m_curOperateDialog.notifyDataSetChanged();
        }

        if (event.eventtype == FspEngine.REMOTE_USER_JOIN_GROUP) {
            m_eventMsgLists.push(new EventMsgEntity(event.userid, "加入了组"));
        }else if (event.eventtype == FspEngine.REMOTE_USER_LEAVE_GROUP) {
            m_eventMsgLists.push(new EventMsgEntity(event.userid, "离开了组"));
        }

        m_eventMsgAdapter.notifyDataSetChanged();
    }

    // inviteIncome dialog need this
    @Override
    public void onEventRefreshUserStatusFinished(FspEvents.RefreshUserStatusFinished status) {
        if (m_curOperateDialog != null && m_curOperateDialog.isShowing()) {
            m_curOperateDialog.notifyDataSetChanged(status);
        }
    }
}
