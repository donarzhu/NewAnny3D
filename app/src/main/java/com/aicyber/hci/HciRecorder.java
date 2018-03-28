package com.aicyber.hci;

import java.util.List;
import java.util.Random;

import com.aicyber.constant.Constants;
import com.aicyber.http.HttpRequest;
import com.aicyber.loaction.LocationService;
import com.aicyber.model.MChatData;
import com.aicyber.model.MChatData.DataBody;
import com.aicyber.model.RobotSpeakData;
import com.aicyber.model.WeatherData;
import com.aicyber.model.WeatherInfor;
import com.aicyber.util.JsonUtil;
import com.aicyber.util.Logger;
import com.aicyber.util.TimeUtil;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.sinovoice.hcicloudsdk.android.asr.recorder.ASRRecorder;
import com.sinovoice.hcicloudsdk.common.asr.AsrConfig;
import com.sinovoice.hcicloudsdk.common.asr.AsrInitParam;
import com.sinovoice.hcicloudsdk.common.asr.AsrRecogResult;
import com.sinovoice.hcicloudsdk.recorder.ASRRecorderListener;
import com.sinovoice.hcicloudsdk.recorder.RecorderEvent;
import com.unity3d.player.UnityPlayer;

import android.content.Context;

public class HciRecorder {

	private static ASRRecorder mAsrRecorder = null;
	private static AsrInitParam mAsrInitParam = null;
	private static final AsrConfig mAsrConfig = new AsrConfig();
	private static RequestQueue mQueue; // 网络请求队列
	private static String mUserId;
	private static ASRCallback mAsrCallback = null;

	public void initASRRecorder(String capKey, Context context, ASRCallback asrCallback) {
		// 网络 账号 录音机
		mQueue = Volley.newRequestQueue(context);

		mAsrCallback = asrCallback;
		// 初始化录音机
		mAsrRecorder = new ASRRecorder();
		// 配置初始化参数
		mAsrInitParam = new AsrInitParam();
		String dataPath = context.getFilesDir().getPath().replace("files", "lib");
		mAsrInitParam.addParam(AsrInitParam.PARAM_KEY_INIT_CAP_KEYS, capKey);
		mAsrInitParam.addParam(AsrInitParam.PARAM_KEY_DATA_PATH, dataPath);
		mAsrInitParam.addParam(AsrInitParam.PARAM_KEY_FILE_FLAG, AsrInitParam.VALUE_OF_PARAM_FILE_FLAG_ANDROID_SO);

		// 配置录音机识别参数
		if (null != mAsrConfig) {
			// PARAM_KEY_CAP_KEY 设置使用的能力
			mAsrConfig.addParam(AsrConfig.SessionConfig.PARAM_KEY_CAP_KEY, capKey);
			// PARAM_KEY_AUDIO_FORMAT 音频格式根据不同的能力使用不用的音频格式
			mAsrConfig.addParam(AsrConfig.AudioConfig.PARAM_KEY_AUDIO_FORMAT,
					AsrConfig.AudioConfig.VALUE_OF_PARAM_AUDIO_FORMAT_PCM_16K16BIT);
			// PARAM_KEY_ENCODE 音频编码压缩格式，使用OPUS可以有效减小数据流量
			mAsrConfig.addParam(AsrConfig.AudioConfig.PARAM_KEY_ENCODE,
					AsrConfig.AudioConfig.VALUE_OF_PARAM_ENCODE_SPEEX);

			mAsrConfig.addParam(AsrConfig.SessionConfig.PARAM_KEY_REALTIME, "no");

		}
	}

	/**
	 * 开启录音机
	 */
	public void startRecording() {
		Logger.debug("====================================重新启动录音机");
		if (null == mAsrRecorder) {
			Logger.debug("====================================重新创建录音机");
			mAsrRecorder = new ASRRecorder();
		}
		// 错误状态
		if (mAsrRecorder.getRecorderState() == ASRRecorder.RECORDER_STATE_ERROR) {
			mAsrRecorder.cancel();
			mAsrRecorder.release();
			mAsrRecorder = new ASRRecorder();
		}

		Logger.debug("====================================状态=====" + mAsrRecorder.getRecorderState());
		if (mAsrRecorder.getRecorderState() == ASRRecorder.RECORDER_STATE_RECORDING) {
			Logger.debug("====================================录音机正在录音不需要重新启动");
			return;
		}

		if (mAsrRecorder.getRecorderState() == ASRRecorder.RECORDER_STATE_NOT_INIT) {
			Logger.debug("====================================如果录音机还没有初始化则先进行初始化");
			mAsrRecorder.init(mAsrInitParam.getStringConfig(), new ASRResultProcess());
		}
		Logger.debug("====================================状态=====" + mAsrRecorder.getRecorderState());

		/*
		 * while (mAsrRecorder.getRecorderState() !=
		 * ASRRecorder.RECORDER_STATE_IDLE) {
		 * Logger.debug("====================================状态====" +
		 * mAsrRecorder.getRecorderState());
		 * Logger.debug("====================================状态不对不能重启");
		 * mAsrRecorder.init(mAsrInitParam.getStringConfig(), new
		 * ASRResultProcess()); continue; }
		 */

		Logger.debug("====================================录音机重新启动成功");
		mAsrRecorder.start(mAsrConfig.getStringConfig(), "");
		Logger.debug("====================================状态=====" + mAsrRecorder.getRecorderState());
	}

