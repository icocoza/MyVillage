package com.ccz.myvillage.common.ws;

import org.java_websocket.client.WebSocketClient;

/**
 * Created by 1100177 on 2018. 2. 7..
 */

public interface IWsListener {
    void onOpen(WebSocketClient wsconn);
    void onMessage(WebSocketClient wsconn, String s);
    void onClose(WebSocketClient wsconn, int i, String s, boolean b);
    void onError(WebSocketClient wsconn, Exception e);
}
