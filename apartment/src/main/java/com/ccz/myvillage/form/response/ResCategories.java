package com.ccz.myvillage.form.response;

import com.ccz.myvillage.dto.Category;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ResCategories extends ResponseCmd {

    @JsonProperty
    private List<Category> categories;

    @JsonProperty
    public List<Category> getCategories() {
        return categories;
    }

}
