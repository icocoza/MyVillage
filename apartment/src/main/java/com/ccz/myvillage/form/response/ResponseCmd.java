package com.ccz.myvillage.form.response;

import com.ccz.myvillage.form.CommonCmd;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by 1100177 on 2018. 7. 11..
 */

public class ResponseCmd extends CommonCmd {
    @JsonProperty
    private String result;

    @JsonProperty
    public String getResult() {
        return result;
    }

    @JsonProperty
    public void setResult(String result) {
        this.result = result;
    }
}
