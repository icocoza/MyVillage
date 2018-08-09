package com.ccz.myvillage.common.ws;

import org.java_websocket.client.WebSocketClient;

public interface IWsFileListener extends IWsListener{
    void onFileSent(WebSocketClient wsconn, int id, int size,  int totalSize);
    void onFileComplete(WebSocketClient wsconn, int id);
}
