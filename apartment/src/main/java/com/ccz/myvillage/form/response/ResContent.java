package com.ccz.myvillage.form.response;


import com.ccz.myvillage.dto.BoardFile;
import com.ccz.myvillage.dto.BoardLike;
import com.ccz.myvillage.dto.ScrapItemDetail;
import com.ccz.myvillage.dto.VoteItem;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class ResContent extends ResponseCmd {
    @JsonProperty private String content;
    @JsonProperty private List<BoardFile> files;
    @JsonProperty private BoardLike like;
    @JsonProperty private List<VoteItem> vote;
    @JsonProperty private List<ScrapItemDetail> scraps;

    @JsonProperty public String getContent() {  return content; }
    @JsonProperty public List<BoardFile> getFiles() {   return files;   }
    @JsonProperty public BoardLike getLike() {  return like;    }
    @JsonProperty public List<VoteItem> getVote() { return vote;    }
    @JsonProperty public List<ScrapItemDetail> getScraps() {  return scraps;  };

    public ArrayList<String> getVoteTitleList() {
        if(vote == null)
            return null;
        ArrayList<String> voteItems = new ArrayList<>();
        for(VoteItem voteItem : vote)
            voteItems.add(voteItem.getVotetext());
        return voteItems;
    }
}
