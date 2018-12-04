package com.hypericum.hypapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bhumi Shah on 11/30/2017.
 */

public class FilterOtherOption {

    @SerializedName("id")
    @Expose
    public int id;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("position")
    @Expose
    public int position;
    @SerializedName("visible")
    @Expose
    public boolean visible;
    @SerializedName("variation")
    @Expose
    public boolean variation;
    @SerializedName("options")
    @Expose
    public List<String> options = new ArrayList<>();

    public FilterOtherOption withId(int id) {
        this.id = id;
        return this;
    }

    public FilterOtherOption withName(String name) {
        this.name = name;
        return this;
    }

    public FilterOtherOption withPosition(int position) {
        this.position = position;
        return this;
    }

    public FilterOtherOption withVisible(boolean visible) {
        this.visible = visible;
        return this;
    }

    public FilterOtherOption withVariation(boolean variation) {
        this.variation = variation;
        return this;
    }

    public FilterOtherOption withOptions(List<String> options) {
        this.options = options;
        return this;
    }

}
