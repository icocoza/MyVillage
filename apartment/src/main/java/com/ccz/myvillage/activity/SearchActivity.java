package com.ccz.myvillage.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ccz.myvillage.IConst;
import com.ccz.myvillage.IRequestCode;
import com.ccz.myvillage.R;
import com.ccz.myvillage.activity.dialog.AlertManager;
import com.ccz.myvillage.activity.dialog.IDialogListResultListener;
import com.ccz.myvillage.adapter.BoardItemListAdapter;
import com.ccz.myvillage.common.db.DbHelper;
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
import java.util.stream.Collectors;

public class SearchActivity extends CommonActivity implements AbsListView.OnScrollListener, AdapterView.OnItemClickListener {

    ListView lvBoardList;

    private ArrayList<Category> categoryList;
    private int selectedCategoryIndex = -1;

    private List<BoardItem> boardItemList = new ArrayList<>();
    private Set<String> boardIdSet = new HashSet<>();
    private BoardItemListAdapter boardItemListAdapter;

    private boolean scrollDown = false;
    private boolean scrollingLock = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        categoryList = (ArrayList<Category>) getIntent().getSerializableExtra("categories");
        WsMgr.getInst().setOnWsListener(this, this);

        EditText edtSearch = (EditText) findViewById(R.id.edtSearch);
        edtSearch.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        doSearch(edtSearch.getText().toString());
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });

        initSeachHistoryFromDB();

        lvBoardList = (ListView)findViewById(R.id.lvSearchResult);
        lvBoardList.setAdapter(boardItemListAdapter = new BoardItemListAdapter(this, 0, 0, boardItemList));
        lvBoardList.setOnScrollListener(this);
        lvBoardList.setOnItemClickListener(this);

    }

    public void onClickCancel(View view) {
        finish();
    }

    public void onClickCategory(View view) {
    }

    @Override
    protected boolean processMessage(WebSocketClient wsconn, ECmd cmd, JsonNode jsonNode, String origMessage) throws IOException {
        if (super.processMessage(wsconn, cmd, jsonNode, origMessage) == true)
            return true;
        switch(cmd) {
            case boardsearch:
                resBoardSearch(origMessage);
                break;
        }
        return false;
    }

    private void initSeachHistoryFromDB() {

    }

    private void doSearch(final String searchWord) {
        if(searchWord.length()<2) {
            Toast.makeText(this, getString(R.string.search_2_more), Toast.LENGTH_SHORT).show();
            return;
        }
        DbHelper.getInst().insertSearchWord(searchWord, "");
        List<String> categories = categoryList.stream().map(x -> x.getTitle()).collect(Collectors.toList());
        AlertManager.showList(this, getString(R.string.search_select_area), categories, new IDialogListResultListener() {
            @Override
            public void onDialogResult(boolean yesOrNo, int type) {
                if(yesOrNo == true & selectedCategoryIndex>-1) {
                    TextView tvCategory = (TextView)findViewById(R.id.tvCategory);
                    tvCategory.setText(categoryList.get(selectedCategoryIndex).getTitle());
                    reqBoardSearch(searchWord, 0);
                }
            }

            @Override
            public void onItemSelected(int which) {
                selectedCategoryIndex = which;
            }
        });
    }

    private void reqBoardSearch(String searchWord, int offset) {
        ObjectNode node = NetMessage.makeDefaultNode(ECmd.boardsearch);
        node.put("search", searchWord);
        node.put("categoryindex", selectedCategoryIndex);
        node.put("offset", offset);
        WsMgr.getInst().send(node.toString());
    }

    private void resBoardSearch(String origMessage) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ResBoardList res = mapper.readValue(origMessage, ResBoardList.class);
        if(res.getResult().equals("ok")) {
            if(scrollDown==true)
                addItemToBottomOfList(res);
            else
                addItemToTopOfList(res);
            scrollingLock = false;
        }else if(res.getResult().equals("NoListData")) {
            Toast.makeText(this, getString(R.string.end_of_list), Toast.LENGTH_LONG).show();

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
        Intent in = new Intent(this, ViewerActivity.class);
        in.putExtra("item", boardItem);
        in.putExtra("category", categoryList.get(this.selectedCategoryIndex));
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
        scrollDown = false;
        reqBoardSearch(((EditText)findViewById(R.id.edtSearch)).getText().toString(), 0);
    }

    private void requestBoardListDown() {
        scrollDown = true;
        reqBoardSearch(((EditText)findViewById(R.id.edtSearch)).getText().toString(), boardItemList.size());
    }
}