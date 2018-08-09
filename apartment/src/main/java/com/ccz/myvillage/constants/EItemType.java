package com.ccz.myvillage.constants;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum EItemType {
    none("none"), text("text"), vote("vote"), image("image"), audio("audio"), video("video"),
    link("link"), textimage("textimage"), textaudio("textaudio"), textvideo("textvideo"),
    imageaudio("imageaudio"), imagevideo("imagevideo"), multimedia("multimedia"), richtext("richtext");


    public final String value;

    private EItemType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static final Map<String, EItemType> StrToAptCmdMap;

    static {
        StrToAptCmdMap = new ConcurrentHashMap<>();
        for(EItemType cmd : EItemType.values())
            StrToAptCmdMap.put(cmd.getValue(), cmd);
    }

    static public EItemType getType(String cmd) {
        EItemType ecmd = StrToAptCmdMap.get(cmd);
        if(ecmd != null)
            return ecmd;
        return none;
    }
}
