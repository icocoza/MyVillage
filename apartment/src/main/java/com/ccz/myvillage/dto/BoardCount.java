package com.ccz.myvillage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BoardCount {
    @JsonProperty  private int likes = 0, dislikes=0, visit=0, reply=0;

    @JsonProperty public int getLikes() {   return likes;   }
    @JsonProperty public int getDislikes() {    return dislikes;    }
    @JsonProperty public int getVisit() {   return visit;   }
    @JsonProperty public int getReply() {   return reply;   }
}
