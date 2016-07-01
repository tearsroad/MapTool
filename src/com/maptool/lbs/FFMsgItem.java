package com.maptool.lbs;

public class FFMsgItem {
	private String title;
	private String link;
	private Class activity;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public Class getActivity() {
		return activity;
	}
	public void setActivity(Class activity) {
		this.activity = activity;
	}
	public FFMsgItem(String title, String link) {
		super();
		this.title = title;
		this.link = link;
	}
	public FFMsgItem() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
