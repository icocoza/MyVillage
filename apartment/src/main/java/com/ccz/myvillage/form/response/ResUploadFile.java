package com.ccz.myvillage.form.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResUploadFile extends ResponseCmd {
    @JsonProperty
    private String hostip;
    @JsonProperty
    private int hostport;
    @JsonProperty
    private String fileid;

    @JsonProperty
    public String getHostip() {
        return hostip;
    }
    @JsonProperty
    public int getHostport() {
        return hostport;
    }
    @JsonProperty
    public String getFileid() {
        return fileid;
    }
}