	/**
	 * 关闭录音机
	 */
	public void stopRecording() {
		if (null != mAsrRecorder) {
			Logger.debug("====================================取消录音和识别 ");
			if (mAsrRecorder.getRecorderState() == ASRRecorder.RECORDER_STATE_NOT_INIT) {
				return;
			}
			mAsrRecorder.cancel();
			mAsrRecorder.release();
		}
	}

	/**
	 * 机器人说话
	 * 
	 * @param content
	 *            说话内容
	 * @param emotion
	 *            表情动画ID
	 */

	private void speackContent(String content, int emotion) {
		HciCloudManager.setmEmotion(emotion); // 机器人表情
		if (content == null || content.equals("")) {
			Logger.debug("服务器返回结果内容为空==================");
			startRecording();
		} else {
			Logger.debug("服务器返回回答内容=============" + content);
			HciCloudManager.getInstance().speakWord(content);
		}
	}

	private void speackContent(List<String> content, int emotion) {
		int len = content.size();
		String tts_url = "";
		// 将所有的URL 拼成一个长串发给unity
		for (int i = 0; i < len; i++) {
			tts_url += content.get(i);
			if (i < len - 1) {
				tts_url += ",";
			}
		}
		if (!tts_url.equals("")) {
			Logger.debug("url====" + tts_url);
			UnityPlayer.UnitySendMessage("_GameManager", "AndroidToUnityPlay", tts_url);
		}
	}

	/**
	 * 主动请求回答
	 */
	public void proactiveRobot() {
		JSONObject jsonObject = JsonUtil.getProactiveJsonObj();
		Logger.debug("主动我发的JSON：" + jsonObject.toString());
		HttpRequest<String, MChatData> proative = new HttpRequest<String, MChatData>(Constants.URL_ACTIVE_CHAT,
				jsonObject.toString(), null, new Response.Listener<String>() {
					@Override
					public void onResponse(String result) {
						delServerMsg(result);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Logger.debug("====================================网络错误==" + error);
						if (error.toString().equals("com.android.volley.TimeoutError")) {
							speackContent("你的网络不太好，请检查您的网络状况！", 0);
						} else {
							startRecording();
						}
					}
				});
		mQueue.add(proative);
	}

	/**
	 * 解析用户输入回答
	 * 
	 * @author zhisheng
	 *
	 */
	private class ASRResultProcess implements ASRRecorderListener {
		@Override
		public void onRecorderEventError(RecorderEvent arg0, int arg1) {
			// String sError = "错误码为：" + arg1;
		}

		@Override
		public void onRecorderEventRecogFinsh(RecorderEvent recorderEvent, AsrRecogResult arg1) {
			if (arg1 != null) {
				if (arg1.getRecogItemList().size() > 0) {
					String content = arg1.getRecogItemList().get(0).getRecogResult();
					if (content.equals("")) {
						Logger.debug("======================解析录音内容为空");
						Logger.debug("====================================状态=====" + mAsrRecorder.getRecorderState());
						startRecording();
					} else {
						// 只要用户说话并且 主动次数为2次时 重置次数为0
						TimeUtil.getInstance().reSetTime();
						if (content.equals("再见")) {
							mAsrCallback.onExit();
							return;
						}
						Logger.debug("解析录音内容为===================================" + content);
						if(content.equals("你好"))
						{
							speackContent("你好", 0);
							//UnityPlayer.UnitySendMessage("anny", "sendMessageFromAndroid", "100");
//							startRecording();
							return;
						}
						else
						{
							UnityPlayer.UnitySendMessage("anny", "sendMessageFromAndroid", "0");
							startRecording();
							return;
						}
						/*
						JSONObject jsonObject = JsonUtil.getAnswerJsonObj(content);
						HttpRequest<String, MChatData> answer = new HttpRequest<String, MChatData>(
								Constants.URL_PASSIVE_CHAT, jsonObject.toString(), null,
								new Response.Listener<String>() {
									@Override
									public void onResponse(String result) {
										delServerMsg(result);
									}
								}, new Response.ErrorListener() {
									@Override
									public void onErrorResponse(VolleyError error) {
										// 网络错误
										Logger.debug("====================================网络错误==" + error);
										if (error.toString().equals("com.android.volley.TimeoutError")) {
											speackContent("你的网络不太好，请检查您的网络状况！", 0);
										} else {
											startRecording();
										}
									}
								});
						mQueue.add(answer);
						mAsrRecorder.cancel();
						*/
					}
				} else {
					// 重新启动录音
					Logger.debug("==============================================状态为：无解析内容");
					Logger.debug("====================================状态=====" + mAsrRecorder.getRecorderState());
					startRecording();
				}
			} else {
				startRecording();
			}
		}

