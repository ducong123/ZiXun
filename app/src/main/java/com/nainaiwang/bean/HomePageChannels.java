package com.nainaiwang.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Administrator on 2017/1/3.
 */
@Table(name = "homepagechannels")
public class HomePageChannels {
    @Column(name = "id", isId = true, autoGen = true)
    private String id;
    @Column(name = "name")
    private String name;

    public HomePageChannels(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public HomePageChannels() {
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "HomePageChannels{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
