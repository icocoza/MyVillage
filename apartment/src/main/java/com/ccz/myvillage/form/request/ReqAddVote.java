package com.ccz.myvillage.form.request;

import com.ccz.myvillage.constants.ECmd;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class ReqAddVote extends ReqAddBoard {

    @JsonProperty private long expiretime;
    @JsonProperty private List<String> voteitems = new ArrayList<>();

    public ReqAddVote() {
        super(ECmd.addvote);
    }

    @JsonProperty public void setExpiretime(long expiretime) {
        this.expiretime = expiretime;
    }

    @JsonProperty public void setItemList(List<String> itemList) {
        this.voteitems = itemList;
    }

    @JsonProperty public long getExpiretime() {
        return expiretime;
    }

    @JsonProperty public List<String> getItemList() {
        return voteitems;
    }

}
