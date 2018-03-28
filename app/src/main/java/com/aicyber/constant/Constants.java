package com.aicyber.constant;

/**
 * 常量定义
 * 
 * @author zhisheng
 *
 */
public class Constants {
	public static final String TAG = "arcyber_print_log";
	// 讯飞科技语音识别获得的 android -- appID
	public static final String IFLY_VOICE_SDK_APP_ID = "5716fe9f";

	public static final String URL_API_BASE = "http://api.aicyber.com/";

	// 聊天
	public static final String URL_PASSIVE_CHAT = URL_API_BASE + "passive_chat"; // 被动聊天
	public static final String URL_ACTIVE_CHAT = URL_API_BASE + "active_chat"; // 主动聊天

	// 教学
	public static final String URL_TEACHING_START = URL_API_BASE + "child/start_lesson";
	public static final String URL_TEACHING_STUDY = URL_API_BASE + "child/study_lesson";
	public static final String URL_TEACHING_OVER = URL_API_BASE + "child/end_lesson";
	public static final String URL_TEACHING_FREE = "";
	// 教学下载基础路径
	public static final String URL_TEACHING_DOWNLOAD = "http://file.api.aicyber.com/file_manage/static/lessons/robot/v1/";

	// 人脸识别
	public static final String URL_FACE_RECOG = URL_API_BASE + "recognition/face";

	// 调研
	public static final String URL_SURVEY_START = URL_API_BASE + "aicyber/startSurvey.json";
	public static final String URL_SURVEY_NEXT = URL_API_BASE + "aicyber/replySurvey.json";
	public static final String URL_SURVEY_OVER = URL_API_BASE + "aicyber/recordList.json";

	//
	public static final String ROBOT_APP_SECRET = "b62bd41b38ff6914740d45f58847c149";
	public static final String ROBOT_APP_ID = "youer";// 2016-07-08之前的

	//
	public static final String BAIDU_LOCATION_KEY = "VA0cEX2Pf9rSI6Sb2zk3nULL";

	// 代为的接口地址
	public static final String WEATHER_DATA_URL = "http://106.75.47.70:10086";

}
