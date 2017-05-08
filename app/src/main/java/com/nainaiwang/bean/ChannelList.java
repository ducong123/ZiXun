package com.nainaiwang.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/1/3.
 */
@Table(name = "ChannelList")
public class ChannelList {

    @Column(name = "id")
    private String id;
    @Column(name = "name")
    private String name;
    @Column(name = "create_time")
    private String create_time;
    @Column(name = "user_id")
    private String user_id;
    @Column(name = "type")
    private String type;
    @Column(name = "author")
    private String author;
    @Column(name = "update_time")
    private String update_time;
    @Column(name = "channelImageList")
    private List<String> channelImageList = new ArrayList<>();
    @Column(name = "channel")
    private String channel;
    @Column(name = "picNum")
    private String picNum;
    @Column(name = "is_ad")
    private String is_ad;


    public ChannelList() {
    }

    public ChannelList(String id, String name, String create_time, String user_id, String type, String author, String update_time, List<String> channelImageList, String channel, String picNum, String is_ad) {
        this.id = id;
        this.name = name;
        this.create_time = create_time;
        this.user_id = user_id;
        this.type = type;
        this.author = author;
        this.update_time = update_time;
        this.channelImageList = channelImageList;
        this.channel = channel;
        this.picNum = picNum;
        this.is_ad = is_ad;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getPicNum() {
        return picNum;
    }

    public void setPicNum(String picNum) {
        this.picNum = picNum;
    }

    public String getIs_ad() {
        return is_ad;
    }

    public void setIs_ad(String is_ad) {
        this.is_ad = is_ad;
    }

    public List<String> getChannelImageList() {
        return channelImageList;
    }

    public void setChannelImageList(List<String> channelImageList) {
        this.channelImageList = channelImageList;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    @Override
    public String toString() {
        return "ChannelList{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", create_time='" + create_time + '\'' +
                ", user_id='" + user_id + '\'' +
                ", type='" + type + '\'' +
                ", author='" + author + '\'' +
                ", update_time='" + update_time + '\'' +
                ", channelImageList=" + channelImageList +
                ", channel='" + channel + '\'' +
                ", picNum='" + picNum + '\'' +
                ", is_ad='" + is_ad + '\'' +
                '}';
    }
}
