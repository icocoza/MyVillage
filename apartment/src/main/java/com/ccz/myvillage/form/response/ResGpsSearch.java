package com.ccz.myvillage.form.response;

import com.ccz.myvillage.dto.BuildingInfo;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by 1100177 on 2018. 7. 11..
 */

public class ResGpsSearch extends ResponseCmd {
    @JsonProperty
    private List<BuildingInfo> buildings;

    @JsonProperty
    public List<BuildingInfo> getBuildings() {
        return buildings;
    }

    @JsonProperty
    public void setBuildings(List<BuildingInfo> buildings) {
        this.buildings = buildings;
    }
}