		@Override
		public void onRecorderEventStateChange(RecorderEvent recorderEvent) {
			// String sState = "状态为：初始状态";
			if (recorderEvent == RecorderEvent.RECORDER_EVENT_BEGIN_RECORD) {
				// sState = "状态为：开始录音";
				Logger.debug("====================================change状态=====" + "开始录音");
			} else if (recorderEvent == RecorderEvent.RECORDER_EVENT_HAVING_VOICE) {
				Logger.debug("====================================change状态=====" + "听到声音");
			} else if (recorderEvent == RecorderEvent.RECORDER_EVENT_END_RECORD) {
				Logger.debug("====================================change状态=====" + "录音完毕");
			} else if (recorderEvent == RecorderEvent.RECORDER_EVENT_RECOGNIZE_COMPLETE) {
				Logger.debug("====================================change状态=====" + "识别完毕");
			} else if (recorderEvent == RecorderEvent.RECORDER_EVENT_ENGINE_ERROR) {
				Logger.debug("====================================change状态=====" + "引擎错误");
			} else if (recorderEvent == RecorderEvent.RECORDER_EVENT_DEVICE_ERROR) {
				Logger.debug("====================================change状态=====" + "设备错误");
			} else if (recorderEvent == RecorderEvent.RECORDER_EVENT_VOICE_BUFFER_FULL) {
				Logger.debug("====================================change状态=====" + "缓冲区已经满");
			} else if (recorderEvent == RecorderEvent.RECORDER_EVENT_RECOGNIZE_PROCESS) {
				Logger.debug("====================================change状态=====" + "识别中间结果");
			} else if (recorderEvent == RecorderEvent.RECORDER_EVENT_BEGIN_RECOGNIZE) {
				Logger.debug("====================================change状态=====" + "开始识别");
			} else if (recorderEvent == RecorderEvent.RECORDER_EVENT_NO_VOICE_INPUT) {
				// sState = "状态为：无音频输入";
				Logger.debug("==============================================change状态为：无音频输入");
				Logger.debug("====================================状态=====" + mAsrRecorder.getRecorderState());
				mAsrRecorder.cancel();
				startRecording();
			}
		}

		@Override
		public void onRecorderRecording(byte[] volumedata, int volume) {
		}

		@Override
		public void onRecorderEventRecogProcess(RecorderEvent recorderEvent, AsrRecogResult arg1) {
			// TODO Auto-generated method stub
			if (recorderEvent == RecorderEvent.RECORDER_EVENT_RECOGNIZE_PROCESS) {
				// String sState = "状态为：识别中间反馈";
			}
			if (arg1 != null) {
				// String sResult;
				if (arg1.getRecogItemList().size() > 0) {
					// sResult = "识别中间结果结果为："
					// + arg1.getRecogItemList().get(0).getRecogResult();
				} else {
					// sResult = "未能正确识别,请重新输入";
				}
			}
		}
	}

