package com.hst.meetingdemo.ui.white_board;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hst.fsp.FspBoardView;
import com.hst.fsp.IFspWhiteBoard;
import com.hst.fsp.WhiteBoardInfo;
import com.hst.meetingdemo.R;
import com.hst.meetingdemo.base.BaseActivity;
import com.hst.meetingdemo.business.FspEvents;
import com.hst.meetingdemo.business.FspManager;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;
import permissions.dispatcher.OnNeverAskAgain;

/**
 * 设置
 */
public class WhiteBoardActivity extends BaseActivity {

    @BindView(R.id.wb_btn_edit_start)
    ImageView m_ivBtnEditStart;
    @BindView(R.id.wb_btn_edit_penstyle)
    ImageView m_ivBtnEditPenStyle;
    @BindView(R.id.wb_btn_edit_pen)
    ImageView m_ivBtnPen;
    @BindView(R.id.wb_btn_edit_redo)
    ImageView m_ivBtnRedo;
    @BindView(R.id.wb_btn_edit_undo)
    ImageView m_ivBtnUndo;
    @BindView(R.id.wb_btn_edit_delete)
    ImageView m_ivBtnDelete;
    @BindView(R.id.wb_tv_title)
    TextView m_tvTitle;
    @BindView(R.id.fsp_board_view)
    FspBoardView m_boardView;
    @BindView(R.id.wb_layout_pen_styles)
    View m_layoutPenStyle;
    @BindViews({ R.id.wb_btn_pen_width1, R.id.wb_btn_pen_width2, R.id.wb_btn_pen_width3 })
    List<ImageView> m_btnPenWidths;
    @BindViews({ R.id.wb_btn_pen_color1, R.id.wb_btn_pen_color2, R.id.wb_btn_pen_color3 })
    List<ImageView> m_btnPenColors;
    @BindView(R.id.wb_recyclerview_boardlist)
    RecyclerView m_boardNameListView;
    @BindView(R.id.wb_tv_pages)
    TextView m_tvPages;
    @BindView(R.id.wb_layout_operate_panel)
    View m_layoutOperatePanel;

    WhiteBoardListAdapter m_boardListAdapter;

