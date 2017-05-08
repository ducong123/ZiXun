package com.nainaiwang.bean;

/**
 * Created by Administrator on 2017/2/27.
 */
public class Lunbotu {

    private String id;
    private String cover;
    private String short_content;

    public Lunbotu(String id, String cover, String short_content) {
        this.id = id;
        this.cover = cover;
        this.short_content = short_content;
    }

    public Lunbotu() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getShort_content() {
        return short_content;
    }

    public void setShort_content(String short_content) {
        this.short_content = short_content;
    }

    @Override
    public String toString() {
        return "Lunbotu{" +
                "id='" + id + '\'' +
                ", cover='" + cover + '\'' +
                ", short_content='" + short_content + '\'' +
                '}';
    }
}
