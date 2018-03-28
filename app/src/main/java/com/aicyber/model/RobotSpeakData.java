/**
 * 鏈哄櫒璇磋瘽鐨勬暟鎹粨鏋?
 */
package com.aicyber.model;

import java.util.List;

public class RobotSpeakData {
	private int id;
	
	private String app_id;

	private String group_id;
	
	private String input;
	
	private String output;
	
	private String proactive;
	
	private long topicid;

	private String topic;
	
	private int emotion; //机器人表情索引
	
	private boolean replay;
	
	private String output_type;
	
	private String output_resource;
	
	private String client_user_id;
	
	List<String> tts_url;
	
	public List<String> getTts_url() {
		return tts_url;
	}

	public void setTts_url(List<String> tts_url) {
		this.tts_url = tts_url;
	}

	public long getTopicid() {
		return topicid;
	}

	public void setTopicid(long topicid) {
		this.topicid = topicid;
	}
	
	public String getApp_id() {
		return app_id;
	}

	public void setApp_id(String app_id) {
		this.app_id = app_id;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	

	public String getGroup_id() {
		return group_id;
	}

	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public String isProactive() {
		return proactive;
	}

	public void setProactive(String proactive) {
		this.proactive = proactive;
	}

	public String getOutput_type() {
		return output_type;
	}

	public void setOutput_type(String out_type) {
		this.output_type = out_type;
	}

	public String getOutput_resource() {
		return output_resource;
	}

	public void setOutput_resource(String out_resource) {
		this.output_resource = out_resource;
	}

	public String getClient_user_id() {
		return client_user_id;
	}

	public void setClient_user_id(String client_user_id) {
		this.client_user_id = client_user_id;
	}

	public int getEmotion() {
		return emotion;
	}

	public void setEmotion(int emotion) {
		this.emotion = emotion;
	}

	public boolean isReplay() {
		return replay;
	}

	public void setReplay(boolean replay) {
		this.replay = replay;
	}
}