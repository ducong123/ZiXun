package com.nainaiwang.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Administrator on 2017/1/5.
 */

@Table(name = "search_history")
public class SearchHistory {
    @Column(name = "id", isId = true, autoGen = true)
    public int id;
    @Column(name = "str")
    public String str;

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "SearchHistory{" +
                "id=" + id +
                ", str='" + str + '\'' +
                '}';
    }
}
