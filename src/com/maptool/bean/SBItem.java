package com.maptool.bean;

public class SBItem {
	private boolean isSelected;
	private String title;
	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public SBItem(boolean isSelected, String title) {
		super();
		this.isSelected = isSelected;
		this.title = title;
	}
	public SBItem() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
