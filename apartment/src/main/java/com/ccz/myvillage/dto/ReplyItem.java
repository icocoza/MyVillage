package com.ccz.myvillage.dto;

import com.ccz.myvillage.form.response.ResponseCmd;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReplyItem implements Serializable {
    @JsonProperty public long replyid, parentid;
    @JsonProperty public String userid, username;
    @JsonProperty public int depth;
    @JsonProperty public String msg;
    @JsonProperty public long replytime;

    @JsonProperty public long getReplyid() {    return replyid; }
    @JsonProperty public String getUserid() {   return userid;  }
    @JsonProperty public String getUsername() { return username;    }
    @JsonProperty public int getDepth() {     return depth;   }
    @JsonProperty public String getMsg() {  return msg; }
    @JsonProperty public long getReplytime() {  return replytime;   }

}
