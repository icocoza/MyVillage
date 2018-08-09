package com.ccz.myvillage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VoteItem {
    @JsonProperty private String vitemid;
    @JsonProperty private int selectcount;
    @JsonProperty private String votetext;//, voteurl;

    @JsonProperty public String getVitemid() {  return vitemid; }
    @JsonProperty public int getSelectcount() { return selectcount; }
    @JsonProperty public String getVotetext() { return votetext;    }
}
