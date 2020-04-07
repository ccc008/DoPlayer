package com.zhlee.doplayer.bean;

public class FavoriteBean extends BaseBean {
    private int id;
    private String url;

    public FavoriteBean() {

    }

    public FavoriteBean(String url) {
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "FavoriteBean{" +
                "id=" + id +
                ", url='" + url + '\'' +
                '}';
    }
}
