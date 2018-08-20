package com.ccz.myvillage.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ccz.myvillage.ICache;
import com.ccz.myvillage.IConst;
import com.ccz.myvillage.R;
import com.ccz.myvillage.common.ImageUtils;
import com.ccz.myvillage.common.TimeUtils;
import com.ccz.myvillage.common.ws.WsMgr;
import com.ccz.myvillage.constants.EBoardPreference;
import com.ccz.myvillage.constants.ECmd;
import com.ccz.myvillage.controller.NetMessage;
import com.ccz.myvillage.dto.BoardFile;
import com.ccz.myvillage.dto.BoardItem;
import com.ccz.myvillage.dto.BoardLike;
import com.ccz.myvillage.dto.Category;
import com.ccz.myvillage.dto.ReplyItem;
import com.ccz.myvillage.dto.VoteItem;
import com.ccz.myvillage.form.response.ResBoardLike;
import com.ccz.myvillage.form.response.ResContent;
import com.ccz.myvillage.form.response.ResReplyList;
import com.ccz.myvillage.form.response.ResponseCmd;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.java_websocket.client.WebSocketClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ViewerActivity extends CommonActivity implements ViewTreeObserver.OnScrollChangedListener {

    private BoardItem boardItem;

    private ScrollView scrollView;
    private TextView tvAddReply;
    private EditText edtReply;

    private List<ReplyItem> replyItemList = new ArrayList<>();
    private Set<Long> replyIdSet = new HashSet<>();
    private  RadioGroup voteRadioGroup;
    private LinearLayout placeHolderReply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);

        boardItem = (BoardItem) getIntent().getSerializableExtra("item");
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(this);

        tvAddReply = (TextView)findViewById(R.id.tvAddReply);
        edtReply = (EditText)findViewById(R.id.edtReply);
        edtReply.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tvAddReply.setVisibility( charSequence.length()>0 ? View.VISIBLE : View.GONE);
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        placeHolderReply = (LinearLayout) findViewById(R.id.placeHolderReply);
        addPopupInViewer();

        WsMgr.getInst().setOnWsListener(this, this);
        reqBoardContent();
    }
    @Override
    public void onBackPressed() {
        finish();
    }
    ///for ScrollView
    @Override
    public void onScrollChanged() {
        if (scrollView != null) {
            if (scrollView.getChildAt(0).getBottom() <= (scrollView.getHeight() + scrollView.getScrollY())) {
                reqBoardReply(replyItemList.size()-1, IConst.MAX_BOARDITEM_COUNT);
            } else {
                //scroll view is not at bottom
            }
        }
    }

    @Override
    protected boolean processMessage(WebSocketClient wsconn, ECmd cmd, JsonNode jsonNode, String origMessage) throws IOException {
        if(super.processMessage(wsconn, cmd, jsonNode, origMessage) == true)
            return true;
        switch (cmd) {
            case getcontent:
                resBoardContent(origMessage);
                break;
            case replylist:
                resReplyList(origMessage);
                break;
            case addreply:
                resAddReply(origMessage);
                break;
            case delreply:
                resDelReply(jsonNode);
                break;
            case like: case dislike:
                resLikeDislike(origMessage);
                break;
            case delboard:
                resDelBoard(jsonNode);
                break;
        }
        return true;
    }

    private void reqBoardContent() {
        ObjectNode node = NetMessage.makeDefaultNode(ECmd.getcontent);
        node.put("boardid", boardItem.getBoardid());
        WsMgr.getInst().send(node.toString());
    }

    /*
    http://45.76.220.83:8081/thumb?fileid='file443a2c959a64439588eb220d17c6b443'&scode='apartment'
    http://45.76.220.83:8081/download?fileid='file443a2c959a64439588eb220d17c6b443'&scode='apartment'
    */
    private void resBoardContent(String origMessage) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ResContent res = mapper.readValue(origMessage, ResContent.class);
        if (res.getResult().equals("ok")) {
            ((TextView) findViewById(R.id.tvTitle)).setText(boardItem.getTitle());
            ((TextView) findViewById(R.id.tvId)).setText(boardItem.getCreateusername());
            ((TextView) findViewById(R.id.tvContent)).setText(res.getContent());
            ((TextView) findViewById(R.id.tvTime)).setText(TimeUtils.calcLastTime(boardItem.getCreatetime()));
            updateCount(boardItem.getLikes(), boardItem.getDislikes(), boardItem.getReply(), boardItem.getVisit());

            LinearLayout placeHolderFiles = (LinearLayout) findViewById(R.id.placeHolderFiles);
            for(BoardFile item : res.getFiles()) {
                View view = getLayoutInflater().inflate(R.layout.layout_viewer_image, null);
                ImageView ivThumbnail = (ImageView) view.findViewById(R.id.ivThumbnail);
                String urlStr = String.format("http://%s:8080/thumb?fileid=%s&scode=%s", item.getFileserver(), item.getFileid(), IConst.ServiceCode);
                new DownloadImageTask(ivThumbnail).execute(urlStr);
                ((TextView) view.findViewById(R.id.tvComment)).setText(item.getComment());
                placeHolderFiles.addView(view);
            }
            BoardLike like = res.getLike();
            if(like != null)
                updateLikeDislike(like.getPreference(), true);
            if(res.getVote()!=null && res.getVote().size()>0)
                updateVoteList(res.getVote());
        }

        reqBoardReply(0, IConst.MAX_BOARDITEM_COUNT);
    }

    private void updateLikeDislike(EBoardPreference type, boolean isadd) {
        ImageView ivLike = (ImageView)findViewById(R.id.ivLike);
        ImageView ivDislike = (ImageView)findViewById(R.id.ivDislike);
        ivLike.setSelected(false);
        ivDislike.setSelected(false);
        ivLike.setEnabled(true);
        ivDislike.setEnabled(true);
        if(type == EBoardPreference.like && isadd) {
            ivLike.setSelected(true);
            ivDislike.setEnabled(false);
        }else if(type == EBoardPreference.dislike && isadd){
            ivLike.setEnabled(false);
            ivDislike.setSelected(true);
        }
    }

    private void updateCount(int likes, int dislikes, int reply, int visit) {
        ((TextView) findViewById(R.id.tvLike)).setText(likes+"");
        ((TextView) findViewById(R.id.tvDislike)).setText(dislikes+"");
        ((TextView) findViewById(R.id.tvReply)).setText(reply+"");
        ((TextView) findViewById(R.id.tvVisit)).setText(visit+"");
    }

    private void updateVoteList(List<VoteItem> voteList) {
        View view = getLayoutInflater().inflate(R.layout.layout_board_vote, null);
        voteRadioGroup = view.findViewById(R.id.radioGroupHolder);
        voteRadioGroup.removeAllViews();
        for(VoteItem item : voteList) {
            View voteView = getLayoutInflater().inflate(R.layout.view_vote_viewer, null);
            RadioButton radioButton = ((RadioButton)voteView.findViewById(R.id.rbVoteItem));
            radioButton.setText(item.getVotetext());
            radioButton.setTag(item);
            voteRadioGroup.addView(voteView);
        }
        LinearLayout placeHolderFiles = (LinearLayout) findViewById(R.id.placeHolderFiles);
        placeHolderFiles.addView(view);
    }

    private void reqBoardReply(int offset, int count) {
        ObjectNode node = NetMessage.makeDefaultNode(ECmd.replylist);
        node.put("boardid", boardItem.getBoardid());
        node.put("offset", offset);
        node.put("count", count);
        WsMgr.getInst().send(node.toString());
    }

    private void resReplyList(String origMessage) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ResReplyList res = mapper.readValue(origMessage, ResReplyList.class);
        if (res.getResult().equals("ok")) {
            updateReplyList(res);
        }
    }

    private void updateReplyList(ResReplyList res) {
        for(ReplyItem item : res.getData()) {
            if(replyIdSet.contains(item.getReplyid()) == false) {
                replyIdSet.add(item.getReplyid());
                replyItemList.add(item);
                addReplyItemToLayout(item);
            }
        }
    }

    private void addReplyItemToLayout(ReplyItem item) {
        View replyItem = getLayoutInflater().inflate(R.layout.view_reply_item, null);
        ((TextView)replyItem.findViewById(R.id.tvId)).setText(item.username);
        ((TextView)replyItem.findViewById(R.id.tvReply)).setText(item.msg);
        ((TextView)replyItem.findViewById(R.id.tvTime)).setText(item.getReplyTimeStr());
        if(ICache.UserId.equals(item.getUserid()) == true) {
            ImageView ivMore = ((ImageView) replyItem.findViewById(R.id.ivMore));
            ivMore.setTag(item);
            replyItem.setTag(item);
            ivMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu p = new PopupMenu(getApplicationContext(), view);
                    getMenuInflater().inflate(R.menu.menu_delete_popup, p.getMenu());
                    p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.delete:// delete
                                    reqDelReply((ReplyItem) view.getTag());
                                    return true;
                            }
                            return false;
                        }
                    });
                    p.show();
                }
            });
            ivMore.setVisibility(View.VISIBLE);
        }
        placeHolderReply.addView(replyItem);
    }

    public void onClickAddReply(View view) {
        reqAddReply();
    }

    private void reqAddReply() {
        ObjectNode node = NetMessage.makeDefaultNode(ECmd.addreply);
        node.put("boardid", boardItem.getBoardid());
        node.put("parentrepid", 0); //reserved
        node.put("depth", 0); //reserved
        node.put("msg", edtReply.getText().toString());
        WsMgr.getInst().send(node.toString());
    }

    private void resAddReply(String origMessage) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ResReplyList res = mapper.readValue(origMessage, ResReplyList.class);
        if (res.getResult().equals("ok") == true) {
            edtReply.getText().clear();
            placeHolderReply.removeAllViews();
            replyItemList.clear();
            replyIdSet.clear();
            updateReplyList(res);
        }else
            Toast.makeText(this, getString(R.string.fail_add_reply), Toast.LENGTH_LONG).show();
    }

    private void reqDelReply(ReplyItem item) {
        ObjectNode node = NetMessage.makeDefaultNode(ECmd.delreply);
        node.put("boardid", boardItem.getBoardid());
        node.put("replyid", item.getReplyid()); //reserved
        WsMgr.getInst().send(node.toString());
    }

    private void resDelReply(JsonNode jsonNode) {
        if("ok".equals(jsonNode.get("result").asText()) == true) {
            String replyid = jsonNode.get("replyid").asText();
            for (int i = 0; i < placeHolderReply.getChildCount(); i++) {
                View view = placeHolderReply.getChildAt(i);
                ReplyItem item = (ReplyItem) view.getTag();
                if (replyid.equals(item.getReplyid()) == true) {
                    placeHolderReply.removeView(view);
                    return;
                }
            }
        }
        Toast.makeText(this, getString(R.string.fail_del_reply), Toast.LENGTH_SHORT).show();
    }


    private void resLikeDislike(String origMessage) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ResBoardLike res = mapper.readValue(origMessage, ResBoardLike.class);
        if (res.getResult().equals("ok") == true) {
            this.updateLikeDislike(res.getPreference(), res.isIsadd());
            updateCount(res.getLikes(), res.getDislikes(), res.getReply(), res.getVisit());
        }
    }

    private void reqDelBoard() {
        ObjectNode node = NetMessage.makeDefaultNode(ECmd.delboard);
        node.put("boardid", boardItem.getBoardid());
        WsMgr.getInst().send(node.toString());
    }

    private void resDelBoard(JsonNode jsonNode) {
        if("ok".equals(jsonNode.get("result").asText()) == true) {
            String boardid = jsonNode.get("boardid").asText();
            finish();
            return;
        }
        Toast.makeText(this, getString(R.string.fail_del_board), Toast.LENGTH_SHORT).show();
    }

    public void onClickAddLike(View view) {
        ImageView ivLike = (ImageView)findViewById(R.id.ivLike);

        ObjectNode node = NetMessage.makeDefaultNode(ECmd.like);
        node.put("boardid", boardItem.getBoardid());
        node.put("preference", "like"); //reserved
        node.put("isadd", !ivLike.isSelected());
        WsMgr.getInst().send(node.toString());
    }

    public void onClickAddDislike(View view) {
        ImageView ivDislike = (ImageView)findViewById(R.id.ivDislike);

        ObjectNode node = NetMessage.makeDefaultNode(ECmd.dislike);
        node.put("boardid", boardItem.getBoardid());
        node.put("preference", "dislike"); //reserved
        node.put("isadd", !ivDislike.isSelected());
        WsMgr.getInst().send(node.toString());
    }

    public void onClickShare(View view) {
    }

    public void onClickSelectVote(View view) {
        int selectedId = voteRadioGroup.getCheckedRadioButtonId();
        VoteItem voteItem = (VoteItem) findViewById(selectedId).getTag();
        ObjectNode node = NetMessage.makeDefaultNode(ECmd.selvote);
        node.put("boardid", boardItem.getBoardid());
        node.put("vitemid", voteItem.getVitemid()); //reserved
        node.put("isselect", true);
        WsMgr.getInst().send(node.toString());
    }

    public void onClickMore(View view) {
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView ivThumbnail;

        public DownloadImageTask(ImageView ivThumbnail) {
            this.ivThumbnail = ivThumbnail;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                Bitmap bmp = BitmapFactory.decodeStream(in);
                return ImageUtils.scaleUpContent(bmp, IConst.ScreenPixels.getWidth(), IConst.ScreenPixels.getHeight());
                //return bmp;
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }
            return null;
        }

        protected void onPostExecute(Bitmap result) {
            ivThumbnail.setImageBitmap(result);
        }
    }

    private void addPopupInViewer() {
        View vwMore = findViewById(R.id.layoutMore);
        vwMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu p = new PopupMenu(getApplicationContext(), view);
                if(ICache.UserId.equals(boardItem.getUserid()))
                    getMenuInflater().inflate(R.menu.menu_vote_popup, p.getMenu());
                else
                    getMenuInflater().inflate(R.menu.menu_report_popup, p.getMenu());
                p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delete:// delete
                                reqDelBoard();
                                return true;
                            case R.id.report:
                            case R.id.modify:
                                Toast.makeText(ViewerActivity.this, "Not Implemented NOW", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        return false;
                    }
                });
                p.show();
            }
        });

    }
}