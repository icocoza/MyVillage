package com.ccz.myvillage.form.response;

import com.ccz.myvillage.dto.ReplyItem;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResReplyList extends ResponseCmd {
    @JsonProperty
    List<ReplyItem> data;

    @JsonProperty public List<ReplyItem> getData() {    return data; }
}
