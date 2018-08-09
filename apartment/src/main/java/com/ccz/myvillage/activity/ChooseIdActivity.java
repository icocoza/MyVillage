package com.ccz.myvillage.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ccz.myvillage.BuildConfig;
import com.ccz.myvillage.R;
import com.ccz.myvillage.activity.dialog.AlertManager;
import com.ccz.myvillage.common.Preferences;
import com.ccz.myvillage.common.SysUtils;
import com.ccz.myvillage.common.ws.IWsListener;
import com.ccz.myvillage.common.ws.WsMgr;
import com.ccz.myvillage.constants.ECmd;
import com.ccz.myvillage.controller.NetMessage;
import com.ccz.myvillage.dto.BuildingInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.java_websocket.client.WebSocketClient;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ChooseIdActivity extends CommonActivity implements IWsListener, TextWatcher {
    private BuildingInfo buildingInfo;
    private boolean enabledUid = false;
    private String uid = "";
    private Set<String> userIdSet = new HashSet<>();
    private String uuid = null;

    private EditText edtUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_id);
        WsMgr.getInst().setOnWsListener(this, this);

        buildingInfo = (BuildingInfo) this.getIntent().getSerializableExtra("buildingInfo");
        if(buildingInfo!=null) {
            ((TextView)findViewById(R.id.tvName)).setText(buildingInfo.getBuildName());
        }

        edtUid = (EditText)findViewById(R.id.edtUid);
        edtUid.addTextChangedListener(this);
    }

    public void onClickNext(View view) {
        ObjectNode node = NetMessage.makeDefaultNode(ECmd.find_id);
        node.put("uid", uid);
        WsMgr.getInst().send(node.toString());
    }

    public void onClickBack(View view) {
        Intent intent = new Intent(this, ChooseAptActivity.class);
        intent.putExtra("buildingInfo", buildingInfo);
        startActivity(intent);
        finish();
    }

    public void onClickRegister(View view) {
        Intent in = new Intent(this, ChooseAptActivity.class);
        startActivity(in);
        finish();
    }

    @Override
    public void onOpen(WebSocketClient wsconn) {

    }

    @Override
    public void onClose(WebSocketClient wsconn, int i, String s, boolean b) {
        WsMgr.getInst().reConnectServer();
    }

    @Override
    public void onError(WebSocketClient wsconn, Exception e) {

    }

    @Override
    protected boolean processMessage(WebSocketClient wsconn, ECmd cmd, JsonNode jsonNode, String origMessage) throws IOException {
        String result = jsonNode.get("result").asText();
        if(result == null)
            return false;
        if(ECmd.find_id == cmd) {
            if(result.equals("ok") == true) {
                sendAnonyLogin();
            }else if(result.equals("already_exist_userid")) {
                userIdSet.add(uid);
                enabledUid = false;
                setComment(getString(R.string.already_exist_userid), enabledUid);
                setButtonStatus(enabledUid);
            }
        }else if(ECmd.anony_login_buildid == cmd) {
            if(result.equals("ok") == true) {
                loginOk(jsonNode);
            }else {
                AlertManager.showYes(this, null, getString(R.string.login_failed), null);
                //finish();
            }
        }else if(ECmd.signin == cmd) {
            if(result.equals("ok") == true) {
                Intent intent = new Intent(this, MainBoardPageActivity.class);
                startActivity(intent);
                finish();
            }else {
                AlertManager.showYes(this, null, getString(R.string.signin_failed), null);
                ((TextView)findViewById(R.id.tvRegister)).setVisibility(View.VISIBLE);
            }
        }
        return true;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        enabledUid = false;

        uid = edtUid.getText().toString();
        if(uid==null || uid.length()<1) {
            setComment(getString(R.string.input_username), enabledUid);
        }else if(uid.contains("'") || uid.contains("\"")) {
            setComment(getString(R.string.input_username_wrongtext), enabledUid);
        }else if(uid.getBytes().length<10) {
            setComment(getString(R.string.input_username_short), enabledUid);
        }else if(userIdSet.contains(uid) == true)
            setComment(getString(R.string.already_exist_userid), enabledUid);
        else {
            enabledUid = true;
            setComment(getString(R.string.input_right), enabledUid);
        }
        setButtonStatus(enabledUid);

    }

    @Override
    public void afterTextChanged(Editable editable) {
    }

    private void setButtonStatus(boolean enabled) {
        Button btn = (Button)findViewById(R.id.btnNext);
        if(enabledUid == true) {
            btn.setEnabled(true);
            btn.setBackgroundColor(Color.RED);
        }else {
            btn.setEnabled(false);
            btn.setBackgroundColor(Color.GRAY);
        }
    }

    private void setComment(String comment, boolean valid) {
        TextView tvComment = (TextView)findViewById(R.id.tvComment);
        tvComment.setText(comment);
        tvComment.setTextColor(valid ? Color.BLUE : Color.RED);
    }

    private void sendAnonyLogin() {
        this.uuid = SysUtils.getUuid();
        ObjectNode node = NetMessage.makeDefaultNode(ECmd.anony_login_buildid);
        node.put("buildid", buildingInfo.getBuildId());
        node.put("uuid", uuid);
        node.put("uid", uid);
        node.put("ostype", "AOS");
        node.put("osversion", String.format("%s(%s)", Build.VERSION.RELEASE, Build.VERSION.SDK_INT));
        node.put("appversion", BuildConfig.VERSION_NAME);
        node.put("usertype", "user");
        node.put("epid", Preferences.get(this, "epid"));

        WsMgr.getInst().send(node.toString());
    }

    /*private void sendSignin(String tokenid, String token) {
        ObjectNode node = NetMessage.makeDefaultNode(ECmd.signin);
        node.put("apptoken", IConst.apptoken);
        node.put("tokenid", tokenid);
        node.put("regtoken", token);
        node.put("uuid", uuid);
        WsMgr.getInst().send(node.toString());
    }*/

    private void loginOk(JsonNode jsonNode) {
        String tokenid = jsonNode.get("tid").asText();
        String token  = jsonNode.get("token").asText();

        Preferences.set(this, "tokenid", tokenid);
        Preferences.set(this, "token", token);
        Preferences.set(this, "uuid", uuid);

        sendSignin(tokenid, token);
    }
}
