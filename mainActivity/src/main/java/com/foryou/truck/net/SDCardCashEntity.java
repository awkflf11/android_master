package com.foryou.truck.net;

import java.io.Serializable;

public class SDCardCashEntity implements Serializable{
	public long time=0;
	public String data;
	public String url="";
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	
}
