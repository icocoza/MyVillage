package com.ccz.myvillage.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ScrapItem  implements Serializable {
    @JsonProperty private String scrapid;
    @JsonProperty private String scraptitle, subtitle;
    @JsonProperty private String scrapimg;

    @JsonProperty public String getScrapid() {  return scrapid; }
    @JsonProperty public String getScraptitle() {    return scraptitle;   }
    @JsonProperty public String getSubtitle() { return subtitle;    }
    @JsonProperty public String getScrapimg() { return scrapimg;    }
}
