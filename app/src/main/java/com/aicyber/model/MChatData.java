package com.aicyber.model;

public class MChatData extends MDataBase {

	private String msg = "";
	private String successful = "";
	private DataBody data = null;

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getSuccessful() {
		return successful;
	}

	public void setSuccessful(String successful) {
		this.successful = successful;
	}

	public DataBody getData() {
		return data;
	}

	public void setData(DataBody data) {
		this.data = data;
	}

	public class DataBody {
		String emotion = "";
		String topic_id = "";
		String client_user_id = "";
		String app_id = "";
		String topic = "";
		String output_type = "";
		String replay = "";
		String output_resource = "";
		String tts_url = "";
		String output = "";
		String input = "";
		String group_id = "";
		String id = "";
		String proactive = "";

		public String getEmotion() {
			return emotion;
		}

		public void setEmotion(String emotion) {
			this.emotion = emotion;
		}

		public String getTopic_id() {
			return topic_id;
		}

		public void setTopic_id(String topic_id) {
			this.topic_id = topic_id;
		}

		public String getClient_user_id() {
			return client_user_id;
		}

		public void setClient_user_id(String client_user_id) {
			this.client_user_id = client_user_id;
		}

		public String getApp_id() {
			return app_id;
		}

		public void setApp_id(String app_id) {
			this.app_id = app_id;
		}

		public String getTopic() {
			return topic;
		}

		public void setTopic(String topic) {
			this.topic = topic;
		}

		public String getOutput_type() {
			return output_type;
		}

		public void setOutput_type(String output_type) {
			this.output_type = output_type;
		}

		public String getReplay() {
			return replay;
		}

		public void setReplay(String replay) {
			this.replay = replay;
		}

		public String getOutput_resource() {
			return output_resource;
		}

		public void setOutput_resource(String output_resource) {
			this.output_resource = output_resource;
		}

		public String getTts_url() {
			return tts_url;
		}

		public void setTts_url(String tts_url) {
			this.tts_url = tts_url;
		}

		public String getOutput() {
			return output;
		}

		public void setOutput(String output) {
			this.output = output;
		}

		public String getInput() {
			return input;
		}

		public void setInput(String input) {
			this.input = input;
		}

		public String getGroup_id() {
			return group_id;
		}

		public void setGroup_id(String group_id) {
			this.group_id = group_id;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getProactive() {
			return proactive;
		}

		public void setProactive(String proactive) {
			this.proactive = proactive;
		}
	}
}
