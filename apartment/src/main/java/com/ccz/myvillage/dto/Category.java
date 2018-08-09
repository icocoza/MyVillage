package com.ccz.myvillage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Category implements Serializable {
    @JsonProperty
    public String title;
    @JsonProperty
    public int category;

    @JsonProperty
    public String getTitle() {
        return title;
    }

    @JsonProperty
    public int getCategory() {
        return category;
    }

    @JsonProperty
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty
    public void setCategory(int category) {
        this.category = category;
    }
}
