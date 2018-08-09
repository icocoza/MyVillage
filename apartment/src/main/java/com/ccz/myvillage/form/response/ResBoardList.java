package com.ccz.myvillage.form.response;

import com.ccz.myvillage.dto.BoardItem;
import com.ccz.myvillage.form.CommonCmd;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ResBoardList extends ResponseCmd {
    @JsonProperty    private List<BoardItem> data;
    @JsonProperty    private int category;

    @JsonProperty
    public List<BoardItem> getData() {
        return data;
    }

    @JsonProperty
    public int getCategory() {
        return category;
    }
}
