package com.aicyber.model;

import java.util.ArrayList;
import java.util.List;

public class AudioResouceData {
	String action = "";
	List<String> url = new ArrayList<String>();

	public List<String> getUrl() {
		return url;
	}

	public void setUrl(List<String> url) {
		this.url = url;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

}