    private String m_strCurBoardId;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_white_board;
    }

    @Override
    protected void init() {
        m_boardNameListView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        m_boardNameListView.setLayoutManager(mLayoutManager);
        m_boardNameListView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        m_boardListAdapter = new WhiteBoardListAdapter();
        m_boardNameListView.setAdapter(m_boardListAdapter);

        Intent intent = getIntent();
        String docPath = intent.getStringExtra("docPath");
        if (docPath != null) {
            int nres = FspManager.getInstance().getFspEngine().getFspBoard().createDocWhiteBoard(docPath);
            Logger.d("Wb doc Create res:" + nres + ", filepath="+docPath);
        } else {
            int nres = FspManager.getInstance().getFspEngine().getFspBoard().createBlankWhiteBoard(FspManager.getInstance().generNewWhiteBoardName(),
                    720, 1080, 2);
            Logger.d("Wb blank Create res:" + nres);
        }

        m_boardView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                HideAllPanel();
                return false;
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventWhiteBoardPublish(FspEvents.WhiteBoardPublishEvent eventWbPublish) {
        if (eventWbPublish.isStop) {

        } else {
            if (TextUtils.isEmpty(m_strCurBoardId)) {
                m_strCurBoardId = eventWbPublish.boardId;
                changeToWb(eventWbPublish.boardId);
            }
        }
        m_boardListAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventWhiteBoardInfoUpdate(FspEvents.WhiteBoardInfoUpdateEvent eventWbInfoUpdate) {
        m_boardListAdapter.notifyDataSetChanged();
        updatePageInfo();
    }

    @OnClick(R.id.wb_btn_newwb)
    public void onBtnNewWb() {
        int nres = FspManager.getInstance().getFspEngine().getFspBoard().createBlankWhiteBoard(FspManager.getInstance().generNewWhiteBoardName(),
                720, 1080, 2);
        Logger.d("Wb blank Create res:" + nres);
    }

    @OnClick(R.id.wb_layout_titlexpand)
    public void onBtnTitleLayoutExpand() {
        m_layoutOperatePanel.setVisibility(View.GONE);
        if (m_boardNameListView.getVisibility() == View.VISIBLE) {
            m_boardNameListView.setVisibility(View.GONE);
        } else {
            m_boardNameListView.setVisibility(View.VISIBLE);
        }
        m_boardListAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.wb_btn_more)
    public void onBtnTitleMore(){
        m_boardNameListView.setVisibility(View.GONE);
        if (m_layoutOperatePanel.getVisibility() == View.VISIBLE) {
            m_layoutOperatePanel.setVisibility(View.GONE);
        } else {
            m_layoutOperatePanel.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.wb_btn_prevpage)
    public void onBtnPrevPage(){
        int ncurpageid = FspManager.getInstance().getFspEngine().getFspBoard().getCurrentPageId(m_strCurBoardId);
        FspManager.getInstance().getFspEngine().getFspBoard().changeCurrentPage(m_strCurBoardId, ncurpageid-1);
        updatePageInfo();
    }

    @OnClick(R.id.wb_btn_nextpage)
    public void onBtnNextPage() {
        int ncurpageid = FspManager.getInstance().getFspEngine().getFspBoard().getCurrentPageId(m_strCurBoardId);
        FspManager.getInstance().getFspEngine().getFspBoard().changeCurrentPage(m_strCurBoardId, ncurpageid+1);
        updatePageInfo();
    }

    @OnClick(R.id.wb_btn_close)
    public void onBtnClose() {
        FspManager.getInstance().getFspEngine().getFspBoard().close(m_strCurBoardId);
        FspManager.getInstance().removeWb(m_strCurBoardId);

        Collection<WhiteBoardInfo> wbinfos = FspManager.getInstance().getWbInfos();
        if (wbinfos != null && wbinfos.size() > 0) {
            m_strCurBoardId = wbinfos.iterator().next().boardId;
            FspManager.getInstance().getFspEngine().getFspBoard().open(m_strCurBoardId, m_boardView);
        } else {
            m_strCurBoardId = null;
            finish();
        }
    }

    @OnClick(R.id.wb_btn_info)
    public void onBtnInfo() {

    }

    @OnClick(R.id.wb_btn_edit_start)
    public void onBtnEditStart() {
        UpdateEditBtns(false);
    }

    @OnClick(R.id.wb_btn_pen_width1)
    public void onBtnPenWidth1() {
        selectPenWidth(1);
        FspManager.getInstance().getFspEngine().getFspBoard().setLineWidth(m_strCurBoardId, 1);
    }

    @OnClick(R.id.wb_btn_pen_width2)
    public void onBtnPenWidth2() {
        selectPenWidth(2);
        FspManager.getInstance().getFspEngine().getFspBoard().setLineWidth(m_strCurBoardId, 4);
    }

    @OnClick(R.id.wb_btn_pen_width3)
    public void onBtnPenWidth3() {
        selectPenWidth(3);
        FspManager.getInstance().getFspEngine().getFspBoard().setLineWidth(m_strCurBoardId, 8);
    }

    @OnClick(R.id.wb_btn_pen_color1)
    public void onBtnPenColor1() {
        selectPenColor(1);
        FspManager.getInstance().getFspEngine().getFspBoard().setLineColor(m_strCurBoardId, 0xffff0000);
    }

    @OnClick(R.id.wb_btn_pen_color2)
    public void onBtnPenColor2() {
        selectPenColor(2);
        FspManager.getInstance().getFspEngine().getFspBoard().setLineColor(m_strCurBoardId, 0xff00ff00);
    }

    @OnClick(R.id.wb_btn_pen_color3)
    public void onBtnPenColor3() {
        selectPenColor(3);
        FspManager.getInstance().getFspEngine().getFspBoard().setLineColor(m_strCurBoardId, 0xff0000ff);
    }

    private void updatePageInfo() {
        int ncurpageid = FspManager.getInstance().getFspEngine().getFspBoard().getCurrentPageId(m_strCurBoardId);
        int npagecounts = FspManager.getInstance().getFspEngine().getFspBoard().getPageCount(m_strCurBoardId);
        String strTitle = String.format("%d/%d", ncurpageid+1, npagecounts);
        m_tvPages.setText(strTitle);
        m_tvTitle.setText(FspManager.getInstance().getWbName(m_strCurBoardId));
    }

    private void HideAllPanel() {
        m_layoutOperatePanel.setVisibility(View.GONE);
        UpdateEditBtns(true);
        m_boardNameListView.setVisibility(View.GONE);
    }

    private void selectPenWidth(int nIndex) {
        for (int i = 0; i < 3; i++) {
            if (nIndex - 1 == i) {
                m_btnPenWidths.get(i).setSelected(true);
            }else{
                m_btnPenWidths.get(i).setSelected(false);
            }
        }
    }

    private void selectPenColor(int nIndex) {
        for (int i = 0; i < 3; i++) {
            if (nIndex - 1 == i) {
                m_btnPenColors.get(i).setSelected(true);
            }else{
                m_btnPenColors.get(i).setSelected(false);
            }
        }
    }

    private void UpdateEditBtns(boolean isForceClose){
        if(isForceClose || m_ivBtnEditStart.isSelected()) {
            m_ivBtnEditStart.setSelected(false);
            m_ivBtnEditPenStyle.setVisibility(View.GONE);
            m_ivBtnPen.setVisibility(View.GONE);
            m_ivBtnRedo.setVisibility(View.GONE);
            m_ivBtnUndo.setVisibility(View.GONE);
            m_ivBtnDelete.setVisibility(View.GONE);
            m_layoutPenStyle.setVisibility(View.GONE);
        } else {
            m_ivBtnEditStart.setSelected(true);
            m_ivBtnEditPenStyle.setVisibility(View.VISIBLE);
            m_ivBtnPen.setVisibility(View.VISIBLE);
            m_ivBtnRedo.setVisibility(View.VISIBLE);
            m_ivBtnUndo.setVisibility(View.VISIBLE);
            m_ivBtnDelete.setVisibility(View.VISIBLE);
            m_layoutPenStyle.setVisibility(View.VISIBLE);
        }
    }

    private void changeToWb(String boardId) {
        m_boardNameListView.setVisibility(View.GONE);
        IFspWhiteBoard fspWb = FspManager.getInstance().getFspEngine().getFspBoard();
        if (m_strCurBoardId != null) {
            fspWb.close(m_strCurBoardId);
        }
        fspWb.open(boardId, m_boardView);
        m_strCurBoardId = boardId;
        fspWb.setCurProduceGraphType(m_strCurBoardId, IFspWhiteBoard.GRAPH_TYPE_LINE);
        fspWb.setCurOperateType(m_strCurBoardId, IFspWhiteBoard.GRAPH_TYPE_LINE);
        fspWb.setLineColor(m_strCurBoardId, 0xffff0000);
    }

    private class WhiteBoardListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public String m_boardId;
        public TextView m_tvBoardName;
        public ImageView m_ivSelect;

        public WhiteBoardListViewHolder(View view){
            super(view);
            view.setOnClickListener(this);
            m_tvBoardName = (TextView)view.findViewById(R.id.wb_list_tv_name);
            m_ivSelect = view.findViewById(R.id.sender_select_item_iv_select);
        }

        @Override
        public void onClick(View v) {
            changeToWb(m_boardId);
        }
    }

    private class WhiteBoardListAdapter extends RecyclerView.Adapter<WhiteBoardListViewHolder>{

        @Override
        public WhiteBoardListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_whiteboard_info,parent,false);
            return new WhiteBoardListViewHolder(v);
        }

        @Override
        public void onBindViewHolder(WhiteBoardListViewHolder holder, int position) {
            Collection<WhiteBoardInfo> wbinfos = FspManager.getInstance().getWbInfos();
            int i = 0;
            for (WhiteBoardInfo info : wbinfos) {
                if (i == position) {
                    holder.m_tvBoardName.setText(info.name);
                    holder.m_boardId = info.boardId;
                    if (info.boardId.equals(m_strCurBoardId)) {
                        holder.m_ivSelect.setVisibility(View.VISIBLE);
                    } else {
                        holder.m_ivSelect.setVisibility(View.GONE);
                    }
                }
                i++;
            }
        }

        @Override
        public int getItemCount() {
            return FspManager.getInstance().getWbInfosCount();
        }
    }

}
