package com.ccz.myvillage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TabMenuItem {
    @JsonProperty
    private final String buildId;
    @JsonProperty
    private final String buildName;

    public TabMenuItem(String buildId, String buildName) {
        this.buildId = buildId;
        this.buildName = buildName;
    }

    @JsonProperty
    public String getBuildId() {
        return this.buildId;
    }
    
    @JsonProperty
    public String getBuildName() {
        return this.buildName;
    }
}
