package com.aicyber.hci;

import com.aicyber.record.AudioRecordFunc;
import com.aicyber.util.Logger;
import com.sinovoice.hcicloudsdk.api.asr.HciCloudAsr;
import com.sinovoice.hcicloudsdk.common.HciErrorCode;
import com.sinovoice.hcicloudsdk.common.Session;
import com.sinovoice.hcicloudsdk.common.asr.AsrConfig;
import com.sinovoice.hcicloudsdk.common.asr.AsrGrammarId;
import com.sinovoice.hcicloudsdk.common.asr.AsrInitParam;
import com.sinovoice.hcicloudsdk.common.asr.AsrRecogResult;

import android.content.Context;

public class HciAsr {

	private static HciAsr m_Instance = null;
	private Context mContext;
	private AsrGrammarId grammarId;
	private AsrConfig recogConfig;
	private String mCapKey;

	private HciAsr() {

	}

	public static HciAsr getInstance() {
		if (null == m_Instance) {
			m_Instance = new HciAsr();
		}
		return m_Instance;
	}

	public void init(Context context, String capkey) {
		mContext = context;
		mCapKey = capkey;
		AsrInitParam asrInitParam = new AsrInitParam();
		String dataPath = context.getFilesDir().getAbsolutePath().replace("files", "lib");
		asrInitParam.addParam(AsrInitParam.PARAM_KEY_DATA_PATH, dataPath);
		asrInitParam.addParam(AsrInitParam.PARAM_KEY_FILE_FLAG, AsrInitParam.VALUE_OF_PARAM_FILE_FLAG_ANDROID_SO);
		asrInitParam.addParam(AsrInitParam.PARAM_KEY_INIT_CAP_KEYS, mCapKey);
		int errCode = HciCloudAsr.hciAsrInit(asrInitParam.getStringConfig());
		if (errCode != HciErrorCode.HCI_ERR_NONE) {
			return;
		} else {
		}

		AsrConfig asrConfig = new AsrConfig();

		grammarId = new AsrGrammarId();
		recogConfig = new AsrConfig();
		if (grammarId.isValid()) {
			recogConfig.addParam(AsrConfig.GrammarConfig.PARAM_KEY_GRAMMAR_TYPE,
					AsrConfig.GrammarConfig.VALUE_OF_PARAM_GRAMMAR_TYPE_ID);
			recogConfig.addParam(AsrConfig.GrammarConfig.PARAM_KEY_GRAMMAR_ID, grammarId.toString());
		}
	}

	public void Recog(byte[] data) {
		byte[] voiceData = data;
		if (null == voiceData) {
			return;
		}

		int errCode = -1;
		AsrConfig config = new AsrConfig();
		config.addParam(AsrConfig.SessionConfig.PARAM_KEY_CAP_KEY, mCapKey);
		config.addParam(AsrConfig.SessionConfig.PARAM_KEY_REALTIME, "no");
		String sSessionConfig = config.getStringConfig();
		;
		Session nSessionId = new Session();
		errCode = HciCloudAsr.hciAsrSessionStart(sSessionConfig, nSessionId);
		if (HciErrorCode.HCI_ERR_NONE != errCode) {
			Logger.debug("====================================识别配置初始化失败！");
			return;
		}

		AsrRecogResult asrResult = new AsrRecogResult();
		errCode = HciCloudAsr.hciAsrRecog(nSessionId, voiceData, recogConfig.getStringConfig(), null, asrResult);

		if (HciErrorCode.HCI_ERR_NONE == errCode) {
			printAsrResult(asrResult);
		} else {
			Logger.debug("====================================识别是发生错误==" + errCode);
		}
		HciCloudAsr.hciAsrSessionStop(nSessionId);
	}

	private static void printAsrResult(AsrRecogResult recogResult) {
		if (recogResult.getRecogItemList().size() < 1) {
			Logger.debug("====================================识别为空");
			AudioRecordFunc.getInstance().startRecord();
		}
		for (int i = 0; i < recogResult.getRecogItemList().size(); i++) {
			if (recogResult.getRecogItemList().get(i).getRecogResult() != null) {
				String utf8 = recogResult.getRecogItemList().get(i).getRecogResult();
				Logger.debug("====================================识别结果===" + utf8);
				AudioRecordFunc.getInstance().startRecord();
			} else {
				Logger.debug("====================================识别结果===" + "未知问题");
				AudioRecordFunc.getInstance().startRecord();
			}
		}
	}
}
