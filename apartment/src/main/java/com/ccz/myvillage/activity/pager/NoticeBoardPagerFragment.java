package com.ccz.myvillage.activity.pager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.ccz.myvillage.IConst;
import com.ccz.myvillage.IRequestCode;
import com.ccz.myvillage.R;
import com.ccz.myvillage.activity.ViewerActivity;
import com.ccz.myvillage.adapter.BoardItemListAdapter;
import com.ccz.myvillage.common.ws.WsMgr;
import com.ccz.myvillage.constants.ECmd;
import com.ccz.myvillage.controller.NetMessage;
import com.ccz.myvillage.dto.BoardItem;
import com.ccz.myvillage.dto.Category;
import com.ccz.myvillage.form.response.ResBoardList;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.java_websocket.client.WebSocketClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NoticeBoardPagerFragment extends DefaultPagerFragment implements AbsListView.OnScrollListener, AdapterView.OnItemClickListener {

    ListView lvBoardList;

    private Category category;
    private List<BoardItem> boardItemList = new ArrayList<>();
    private Set<String> boardIdSet = new HashSet<>();
    private BoardItemListAdapter boardItemListAdapter;

    private boolean scrollDown = false;
    private boolean scrollingLock = false;

    public NoticeBoardPagerFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        category = (Category) getArguments().getSerializable("category");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_board_listview, container, false);
        lvBoardList = (ListView) linearLayout.findViewById(R.id.lvBoardList);
        lvBoardList.setAdapter(boardItemListAdapter = new BoardItemListAdapter(getContext(), 0, 0, boardItemList));
        lvBoardList.setOnScrollListener(this);
        lvBoardList.setOnItemClickListener(this);
        return linearLayout;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(IRequestCode.REQUESTCODE_BOARD_WRITE == requestCode && resultCode == 1) {
            requestBoardListUp();
        }else if(IRequestCode.REQUESTCODE_PREFERENCE == requestCode && resultCode == 1) {
            lvBoardList.setAdapter(boardItemListAdapter = new BoardItemListAdapter(getContext(), 0, 0, boardItemList));
        }else if(IRequestCode.REQUESTCODE_BOARD_VIEWER == requestCode && resultCode == IRequestCode.RESULTCODE_BOARDID) {
            BoardItem boardItem = (BoardItem) data.getSerializableExtra("item");
            if(boardItem != null) {
                boardItemList.remove(boardItem);
                boardItemListAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void processMessage(WebSocketClient wsconn, ECmd cmd, JsonNode jsonNode, String origMessage) throws IOException {
        switch (cmd) {
            case boardlist:
                resBoardList(origMessage);
                break;
        }

    }

    public void requestBoardList() {
        if(boardItemList.size()==0) {   //first
            requestBoardListDown();
        }
    }

    private void reqBoardItemList(int category, int offset, int count) {
        ObjectNode node = NetMessage.makeDefaultNode(ECmd.boardlist);
        node.put("category", category);
        node.put("offset", offset);
        node.put("count", count);
        WsMgr.getInst().send(node.toString());
    }

    //코드가 복잡할 수 있으니 추후 정리 한번 하자.
    private void resBoardList(String origMessage) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ResBoardList res = mapper.readValue(origMessage, ResBoardList.class);
        if(res.getResult().equals("ok")) {
            if(scrollDown==true)
                addItemToBottomOfList(res);
            else
                addItemToTopOfList(res);
            scrollingLock = false;
        }else if(res.getResult().equals("NoListData")) {
            Toast.makeText(this.getActivity(), getString(R.string.end_of_list), Toast.LENGTH_LONG).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    scrollingLock = false;
                }
                }, 10000);

        }
    }

    private void addItemToBottomOfList(ResBoardList res) {
        List<BoardItem> itemList = res.getData();
        itemList.stream().filter(x -> boardIdSet.contains(x.getBoardid()) ).forEach(y -> {
            boardItemList.add(y);
            boardIdSet.add(y.getBoardid());
        });
        boardItemListAdapter.notifyDataSetChanged();
    }

    private void addItemToTopOfList(ResBoardList res) {
        List<BoardItem> itemList = res.getData();
        for(int i=itemList.size()-1; i>=0; i--) {
            BoardItem item = itemList.get(i);
            if(boardIdSet.contains(item.getBoardid()) == false) {
                boardItemList.add(0, item);
                boardIdSet.add(item.getBoardid());
            }
        }
        boardItemListAdapter.notifyDataSetChanged();
    }

    //////////////////////////////////
    //// Start the ListView Events ///
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        BoardItem boardItem = (BoardItem) view.getTag();
        Intent in = new Intent(this.getActivity(), ViewerActivity.class);
        in.putExtra("item", boardItem);
        in.putExtra("category", category);
        startActivityForResult(in, IRequestCode.REQUESTCODE_BOARD_VIEWER);
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        if(scrollingLock == true)
            return;
        if (lvBoardList.getLastVisiblePosition() == lvBoardList.getAdapter().getCount() -1 &&
                lvBoardList.getChildAt(lvBoardList.getChildCount() - 1).getBottom() <= lvBoardList.getHeight()) {
            scrollingLock = true;
            requestBoardListDown();
        }
        else if (lvBoardList.getFirstVisiblePosition() == 0 && lvBoardList.getChildAt(0).getTop() >= 0) {
            scrollingLock = true;
            requestBoardListUp();
        }

        if(lvBoardList.canScrollVertically(-1) == false)
            ;//top
        else if(lvBoardList.canScrollVertically(1) == false)
            ;//bottom
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {

    }
    //// End the ListView Events /////
    //////////////////////////////////

    private void requestBoardListUp() {
        WsMgr.getInst().setOnWsListener(this.getActivity(), this);
        scrollDown = false;
        reqBoardItemList(category.getCategory(), 0, IConst.MAX_BOARDITEM_COUNT/2);
    }

    private void requestBoardListDown() {
        WsMgr.getInst().setOnWsListener(this.getActivity(), this);
        scrollDown = true;
        reqBoardItemList(category.getCategory(), boardItemList.size(), IConst.MAX_BOARDITEM_COUNT);
    }

}
