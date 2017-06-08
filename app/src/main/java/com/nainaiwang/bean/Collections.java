package com.nainaiwang.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "collections")
public class Collections {

	@Column(name = "id", isId = true, autoGen = true)
	public int id;
	@Column(name = "name")
	private String name;

	@Column(name = "create_time")
	private String create_time;
	@Column(name = "url")
	private String url;
	@Column(name = "article_id")
	private String article_id;

	public Collections() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Collections(int id, String name, String create_time, String url,String article_id) {
		super();
		this.id = id;
		this.name = name;
		this.create_time = create_time;
		this.url = url;
		this.article_id = article_id;

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
    public String getArticle_id() {
        return article_id;
    }

    public void setArticle_id(String article_id) {
        this.article_id = article_id;
    }

	@Override
	public String toString() {
		return "Collections [id=" + id + ", name=" + name + ", create_time=" + create_time
				+ ", url=" + url+ ",article_id"+article_id+"]";
	}

}
