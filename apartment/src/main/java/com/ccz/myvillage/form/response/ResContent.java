package com.ccz.myvillage.form.response;


import com.ccz.myvillage.dto.BoardFile;
import com.ccz.myvillage.dto.BoardLike;
import com.ccz.myvillage.dto.VoteItem;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ResContent extends ResponseCmd {
    @JsonProperty private String content;
    @JsonProperty private List<BoardFile> files;
    @JsonProperty private BoardLike like;
    @JsonProperty private List<VoteItem> vote;

    @JsonProperty public String getContent() {  return content; }
    @JsonProperty public List<BoardFile> getFiles() {   return files;   }
    @JsonProperty public BoardLike getLike() {  return like;    }
    @JsonProperty public List<VoteItem> getVote() { return vote;    }
}
