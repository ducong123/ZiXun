package com.nainaiwang.bean;

/**
 * Created by Administrator on 2017/1/9.
 */
public class Details {

    private String create_time;
    private String author;
    private String name;
    private String content;

    public Details(String create_time, String content, String name, String author) {
        this.create_time = create_time;
        this.content = content;
        this.name = name;
        this.author = author;
    }

    public Details() {
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Details{" +
                "create_time='" + create_time + '\'' +
                ", author='" + author + '\'' +
                ", name='" + name + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
