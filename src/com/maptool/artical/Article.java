package com.maptool.artical;

public class Article {
	private String title;
	private String content;
	private String summary;
	private String imgLink;
	private String link;
	private int state;
	private String commentCount;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getImgLink() {
		return imgLink;
	}

	public void setImgLink(String imgLink) {
		this.imgLink = imgLink;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(String commentCount) {
		this.commentCount = commentCount;
	}
	
	@Override
	public String toString() {
		return "{title}"+title+"{link}"+link;
	}

	public Article(String title, String content, String summary,
			String imgLink, String link, int state, String commentCount) {
		super();
		this.title = title;
		this.content = content;
		this.summary = summary;
		this.imgLink = imgLink;
		this.link = link;
		this.state = state;
		this.commentCount = commentCount;
	}

	public Article() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Article(String title, String link) {
		super();
		this.title = title;
		this.link = link;
	}
	
}
