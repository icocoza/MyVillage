package com.ccz.myvillage.form.request;

import com.ccz.myvillage.IConst;
import com.ccz.myvillage.constants.ECmd;
import com.ccz.myvillage.constants.EItemType;
import com.ccz.myvillage.form.CommonCmd;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class ReqAddBoard extends CommonCmd {

    @JsonProperty
    private String title, content;
    @JsonProperty
    private boolean hasimage, hasfile;
    @JsonProperty
    private int category;
    @JsonProperty
    private EItemType itemtype;
    @JsonProperty
    private List<String> fileids = new ArrayList<>();

    public ReqAddBoard() {
        super(ECmd.addboard);
    }

    public ReqAddBoard(ECmd cmd) {
        super(cmd);
    }

    @JsonProperty public String getTitle() {    return title;   }
    @JsonProperty public String getContent() {  return content; }
    @JsonProperty public boolean isHasimage()   {   return hasimage;    }
    @JsonProperty public boolean isHasfile()    {   return hasfile; }
    @JsonProperty public int getCategory()  {   return category;   }
    @JsonProperty public EItemType getItemtype() {  return itemtype;    }
    @JsonProperty public List<String> getFileids()  {   return fileids; }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setHasimage(boolean hasimage) {
        this.hasimage = hasimage;
    }

    public void setHasfile(boolean hasfile) {
        this.hasfile = hasfile;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public void setItemtype(EItemType itemtype) {
        this.itemtype = itemtype;
    }

    public void setFileids(List<String> fileids) {
        this.fileids = fileids;
    }
}
