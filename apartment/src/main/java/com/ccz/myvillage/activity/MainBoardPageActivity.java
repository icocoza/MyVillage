package com.ccz.myvillage.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RadioButton;

import com.ccz.myvillage.IConst;
import com.ccz.myvillage.IRequestCode;
import com.ccz.myvillage.R;
import com.ccz.myvillage.activity.pager.NoticeBoardPagerFragment;
import com.ccz.myvillage.adapter.VillageBoardPagerAdapter;
import com.ccz.myvillage.adapter.VillageComponentAdapter;
import com.ccz.myvillage.common.ColorCollection;
import com.ccz.myvillage.common.VillageComponent;
import com.ccz.myvillage.common.ws.IWsListener;
import com.ccz.myvillage.common.ws.WsMgr;
import com.ccz.myvillage.constants.ECmd;
import com.ccz.myvillage.controller.NetMessage;
import com.ccz.myvillage.dto.Category;
import com.ccz.myvillage.dto.TabMenuItem;
import com.ccz.myvillage.form.response.ResBoardList;
import com.ccz.myvillage.form.response.ResCategories;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import org.java_websocket.client.WebSocketClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainBoardPageActivity extends CommonActivity implements DiscreteScrollView.OnItemChangedListener<VillageComponentAdapter.ViewHolder>,DiscreteScrollView.ScrollStateChangeListener<VillageComponentAdapter.ViewHolder>,IWsListener, ViewPager.OnPageChangeListener {

    private ArrayList<Category> categoryList;

    private DiscreteScrollView pickerMenu;
    private FrameLayout layoutTopMenu;
    private ViewPager viewPagerBoard;
    private VillageComponentAdapter.ViewHolder viewHolderPrev;
    private View[] radioButtons = new View[5];  //5 is bottom menu count

    private int lastRequestCategory = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setContentView(R.layout.activity_main_board_page);
        layoutTopMenu = (FrameLayout)findViewById(R.id.layoutTopMenu);
        pickerMenu = (DiscreteScrollView)findViewById(R.id.pickerMenu);

        WsMgr.getInst().setOnWsListener(this, this);
        reqCagetoryList();
        initRadioButton();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        NoticeBoardPagerFragment noticeBoardPagerFragment = (NoticeBoardPagerFragment) viewPagerBoard.getAdapter().instantiateItem(viewPagerBoard, viewPagerBoard.getCurrentItem());
        noticeBoardPagerFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCurrentItemChanged(@Nullable VillageComponentAdapter.ViewHolder viewHolder, int adapterPosition) {
        if(viewHolder!=null)
            viewHolder.showSelect();
        pickerMenu.setBackgroundColor(ColorCollection.get(adapterPosition));
        viewPagerBoard.setCurrentItem(adapterPosition, true);
        lastRequestCategory = adapterPosition;
    }

    @Override
    public void onScrollStart(@NonNull VillageComponentAdapter.ViewHolder currentItemHolder, int adapterPosition) {
        currentItemHolder.hideSelect();
    }

    @Override
    public void onScrollEnd(@NonNull VillageComponentAdapter.ViewHolder currentItemHolder, int adapterPosition) {
    }

    @Override
    public void onScroll(float scrollPosition, int currentPosition, int newPosition, @Nullable VillageComponentAdapter.ViewHolder currentHolder, @Nullable VillageComponentAdapter.ViewHolder newCurrent) {
        if(currentHolder!=null)
            currentHolder.hideSelect();
        if(newCurrent!=null)
            newCurrent.showSelect();
    }

    @Override
    public void onBackPressed() {
        if(viewPagerBoard==null) {
            super.onBackPressed();
            return;
        }
        if (viewPagerBoard.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            viewPagerBoard.setCurrentItem(viewPagerBoard.getCurrentItem() - 1);
        }
    }

    @Override
    protected boolean processMessage(WebSocketClient wsconn, ECmd cmd, JsonNode jsonNode, String origMessage) throws IOException {
        if(super.processMessage(wsconn, cmd, jsonNode, origMessage) == true)
            return true;
        switch (cmd) {
            case getcategorylist:
                resCategoryList(origMessage);
                break;
        }
        return true;
    }

    private void reqCagetoryList() {
        ObjectNode node = NetMessage.makeDefaultNode(ECmd.getcategorylist);
        WsMgr.getInst().send(node.toString());
    }

    private void resCategoryList(String origMessage) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ResCategories res = mapper.readValue(origMessage, ResCategories.class);
        categoryList = new ArrayList<>();
        categoryList.addAll(res.getCategories());
        if(categoryList.size() > 0) {
            updateCategoryListOnTab();
            viewPagerBoard = (ViewPager)findViewById(R.id.pagerBoard);
            viewPagerBoard.setAdapter(new VillageBoardPagerAdapter(getSupportFragmentManager(), categoryList));
            viewPagerBoard.addOnPageChangeListener(this);
            NoticeBoardPagerFragment noticeBoardPagerFragment = (NoticeBoardPagerFragment) viewPagerBoard.getAdapter().instantiateItem(viewPagerBoard, 0);
            noticeBoardPagerFragment.requestBoardList();
        }
    }

    private void updateCategoryListOnTab() {
        pickerMenu.setSlideOnFling(true);
        pickerMenu.setAdapter(new VillageComponentAdapter(categoryList));
        pickerMenu.addOnItemChangedListener(this);
        pickerMenu.addScrollStateChangeListener(this);
        pickerMenu.scrollToPosition(0);
        pickerMenu.setItemTransitionTimeMillis(250);
        pickerMenu.setItemTransformer(new ScaleTransformer.Builder().setMinScale(0.7f).build());
    }

    public void onClickHome(View view) {
        resetRadioButton(view);
    }

    public void onClickSearch(View view) {
        resetRadioButton(view);
        Intent in = new Intent(this, SearchActivity.class);
        in.putExtra("categories", categoryList);
        startActivityForResult(in, IRequestCode.REQUESTCODE_BOARD_SEARCH);
    }

    public void onClickAdd(View view) {
        resetRadioButton(view);
        Intent in = new Intent(this, WriteBoardActivity.class);
        in.putExtra("categories", categoryList);
        startActivityForResult(in, IRequestCode.REQUESTCODE_BOARD_WRITE);
    }

    public void onClickNotification(View view) {
        resetRadioButton(view);
    }

    public void onClickPerson(View view) {
        resetRadioButton(view);
        Intent in = new Intent(this, PreferenceActivity.class);
        startActivityForResult(in, IRequestCode.REQUESTCODE_PREFERENCE);
    }

    /////// Start for ViewPager
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Log.i("[onPageSelected]", position + "");
        NoticeBoardPagerFragment noticeBoardPagerFragment = (NoticeBoardPagerFragment) viewPagerBoard.getAdapter().instantiateItem(viewPagerBoard, position);
        noticeBoardPagerFragment.requestBoardList();

        pickerMenu.smoothScrollToPosition(position);
        layoutTopMenu.setBackgroundColor(ColorCollection.get(position));
        pickerMenu.setBackgroundColor(ColorCollection.get(position));
        if(viewHolderPrev!=null)
            viewHolderPrev.hideSelect();
        viewHolderPrev = (VillageComponentAdapter.ViewHolder)pickerMenu.getViewHolder(position);
        viewHolderPrev.showSelect();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        Log.i("[onPageScrollStateChanged]", state + "");
    }
    ////// End of ViewPager

    private void initRadioButton() {    //이런짓 하면 안되는데.. RadioButton이 동작하지 않아 수동으로..
        radioButtons[0] = findViewById(R.id.rbHome);
        radioButtons[1] = findViewById(R.id.rbSearch);
        radioButtons[2] = findViewById(R.id.rbAdd);
        radioButtons[3] = findViewById(R.id.rbNoti);
        radioButtons[4] = findViewById(R.id.rbPerson);
    }

    private void resetRadioButton(View view) {
        for(int i=0; i<radioButtons.length; i++)
            if(radioButtons[i] != view)
                ((RadioButton)radioButtons[i]).setChecked(false);
    }
}
