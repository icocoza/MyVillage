package com.ccz.myvillage.dto;

import com.ccz.myvillage.form.CommonCmd;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BoardItem  implements Serializable {
    @JsonProperty    private String userid;
    @JsonProperty    private String boardid, title;
    @JsonProperty    private String itemtype;
    @JsonProperty    private String  content;
    @JsonProperty    private boolean hasimage, hasfile;
    @JsonProperty    private String createusername;
    @JsonProperty    private Date createtime;
    @JsonProperty   private String cropurl;
    @JsonProperty    private int likes = 0, dislikes=0, visit=0, reply=0;

    @JsonProperty
    public String getUserid() {
        return userid;
    }
    @JsonProperty
    public String getBoardid() {
        return boardid;
    }
    @JsonProperty
    public String getTitle(){
        return title;
    }
    @JsonProperty
    public String getItemtype() {
        return itemtype;
    }
    @JsonProperty
    public String getContent() {
        return content;
    }
    @JsonProperty
    public boolean isHasimage() {
        return hasimage;
    }
    @JsonProperty
    public boolean isHasfile() {
        return hasfile;
    }
    @JsonProperty
    public String getCreateusername() {
        return createusername;
    }
    @JsonProperty
    public Date getCreatetime() {
        return createtime;
    }
    @JsonProperty
    public int getLikes() {
        return likes;
    }
    @JsonProperty
    public int getDislikes() {
        return dislikes;
    }
    @JsonProperty
    public int getVisit() {
        return visit;
    }
    @JsonProperty
    public int getReply() {
        return reply;
    }
    @JsonProperty
    public String getCropurl() {    return cropurl; }
}
