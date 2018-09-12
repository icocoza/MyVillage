package com.ccz.myvillage.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ScrapItemDetail extends ScrapItem {
    @JsonProperty private String body;

    @JsonProperty public String getBody() {  return body; }
}
