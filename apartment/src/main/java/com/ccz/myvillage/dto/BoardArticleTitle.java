package com.ccz.myvillage.dto;

import java.util.Date;

public class BoardArticleTitle {
    public String boardid, title;
    public String  content;
    public boolean hasimage, hasfile;
    public String createdBy;
    public Date createTime;

    public BoardArticleTitle() {}
    public BoardArticleTitle(String title) {
        this.title = title;
    }

}
