package com.ccz.myvillage.dto;

import com.ccz.myvillage.constants.EBoardPreference;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Time;
import java.sql.Timestamp;

public class BoardLike {
    @JsonProperty private EBoardPreference preference;
    @JsonProperty private Timestamp visittime;

    @JsonProperty public EBoardPreference getPreference() {     return preference;  }
    @JsonProperty public Timestamp getVisittime() {     return visittime;   }
}
