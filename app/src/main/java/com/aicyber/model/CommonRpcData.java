package com.aicyber.model;

/**
 * 通用WEB数据结构
 * 
 * @author zhisheng
 *
 */
public class CommonRpcData {
	private String data = "";
	private boolean success = false;

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
}
