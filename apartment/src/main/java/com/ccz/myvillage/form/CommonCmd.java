package com.ccz.myvillage.form;

import com.ccz.myvillage.IConst;
import com.ccz.myvillage.constants.ECmd;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Random;

/**
 * Created by 1100177 on 2018. 7. 11..
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommonCmd implements Serializable {
    @JsonProperty
    protected String action;
    @JsonProperty
    protected String scode;
    @JsonProperty
    protected String rcode;
    @JsonProperty
    protected ECmd cmd;

    public CommonCmd() {
        action = IConst.ServerAction;
        scode = IConst.ServiceCode;
        rcode = new Random().nextInt(9000)+999 + "";
    }
    public CommonCmd(ECmd cmd) {
        this.cmd = cmd;
        action = IConst.ServerAction;
        scode = IConst.ServiceCode;
        rcode = new Random().nextInt(9000)+999 + "";
    }
    @JsonProperty
    public String getScode() {
        return scode;
    }

    @JsonProperty
    public String getRcode() {
        return rcode;
    }

    @JsonProperty
    public ECmd getCmd() {
        return cmd;
    }

    @JsonProperty
    public void setCmd(ECmd cmd) {
        this.cmd = cmd;
    }
}
