package com.ccz.myvillage.activity.pager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.ccz.myvillage.R;
import com.ccz.myvillage.common.ws.IWsListener;
import com.ccz.myvillage.constants.ECmd;
import com.ccz.myvillage.dto.Category;
import com.ccz.myvillage.dto.TabMenuItem;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.java_websocket.client.WebSocketClient;

import java.io.IOException;

public abstract class DefaultPagerFragment extends android.support.v4.app.Fragment implements IWsListener {
    protected Category category;
    protected ListView lvBoardList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public void onOpen(WebSocketClient wsconn) {

    }

    @Override
    public void onClose(WebSocketClient wsconn, int i, String s, boolean b) {

    }

    @Override
    public void onError(WebSocketClient wsconn, Exception e) {

    }

    public void onMessage(WebSocketClient wsconn, String s) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNode = mapper.readTree(s);
            this.processMessage(wsconn, ECmd.getType(jsonNode.get("cmd").asText()), jsonNode, s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    abstract protected void processMessage(WebSocketClient wsconn, ECmd cmd, JsonNode jsonNode, String origMessage) throws IOException;
}
