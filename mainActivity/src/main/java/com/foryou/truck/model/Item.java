package com.foryou.truck.model;

public class Item {
	
	public Item(){}
	
	private boolean isSelect;
	private int id;
	public String tagText;
	public String tagKey;//
	
	public boolean isSelect() {
		return isSelect;
	}
 
	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}
	 
	public int getId() {
		return id;
	}
	 
	public void setId(int id) {
		this.id = id;
	}
	
	
}
