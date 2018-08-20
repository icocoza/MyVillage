package com.ccz.myvillage.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ccz.myvillage.ICache;
import com.ccz.myvillage.IConst;
import com.ccz.myvillage.R;
import com.ccz.myvillage.activity.dialog.AlertManager;
import com.ccz.myvillage.activity.dialog.IDialogResultListener;
import com.ccz.myvillage.common.Preferences;
import com.ccz.myvillage.common.SysUtils;
import com.ccz.myvillage.common.ws.IWsListener;
import com.ccz.myvillage.common.ws.WsMgr;
import com.ccz.myvillage.constants.ECmd;
import com.ccz.myvillage.controller.NetMessage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.java_websocket.client.WebSocketClient;

import java.io.IOException;
import java.net.URISyntaxException;

public class MainActivity extends CommonActivity implements IWsListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            WsMgr.getInst().connectServer(this, IConst.host, this);
        } catch (URISyntaxException e) {
            AlertManager.showYes(this, null, getString(R.string.net_error), new IDialogResultListener() {
                @Override
                public void onDialogResult(boolean yesOrNo, int type) {
                    finish();
                }
            });
        }

        IConst.ScreenPixels = SysUtils.getScreenPixels(this, 0.90f);
        IConst.ScreenDp = SysUtils.getScreenDp(this, 0.90f);
    }

    @Override
    public void onOpen(WebSocketClient wsconn) {
        if(super.sendSignin() == false) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Intent in = new Intent(MainActivity.this, ChooseAptActivity.class);
                    startActivity(in);
                    finish();
                }
            }, 1000);
        }
    }


    @Override
    public void onClose(WebSocketClient wsconn, int i, String s, boolean b) {
        //이 화면에서는 연결이 끊어지더라도 재접속을 시키지 않아야 함.
    }

    @Override
    public void onError(WebSocketClient wsconn, Exception e) {
        AlertManager.showYes(this, null, getString(R.string.net_connect_failed), new IDialogResultListener() {
            @Override
            public void onDialogResult(boolean yesOrNo, int type) {
                finish();
            }
        });
    }

    @Override
    protected boolean processMessage(WebSocketClient wsconn, ECmd cmd, JsonNode jsonNode, String origMessage) throws IOException {
        if(ECmd.signin == cmd) {
            if(jsonNode.get("result").asText().equals("ok")) {
                ICache.UserId = jsonNode.get("userid").asText();
                Preferences.set(this, Preferences.USERNAME, jsonNode.get("username").asText());
                WsMgr.getInst().setHasSignIn(true);
                Intent intent = new Intent(this, MainBoardPageActivity.class);
                startActivity(intent);
            }else {
                WsMgr.getInst().setHasSignIn(false);
                AlertManager.showYesNo(this, null, getString(R.string.signin_error), new IDialogResultListener() {
                    @Override
                    public void onDialogResult(boolean yesOrNo, int type) {
                        if(yesOrNo == true) {
                            Intent in = new Intent(MainActivity.this, ChooseAptActivity.class);
                            startActivity(in);
                            finish();
                        }else
                            finish();
                    }
                });
            }
            return true;
        }
        return super.processMessage(wsconn, cmd, jsonNode, origMessage);
    }

}
