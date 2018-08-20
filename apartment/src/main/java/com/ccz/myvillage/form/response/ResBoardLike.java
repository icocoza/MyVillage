package com.ccz.myvillage.form.response;

import com.ccz.myvillage.constants.EBoardPreference;
import com.ccz.myvillage.dto.BoardCount;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ResBoardLike extends ResponseCmd{
    @JsonProperty private boolean isadd;
    @JsonProperty private EBoardPreference preference;
    @JsonProperty private BoardCount count;

    @JsonProperty public boolean isIsadd() {
        return isadd;
    }
    @JsonProperty public EBoardPreference getPreference() {     return preference;  }

    public int getLikes() {
        return count == null ? 0: count.getLikes();
    }
    public int getDislikes() {
        return count == null ? 0: count.getDislikes();
    }
    public int getVisit() {
        return count == null ? 0: count.getVisit();
    }
    public int getReply() {
        return count == null ? 0:count.getReply();
    }
}
