package com.ccz.myvillage.activity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.ccz.myvillage.IConst;
import com.ccz.myvillage.common.Preferences;
import com.ccz.myvillage.common.ws.IWsListener;
import com.ccz.myvillage.common.ws.WsMgr;
import com.ccz.myvillage.constants.ECmd;
import com.ccz.myvillage.controller.NetMessage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.java_websocket.client.WebSocketClient;

import java.io.IOException;


public abstract class CommonActivity  extends AppCompatActivity implements IWsListener{

    protected boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    protected void onBackPressedTwice() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finish();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 1500);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
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

    @Override
    public void onOpen(WebSocketClient wsconn) {
        Log.i("[NETWORK]", "OPENED!!");
        if(WsMgr.getInst().hasSignIn()) //이미 SignIn된 상태에서 연결이 끊어졌다가 다시 붙은 경우, 자동 SignIn
            sendSignin();
    }

    @Override
    public void onError(WebSocketClient wsconn, Exception e) {
        //if(enabledNetworkStatus ==true)
        //    WsMgr.getInst().reConnectServer();
    }

    @Override
    public void onClose(WebSocketClient wsconn, int i, String s, boolean b) {
        Log.i("[NETWORK]", "CLOSED!!");
        WsMgr.getInst().reConnectServer();
    }

    protected boolean processMessage(WebSocketClient wsconn, ECmd cmd, JsonNode jsonNode, String origMessage) throws IOException {
        return false;
    }

    protected boolean sendSignin() {
        String tokenid = Preferences.get(this, Preferences.TOKENID);
        String token = Preferences.get(this, Preferences.TOEKN);
        if (tokenid != null && token != null) {
            sendSignin(tokenid, token);
            return true;
        }
        return false;
    }

    protected void sendSignin(String tokenid, String token) {
        ObjectNode node = NetMessage.makeDefaultNode(ECmd.signin);
        node.put("apptoken", IConst.apptoken);
        node.put("tokenid", tokenid);
        node.put("regtoken", token);
        node.put("uuid", Preferences.get( this,Preferences.UUID));
        WsMgr.getInst().send(node.toString());
    }

}
