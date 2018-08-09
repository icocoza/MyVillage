package com.ccz.myvillage.form.response;

import com.ccz.myvillage.constants.EBoardPreference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ResBoardLike extends ResponseCmd{
    @JsonProperty private boolean isadd;
    @JsonProperty private EBoardPreference preference;

    @JsonProperty public boolean isIsadd() {
        return isadd;
    }
    @JsonProperty public EBoardPreference getPreference() {     return preference;  }
}