	/**
	 * 处理服务器数据
	 * 
	 * @param jsonRes
	 */
	private void delServerMsg(String jsonRes) {
		Logger.debug("服务器返回结果==================" + jsonRes);
		MChatData rsd = JsonUtil.parseJSONObject(jsonRes, MChatData.class);
		if (rsd == null)
			return;
		DataBody dataBody = rsd.getData();
		if (dataBody == null)
			return;
		String out_type = dataBody.getOutput_type(); // 输出类型
		if (out_type != null && out_type.equals("weather")) {// 天气
			delWeather(dataBody);
		} else if (out_type != null && out_type.equals("dance"))// 跳舞
		{
			UnityPlayer.UnitySendMessage("anny", "sendMessageFromAndroid", "100");
			String emotion = dataBody.getEmotion();
			speackContent(dataBody.getOutput(), 0);
		} else if (out_type != null && out_type.equals("audio"))// 音频
		{
			delAudioOrVedio(dataBody);
		} else if (out_type != null && out_type.equals("video"))// 视频
		{
			delAudioOrVedio(dataBody);
		} else// 正常说话
		{
			// List<String> urList = dataBody.getTts_url();
			String emotion = dataBody.getEmotion();
			String content = dataBody.getOutput();
			Logger.debug("正常聊天==================");
			if (null != content && !content.equals("")) {
				Logger.debug("内容==============================" + content);
				/*
				 * if (urList != null && urList.size() > 0) {
				 * speackContent(urList, emotion); } else {
				 * speackContent(content,rsd.getEmotion()); }
				 */
				// int n = emotion == "" ? 0 : Integer.parseInt(emotion);
				speackContent(content, 0);
			} else {
				Logger.debug("内容==============================1");
				startRecording();
				Logger.debug("内容==============================2");
			}
			// speackContent(rsd.getOutput(),rsd.getEmotion());
		}
	}

	/**
	 * 处理音频视频数据
	 * 
	 * @param rsdData
	 */
	private void delAudioOrVedio(DataBody rsdData) {
		// 随机取一个音频
		String audioUrl = "";
		String out_resource = rsdData.getOutput_resource();
		String urls[] = out_resource.split(";");
		if (urls.length > 0) {
			Random random = new Random();
			int index = random.nextInt(urls.length);
			audioUrl = urls[index];
		} else {
			audioUrl = out_resource;
		}

		// 判断是否有 http：//
		String newUrl = "";
		if (!audioUrl.contains("http://")) {
			newUrl = "http://" + audioUrl;
		} else {
			newUrl = audioUrl;
		}

		String output_type = rsdData.getOutput_type();
		if (output_type != null && output_type.equals("audio")) {
			Logger.debug("播放歌曲============================================" + newUrl);
			stopRecording();
			mAsrCallback.startAudio(newUrl);
		} else {
			Logger.debug("播放视频============================================" + newUrl);
			stopRecording();
			mAsrCallback.intoVedio(newUrl);
		}
	}

	/**
	 * 输出天气数据
	 * 
	 * @param rsdData
	 */
	private void delWeather(DataBody rsdData) {
		String app_id = rsdData.getApp_id(); // appid
		String out_put = rsdData.getOutput(); // 输出
		String out_type = rsdData.getOutput_type(); // 输出类型
		String city_name = ""; // 城市名字
		String out_resource = rsdData.getOutput_resource();
		final String emotion = rsdData.getEmotion();
		Logger.debug("进入天气==================");
		Logger.debug("城市==============================" + out_resource);
		// 本地天气
		if (out_resource != null && out_resource.equals("local;")) {
			city_name = LocationService.getInstance().getCityName();
			Logger.debug("本地城市==============================" + city_name);
		} else// 除本地以外的天气
		{
			String cityName[] = out_resource.split(";");
			if (cityName.length > 0) {
				city_name = cityName[0];
			} else {
				city_name = out_resource;
			}
		}

		// final String citufd = city_name;
		Logger.debug("请求" + "city_name" + "==================" + city_name);
		JSONObject jsonObject = JsonUtil.getWeatherJsonObj(app_id, out_put, city_name);

		Logger.debug("天气数据结构=======================================" + jsonObject.toString());
		HttpRequest<String, RobotSpeakData> weather = new HttpRequest<String, RobotSpeakData>(
				Constants.WEATHER_DATA_URL, jsonObject.toString(), null, new Response.Listener<String>() {
					@Override
					public void onResponse(String result) {
						Logger.debug("取得天气数据=======================================" + result);
						WeatherData wData = JsonUtil.parseJSONObject(result, WeatherData.class);
						WeatherInfor wInfor = JsonUtil.parseJSONObject(wData.getResult(), WeatherInfor.class);
						// 更新UNITY
						String infor = wInfor.getType() + "," + wInfor.getHightemp() + "," + wInfor.getCity();
						Logger.debug("向UNITY  发送的数据串=======================================" + infor);
						UnityPlayer.UnitySendMessage("Canvas", "ShowWeather", infor);
						speackContent(wInfor.getContent(), 0);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// 网络错误
						Logger.debug("天气数据错误=======================================" + error);
						startRecording();
					}
				});
		mQueue.add(weather);
	}
}
