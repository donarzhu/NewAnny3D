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
	private static RequestQueue mQueue; // �����������
	private static String mUserId;
	private static ASRCallback mAsrCallback = null;

	public void initASRRecorder(String capKey, Context context, ASRCallback asrCallback) {
		// ���� �˺� ¼����
		mQueue = Volley.newRequestQueue(context);

		mAsrCallback = asrCallback;
		// ��ʼ��¼����
		mAsrRecorder = new ASRRecorder();
		// ���ó�ʼ������
		mAsrInitParam = new AsrInitParam();
		String dataPath = context.getFilesDir().getPath().replace("files", "lib");
		mAsrInitParam.addParam(AsrInitParam.PARAM_KEY_INIT_CAP_KEYS, capKey);
		mAsrInitParam.addParam(AsrInitParam.PARAM_KEY_DATA_PATH, dataPath);
		mAsrInitParam.addParam(AsrInitParam.PARAM_KEY_FILE_FLAG, AsrInitParam.VALUE_OF_PARAM_FILE_FLAG_ANDROID_SO);

		// ����¼����ʶ�����
		if (null != mAsrConfig) {
			// PARAM_KEY_CAP_KEY ����ʹ�õ�����
			mAsrConfig.addParam(AsrConfig.SessionConfig.PARAM_KEY_CAP_KEY, capKey);
			// PARAM_KEY_AUDIO_FORMAT ��Ƶ��ʽ���ݲ�ͬ������ʹ�ò��õ���Ƶ��ʽ
			mAsrConfig.addParam(AsrConfig.AudioConfig.PARAM_KEY_AUDIO_FORMAT,
					AsrConfig.AudioConfig.VALUE_OF_PARAM_AUDIO_FORMAT_PCM_16K16BIT);
			// PARAM_KEY_ENCODE ��Ƶ����ѹ����ʽ��ʹ��OPUS������Ч��С��������
			mAsrConfig.addParam(AsrConfig.AudioConfig.PARAM_KEY_ENCODE,
					AsrConfig.AudioConfig.VALUE_OF_PARAM_ENCODE_SPEEX);

			mAsrConfig.addParam(AsrConfig.SessionConfig.PARAM_KEY_REALTIME, "no");

		}
	}

	/**
	 * ����¼����
	 */
	public void startRecording() {
		Logger.debug("====================================��������¼����");
		if (null == mAsrRecorder) {
			Logger.debug("====================================���´���¼����");
			mAsrRecorder = new ASRRecorder();
		}
		// ����״̬
		if (mAsrRecorder.getRecorderState() == ASRRecorder.RECORDER_STATE_ERROR) {
			mAsrRecorder.cancel();
			mAsrRecorder.release();
			mAsrRecorder = new ASRRecorder();
		}

		Logger.debug("====================================״̬=====" + mAsrRecorder.getRecorderState());
		if (mAsrRecorder.getRecorderState() == ASRRecorder.RECORDER_STATE_RECORDING) {
			Logger.debug("====================================¼��������¼������Ҫ��������");
			return;
		}

		if (mAsrRecorder.getRecorderState() == ASRRecorder.RECORDER_STATE_NOT_INIT) {
			Logger.debug("====================================���¼������û�г�ʼ�����Ƚ��г�ʼ��");
			mAsrRecorder.init(mAsrInitParam.getStringConfig(), new ASRResultProcess());
		}
		Logger.debug("====================================״̬=====" + mAsrRecorder.getRecorderState());

		/*
		 * while (mAsrRecorder.getRecorderState() !=
		 * ASRRecorder.RECORDER_STATE_IDLE) {
		 * Logger.debug("====================================״̬====" +
		 * mAsrRecorder.getRecorderState());
		 * Logger.debug("====================================״̬���Բ�������");
		 * mAsrRecorder.init(mAsrInitParam.getStringConfig(), new
		 * ASRResultProcess()); continue; }
		 */

		Logger.debug("====================================¼�������������ɹ�");
		mAsrRecorder.start(mAsrConfig.getStringConfig(), "");
		Logger.debug("====================================״̬=====" + mAsrRecorder.getRecorderState());
	}

	/**
	 * �ر�¼����
	 */
	public void stopRecording() {
		if (null != mAsrRecorder) {
			Logger.debug("====================================ȡ��¼����ʶ�� ");
			if (mAsrRecorder.getRecorderState() == ASRRecorder.RECORDER_STATE_NOT_INIT) {
				return;
			}
			mAsrRecorder.cancel();
			mAsrRecorder.release();
		}
	}

	/**
	 * ������˵��
	 * 
	 * @param content
	 *            ˵������
	 * @param emotion
	 *            ���鶯��ID
	 */

	private void speackContent(String content, int emotion) {
		HciCloudManager.setmEmotion(emotion); // �����˱���
		if (content == null || content.equals("")) {
			Logger.debug("���������ؽ������Ϊ��==================");
			startRecording();
		} else {
			Logger.debug("���������ػش�����=============" + content);
			HciCloudManager.getInstance().speakWord(content);
		}
	}

	private void speackContent(List<String> content, int emotion) {
		int len = content.size();
		String tts_url = "";
		// �����е�URL ƴ��һ����������unity
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
	 * ��������ش�
	 */
	public void proactiveRobot() {
		JSONObject jsonObject = JsonUtil.getProactiveJsonObj();
		Logger.debug("�����ҷ���JSON��" + jsonObject.toString());
		HttpRequest<String, MChatData> proative = new HttpRequest<String, MChatData>(Constants.URL_ACTIVE_CHAT,
				jsonObject.toString(), null, new Response.Listener<String>() {
					@Override
					public void onResponse(String result) {
						delServerMsg(result);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Logger.debug("====================================�������==" + error);
						if (error.toString().equals("com.android.volley.TimeoutError")) {
							speackContent("������粻̫�ã�������������״����", 0);
						} else {
							startRecording();
						}
					}
				});
		mQueue.add(proative);
	}

	/**
	 * �����û�����ش�
	 * 
	 * @author zhisheng
	 *
	 */
	private class ASRResultProcess implements ASRRecorderListener {
		@Override
		public void onRecorderEventError(RecorderEvent arg0, int arg1) {
			// String sError = "������Ϊ��" + arg1;
		}

		@Override
		public void onRecorderEventRecogFinsh(RecorderEvent recorderEvent, AsrRecogResult arg1) {
			if (arg1 != null) {
				if (arg1.getRecogItemList().size() > 0) {
					String content = arg1.getRecogItemList().get(0).getRecogResult();
					if (content.equals("")) {
						Logger.debug("======================����¼������Ϊ��");
						Logger.debug("====================================״̬=====" + mAsrRecorder.getRecorderState());
						startRecording();
					} else {
						// ֻҪ�û�˵������ ��������Ϊ2��ʱ ���ô���Ϊ0
						TimeUtil.getInstance().reSetTime();
						if (content.equals("�ټ�")) {
							mAsrCallback.onExit();
							return;
						}
						Logger.debug("����¼������Ϊ===================================" + content);
						if(content.equals("���"))
						{
							speackContent("���", 0);
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
										// �������
										Logger.debug("====================================�������==" + error);
										if (error.toString().equals("com.android.volley.TimeoutError")) {
											speackContent("������粻̫�ã�������������״����", 0);
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
					// ��������¼��
					Logger.debug("==============================================״̬Ϊ���޽�������");
					Logger.debug("====================================״̬=====" + mAsrRecorder.getRecorderState());
					startRecording();
				}
			} else {
				startRecording();
			}
		}

		@Override
		public void onRecorderEventStateChange(RecorderEvent recorderEvent) {
			// String sState = "״̬Ϊ����ʼ״̬";
			if (recorderEvent == RecorderEvent.RECORDER_EVENT_BEGIN_RECORD) {
				// sState = "״̬Ϊ����ʼ¼��";
				Logger.debug("====================================change״̬=====" + "��ʼ¼��");
			} else if (recorderEvent == RecorderEvent.RECORDER_EVENT_HAVING_VOICE) {
				Logger.debug("====================================change״̬=====" + "��������");
			} else if (recorderEvent == RecorderEvent.RECORDER_EVENT_END_RECORD) {
				Logger.debug("====================================change״̬=====" + "¼�����");
			} else if (recorderEvent == RecorderEvent.RECORDER_EVENT_RECOGNIZE_COMPLETE) {
				Logger.debug("====================================change״̬=====" + "ʶ�����");
			} else if (recorderEvent == RecorderEvent.RECORDER_EVENT_ENGINE_ERROR) {
				Logger.debug("====================================change״̬=====" + "�������");
			} else if (recorderEvent == RecorderEvent.RECORDER_EVENT_DEVICE_ERROR) {
				Logger.debug("====================================change״̬=====" + "�豸����");
			} else if (recorderEvent == RecorderEvent.RECORDER_EVENT_VOICE_BUFFER_FULL) {
				Logger.debug("====================================change״̬=====" + "�������Ѿ���");
			} else if (recorderEvent == RecorderEvent.RECORDER_EVENT_RECOGNIZE_PROCESS) {
				Logger.debug("====================================change״̬=====" + "ʶ���м���");
			} else if (recorderEvent == RecorderEvent.RECORDER_EVENT_BEGIN_RECOGNIZE) {
				Logger.debug("====================================change״̬=====" + "��ʼʶ��");
			} else if (recorderEvent == RecorderEvent.RECORDER_EVENT_NO_VOICE_INPUT) {
				// sState = "״̬Ϊ������Ƶ����";
				Logger.debug("==============================================change״̬Ϊ������Ƶ����");
				Logger.debug("====================================״̬=====" + mAsrRecorder.getRecorderState());
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
				// String sState = "״̬Ϊ��ʶ���м䷴��";
			}
			if (arg1 != null) {
				// String sResult;
				if (arg1.getRecogItemList().size() > 0) {
					// sResult = "ʶ���м������Ϊ��"
					// + arg1.getRecogItemList().get(0).getRecogResult();
				} else {
					// sResult = "δ����ȷʶ��,����������";
				}
			}
		}
	}

	/**
	 * �������������
	 * 
	 * @param jsonRes
	 */
	private void delServerMsg(String jsonRes) {
		Logger.debug("���������ؽ��==================" + jsonRes);
		MChatData rsd = JsonUtil.parseJSONObject(jsonRes, MChatData.class);
		if (rsd == null)
			return;
		DataBody dataBody = rsd.getData();
		if (dataBody == null)
			return;
		String out_type = dataBody.getOutput_type(); // �������
		if (out_type != null && out_type.equals("weather")) {// ����
			delWeather(dataBody);
		} else if (out_type != null && out_type.equals("dance"))// ����
		{
			UnityPlayer.UnitySendMessage("anny", "sendMessageFromAndroid", "100");
			String emotion = dataBody.getEmotion();
			speackContent(dataBody.getOutput(), 0);
		} else if (out_type != null && out_type.equals("audio"))// ��Ƶ
		{
			delAudioOrVedio(dataBody);
		} else if (out_type != null && out_type.equals("video"))// ��Ƶ
		{
			delAudioOrVedio(dataBody);
		} else// ����˵��
		{
			// List<String> urList = dataBody.getTts_url();
			String emotion = dataBody.getEmotion();
			String content = dataBody.getOutput();
			Logger.debug("��������==================");
			if (null != content && !content.equals("")) {
				Logger.debug("����==============================" + content);
				/*
				 * if (urList != null && urList.size() > 0) {
				 * speackContent(urList, emotion); } else {
				 * speackContent(content,rsd.getEmotion()); }
				 */
				// int n = emotion == "" ? 0 : Integer.parseInt(emotion);
				speackContent(content, 0);
			} else {
				Logger.debug("����==============================1");
				startRecording();
				Logger.debug("����==============================2");
			}
			// speackContent(rsd.getOutput(),rsd.getEmotion());
		}
	}

	/**
	 * ������Ƶ��Ƶ����
	 * 
	 * @param rsdData
	 */
	private void delAudioOrVedio(DataBody rsdData) {
		// ���ȡһ����Ƶ
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

		// �ж��Ƿ��� http��//
		String newUrl = "";
		if (!audioUrl.contains("http://")) {
			newUrl = "http://" + audioUrl;
		} else {
			newUrl = audioUrl;
		}

		String output_type = rsdData.getOutput_type();
		if (output_type != null && output_type.equals("audio")) {
			Logger.debug("���Ÿ���============================================" + newUrl);
			stopRecording();
			mAsrCallback.startAudio(newUrl);
		} else {
			Logger.debug("������Ƶ============================================" + newUrl);
			stopRecording();
			mAsrCallback.intoVedio(newUrl);
		}
	}

	/**
	 * �����������
	 * 
	 * @param rsdData
	 */
	private void delWeather(DataBody rsdData) {
		String app_id = rsdData.getApp_id(); // appid
		String out_put = rsdData.getOutput(); // ���
		String out_type = rsdData.getOutput_type(); // �������
		String city_name = ""; // ��������
		String out_resource = rsdData.getOutput_resource();
		final String emotion = rsdData.getEmotion();
		Logger.debug("��������==================");
		Logger.debug("����==============================" + out_resource);
		// ��������
		if (out_resource != null && out_resource.equals("local;")) {
			city_name = LocationService.getInstance().getCityName();
			Logger.debug("���س���==============================" + city_name);
		} else// ���������������
		{
			String cityName[] = out_resource.split(";");
			if (cityName.length > 0) {
				city_name = cityName[0];
			} else {
				city_name = out_resource;
			}
		}

		// final String citufd = city_name;
		Logger.debug("����" + "city_name" + "==================" + city_name);
		JSONObject jsonObject = JsonUtil.getWeatherJsonObj(app_id, out_put, city_name);

		Logger.debug("�������ݽṹ=======================================" + jsonObject.toString());
		HttpRequest<String, RobotSpeakData> weather = new HttpRequest<String, RobotSpeakData>(
				Constants.WEATHER_DATA_URL, jsonObject.toString(), null, new Response.Listener<String>() {
					@Override
					public void onResponse(String result) {
						Logger.debug("ȡ����������=======================================" + result);
						WeatherData wData = JsonUtil.parseJSONObject(result, WeatherData.class);
						WeatherInfor wInfor = JsonUtil.parseJSONObject(wData.getResult(), WeatherInfor.class);
						// ����UNITY
						String infor = wInfor.getType() + "," + wInfor.getHightemp() + "," + wInfor.getCity();
						Logger.debug("��UNITY  ���͵����ݴ�=======================================" + infor);
						UnityPlayer.UnitySendMessage("Canvas", "ShowWeather", infor);
						speackContent(wInfor.getContent(), 0);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// �������
						Logger.debug("�������ݴ���=======================================" + error);
						startRecording();
					}
				});
		mQueue.add(weather);
	}
}
