package com.ccz.myvillage.controller;

import android.support.annotation.NonNull;

import com.ccz.myvillage.IConst;
import com.ccz.myvillage.constants.ECmd;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class NetMessage {

    private static int rcode = 1;

    public static ObjectNode makeDefaultNode(@NonNull ECmd cmd) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objNode = mapper.createObjectNode();
        objNode.put("action", IConst.ServerAction);
        objNode.put("scode", IConst.ServiceCode);
        objNode.put("rcode", ++rcode + "");
        objNode.put("cmd", cmd.getValue());
        return objNode;
    }

}
