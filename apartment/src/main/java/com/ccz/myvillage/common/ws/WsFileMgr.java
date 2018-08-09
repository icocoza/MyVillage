package com.ccz.myvillage.common.ws;

import android.app.Activity;

import com.ccz.myvillage.constants.ECmd;
import com.ccz.myvillage.controller.NetMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.stream.Stream;

public class WsFileMgr {
    private URI uri;
    private WebSocketClient webSocketClient;
    private IWsFileListener onWsFileListener;
    private Activity activity;

    private int currentId = -1;

    public void connectServer(Activity activity, String urlWithPort, IWsFileListener onWsFileListener) throws URISyntaxException {
        try {
            if (webSocketClient != null && webSocketClient.isConnecting())
                webSocketClient.close();
        }catch(Exception e) {
        }

        this.activity = activity;
        this.onWsFileListener = onWsFileListener;
        uri = new URI(urlWithPort);
        connectServer();
    }

    private void connectServer() {
        webSocketClient = new WebSocketClient(uri) {

            @Override
            public void onOpen(ServerHandshake handshakedata) {
                WsFileMgr.this.activity.runOnUiThread(() -> WsFileMgr.this.onWsFileListener.onOpen(webSocketClient));
            }

            @Override
            public void onMessage(final String s) {
                WsFileMgr.this.activity.runOnUiThread(() -> WsFileMgr.this.onWsFileListener.onMessage(webSocketClient, s));
            }

            @Override
            public void onClose(final int i, final String s, final boolean b) {
                WsFileMgr.this.activity.runOnUiThread(() -> WsFileMgr.this.onWsFileListener.onClose(webSocketClient, i, s, b));
            }

            @Override
            public void onError(final Exception e) {
                WsFileMgr.this.activity.runOnUiThread(() -> WsFileMgr.this.onWsFileListener.onError(webSocketClient, e));
            }
        };
        webSocketClient.connect();
    }

    public void sendFileStart(int id, String fileId) {
        currentId = id;
        ObjectNode node = NetMessage.makeDefaultNode(ECmd.filesstart);
        node.put("fileid", fileId);
        webSocketClient.send(node.toString());
    }

    public void sendFile(final byte[] fileData) {
        final int totalSize = fileData.length;
        WsFileMgr.this.activity.runOnUiThread(() -> {
            ByteArrayInputStream bis = new ByteArrayInputStream(fileData);
            byte[] buffer = new byte[8192];
            int read = 0, sent=0;
            try {
                while ((read = bis.read(buffer)) > 0) {
                    if (read < 8192) { //size를 설정할수 없음..
                        byte[] lastbuffer = new byte[read];
                        System.arraycopy(buffer, 0, lastbuffer, 0, read);
                        webSocketClient.send(lastbuffer);
                        break;
                    }
                    webSocketClient.send(buffer);
                    this.onWsFileListener.onFileSent(webSocketClient, currentId, (sent += read), totalSize);
                }
                this.onWsFileListener.onFileSent(webSocketClient, currentId, totalSize, totalSize);
                this.onWsFileListener.onFileComplete(webSocketClient, currentId);
            }catch(IOException e) {
            }
        });

    }
}
