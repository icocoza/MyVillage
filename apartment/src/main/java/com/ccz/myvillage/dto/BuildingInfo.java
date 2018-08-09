package com.ccz.myvillage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by 1100177 on 2018. 7. 11..
 */

public class BuildingInfo implements Serializable {
    @JsonProperty
    private String buildId;
    @JsonProperty
    private String buildName;

    @JsonProperty
    public String getBuildId() {
        return buildId;
    }

    @JsonProperty
    public void setBuildId(String buildId) {
        this.buildId = buildId;
    }

    @JsonProperty
    public String getBuildName() {
        return buildName;
    }

    @JsonProperty
    public void setBuildName(String buildName) {
        this.buildName = buildName;
    }
}
