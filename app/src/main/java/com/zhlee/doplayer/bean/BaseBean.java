package com.zhlee.doplayer.bean;


import com.google.gson.Gson;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

public class BaseBean extends DataSupport implements Serializable {

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public BaseBean fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, this.getClass());
    }
}
