package com.aicyber.model;

import java.util.List;
import java.util.StringTokenizer;

import com.alibaba.fastjson.JSONObject;

public class WeatherData {
	String id;
	String jsonrpc;
	String result;
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getJsonrpc() {
		return jsonrpc;
	}

	public void setJsonrpc(String jsonrpc) {
		this.jsonrpc = jsonrpc;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
}

