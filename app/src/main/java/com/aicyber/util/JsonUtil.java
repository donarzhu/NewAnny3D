package com.aicyber.util;

import java.util.List;
import java.util.Random;

import com.aicyber.constant.Constants;
import com.aicyber.db.SaveValue;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * json解析
 * 
 * @author zzs
 * 
 */
public class JsonUtil {

	private static JSONObject jsonObject = new JSONObject();
	public static String mUserId = SaveValue.getUserId();
	public static final String mKey_AppId = "app_id";
	public static final String mKey_UserId = "client_user_id";

	public static <T> T parseJSONObject(String jsonString, Class<T> clazz) {
		return JSON.parseObject(jsonString, clazz);
	}

	public static <T> List<T> parseJSONArray(String jsonString, Class<T> clazz) {
		return JSON.parseArray(jsonString, clazz);
	}

	public static String toJSONString(Object object) {
		return JSON.toJSONString(object);
	}

	public static JSONObject getFaceRecogJsonObj(String content) {
		jsonObject.clear();
		jsonObject.put("client_user_id", mUserId);
		jsonObject.put("app_id", Constants.ROBOT_APP_ID);
		jsonObject.put("secret", Constants.ROBOT_APP_SECRET);
		jsonObject.put("is_open", "true");
		jsonObject.put("file", content);
		Random random = new Random();
		int index = random.nextInt(10000000);
		jsonObject.put("flag", System.currentTimeMillis() + "============" + index);
		return jsonObject;
	}

	// 正常的主动被动接口
	public static JSONObject getAnswerJsonObj(String content) {
		jsonObject.clear();
		jsonObject.put("client_user_id", mUserId);
		jsonObject.put("user_input", content);
		jsonObject.put("app_id", Constants.ROBOT_APP_ID);
		jsonObject.put("secret", Constants.ROBOT_APP_SECRET);
		return jsonObject;
	}

	public static JSONObject getProactiveJsonObj() {
		jsonObject.clear();
		jsonObject.put("client_user_id", mUserId);
		jsonObject.put("app_id", Constants.ROBOT_APP_ID);
		jsonObject.put("secret", Constants.ROBOT_APP_SECRET);
		return jsonObject;
	}

	// 调研问答的主动和被动接口
	public static JSONObject getQuestionAnswerJsonObj(String content) {
		jsonObject.clear();
		jsonObject.put("client_user_id", mUserId);
		jsonObject.put("userinput", content);
		jsonObject.put("appid", Constants.ROBOT_APP_ID);
		jsonObject.put("secret", Constants.ROBOT_APP_SECRET);
		return jsonObject;
	}

	public static JSONObject getQuestionProactiveJsonObj() {
		jsonObject.clear();
		jsonObject.put("client_user_id", mUserId);
		jsonObject.put("appid", Constants.ROBOT_APP_ID);
		jsonObject.put("secret", Constants.ROBOT_APP_SECRET);
		return jsonObject;
	}

	// 教学接口
	public static JSONObject getStartLesson(String userId, String LessonID, String startWay, String unitID) {
		jsonObject.clear();
		jsonObject.put("userId", userId);
		jsonObject.put("lessonId", LessonID);
		jsonObject.put("startWay", startWay);
		jsonObject.put("unitId", unitID);
		return jsonObject;
	}

	public static JSONObject getOverLesson(Integer lessonId) {
		jsonObject.clear();
		jsonObject.put("studyRecordId", lessonId);
		return jsonObject;
	}

	public static JSONObject getStartSurvey(String id) {
		// 100010
		jsonObject.clear();
		jsonObject.put("surveyId", id);
		return jsonObject;
	}

	public static JSONObject getNextSurvey(String questionId, String roundId, String input) {
		jsonObject.clear();
		jsonObject.put("questionId", questionId);
		jsonObject.put("sentence", input);
		jsonObject.put("roundId", roundId);
		jsonObject.put("userId", mUserId);
		jsonObject.put("appId", Constants.ROBOT_APP_ID);
		return jsonObject;
	}

	public static JSONObject getSurveyOver(String roundId) {
		jsonObject.clear();
		jsonObject.put("roundId", roundId);
		return jsonObject;
	}

	/**
	 * 天气接口
	 * 
	 * @param app_id
	 * @param output
	 * @param cityname
	 * @return
	 */
	public static JSONObject getWeatherJsonObj(String app_id, String output, String cityname) {
		jsonObject.clear();
		JSONObject tempJeson = new JSONObject();
		tempJeson.put("content", output);
		tempJeson.put("city", cityname);
		tempJeson.put("app_id", app_id);

		jsonObject.put("jsonrpc", "2.0");
		jsonObject.put("params", tempJeson);
		jsonObject.put("id", "aukg04b7");
		jsonObject.put("method", "filter");
		return jsonObject;
	}

	/**
	 * 附近的热点接口
	 * 
	 * @param longitude
	 * @param latitude
	 * @param input
	 * @return
	 */
	public static JSONObject getNearbyJsonObj(String longitude, String latitude, String input) {
		// 大的在前 小的在后
		jsonObject.clear();
		JSONObject tempJeson = new JSONObject();
		tempJeson.put("longitude", longitude);
		tempJeson.put("latitude", latitude);
		tempJeson.put("input", input);

		jsonObject.put("jsonrpc", "2.0");
		jsonObject.put("params", tempJeson);
		jsonObject.put("id", "aukg04b7");
		jsonObject.put("method", "nearby");
		return jsonObject;
	}

}