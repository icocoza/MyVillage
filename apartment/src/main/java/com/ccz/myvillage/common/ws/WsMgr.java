package com.ccz.myvillage.common.ws;

import android.app.Activity;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by 1100177 on 2018. 2. 7..
 */

public class WsMgr {

    public static WsMgr s_pThis;
    public static WsMgr getInst() {return s_pThis = (s_pThis == null ? new WsMgr() : s_pThis);}
    public static void freeInst() {	s_pThis = null;	}

    private URI uri;
    private WebSocketClient webSocketClient;
    private IWsListener onWsListener;
    private Activity activity;

    private boolean hasSignIn = false;

    private int retryCount = 0, totalRetryCount = 0;

    public void connectServer(Activity activity, String urlWithPort, IWsListener onWsListener) throws URISyntaxException {
        retryCount = 0;
        try {
            if (webSocketClient != null && webSocketClient.isConnecting())
                webSocketClient.close();
        }catch(Exception e) {
        }

        this.activity = activity;
        this.onWsListener = onWsListener;
        uri = new URI(urlWithPort);
        connectServer();
    }

    public void reconnectServer() {
        connectServer();
        totalRetryCount++;
    }

    public void setOnWsListener(Activity activity, IWsListener onWsListener) {
        this.activity = activity;
        this.onWsListener = onWsListener;
    }

    public boolean reConnectServer() {
        if(uri == null)
            return false;
        try{
            if(webSocketClient!=null)
                webSocketClient.close();
        }catch(Exception e) {
            e.printStackTrace();
        }
        connectServer();
        return true;
    }

    public void disconnect() {
        try{
            if(uri!=null && webSocketClient!=null)
                webSocketClient.close();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        if(webSocketClient!=null)
            return webSocketClient.isOpen();
        return false;
    }

    public void send(String msg) {
        webSocketClient.send(msg);
    }

    public void send(byte[] data) {
        webSocketClient.send(data);
    }

    public void setHasSignIn(boolean hasSignIn) {
        this.hasSignIn = hasSignIn;
    }
    public boolean hasSignIn() {
        return hasSignIn;
    }

    private void connectServer() {
        webSocketClient = new WebSocketClient(uri) {

            @Override
            public void onOpen(ServerHandshake handshakedata) {
                retryCount = 0;
                WsMgr.this.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        WsMgr.this.onWsListener.onOpen(webSocketClient);
                    }
                });
            }

            @Override
            public void onMessage(final String s) {
                WsMgr.this.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("[PACKET]", s);
                        WsMgr.this.onWsListener.onMessage(webSocketClient, s);
                    }
                });
            }

            @Override
            public void onClose(final int i, final String s, final boolean b) {
                WsMgr.this.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        WsMgr.this.onWsListener.onClose(webSocketClient, i, s, b);
                    }
                });
            }

            @Override
            public void onError(final Exception e) {
                WsMgr.this.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        WsMgr.this.onWsListener.onError(webSocketClient, e);
                    }
                });
            }
        };
        webSocketClient.connect();
    }

}
