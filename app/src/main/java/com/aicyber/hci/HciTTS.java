package com.aicyber.hci;

import java.util.Vector;

import com.aicyber.file.FileOperation;
import com.aicyber.util.Logger;
import com.sinovoice.hcicloudsdk.api.HciCloudSys;
import com.sinovoice.hcicloudsdk.api.tts.HciCloudTts;
import com.sinovoice.hcicloudsdk.common.HciErrorCode;
import com.sinovoice.hcicloudsdk.common.Session;
import com.sinovoice.hcicloudsdk.common.tts.ITtsSynthCallback;
import com.sinovoice.hcicloudsdk.common.tts.TtsConfig;
import com.sinovoice.hcicloudsdk.common.tts.TtsInitParam;
import com.sinovoice.hcicloudsdk.common.tts.TtsSynthResult;

import android.content.Context;

public class HciTTS {

	private static String mCapkey = "";
	private static Vector<byte[]> mSpeackContent = new Vector<byte[]>();
	private static FileOperation mFileOperation = null;
	
	public void initTTS(String capkey, Context context)
	{
		mCapkey = capkey;
		mFileOperation = FileOperation.getInstance(context);
		// TTS初始化
		TtsInitParam ttsInitParam = new TtsInitParam();
		String dataPath = context.getFilesDir().getPath().replace("files", "lib");
		// 获取App应用中的lib的路径,放置能力所需资源文件。如果使用/data/data/packagename/lib目录,需要添加android_so的标记
		ttsInitParam.addParam(TtsInitParam.PARAM_KEY_DATA_PATH, dataPath);
		ttsInitParam.addParam(TtsInitParam.PARAM_KEY_FILE_FLAG, TtsInitParam.VALUE_OF_PARAM_FILE_FLAG_ANDROID_SO);
		// 此处演示初始化的能力为tts.cloud.xiaokun, 用户可以根据自己可用的能力进行设置, 另外,此处可以传入多个能力值,并用;隔开
		ttsInitParam.addParam(TtsInitParam.PARAM_KEY_INIT_CAP_KEYS, mCapkey);
		// 调用初始化方法
		int errCode = HciCloudTts.hciTtsInit(ttsInitParam.getStringConfig());
		if (errCode != HciErrorCode.HCI_ERR_NONE) {
			Logger.debug("hciTtsInit error:" + HciCloudSys.hciGetErrorInfo(errCode));
			return;
		} else {
			Logger.debug("hciTtsInit Success");
		}
	}
	
	
	public void speackWord(String word)
	{ 
		TtsConfig synthConfig = new TtsConfig();
		if (mCapkey.indexOf("tts.cloud.synth") != -1) {
			// property 属于 私有云 云端能力 必填参数，具体请参考开发手册
			// none: 所有标记将会被视为文本读出，缺省值
			synthConfig.addParam(TtsConfig.PrivateCloudConfig.PARAM_KEY_PROPERTY, "cn_xiaokun_common");
			synthConfig.addParam(TtsConfig.BasicConfig.PARAM_KEY_TAG_MODE, "none");
		}
		Synth(mCapkey, synthConfig, word);
		// TTS反初始化
		//HciCloudTts.hciTtsRelease();
	}
	
	public static void Synth(String capkey, TtsConfig synthConfig, String synthText) {
		Logger.debug("启动合成============================================");
        //启动TTS会话
        TtsConfig sessionConfig = new TtsConfig();
        sessionConfig.addParam(TtsConfig.SessionConfig.PARAM_KEY_CAP_KEY, capkey);
        Session session = new Session();
        int errCode = HciCloudTts.hciTtsSessionStart(sessionConfig.getStringConfig(), session);
        if (HciErrorCode.HCI_ERR_NONE != errCode) {
        	Logger.debug("启动合成错误============================================" + HciCloudSys.hciGetErrorInfo(errCode));
			return;
		}

        // 开始合成
        errCode = HciCloudTts.hciTtsSynth(session, synthText, synthConfig.getStringConfig(),  
                mTtsSynthCallback);
        if (errCode == HciErrorCode.HCI_ERR_NONE) {
        	Logger.debug("hciTtsSynth Success");
        }else{
        	Logger.debug("hciTtsSynth error:");
        }
        
        HciCloudTts.hciTtsSessionStop(session);
    }
	
	private static ITtsSynthCallback mTtsSynthCallback = new ITtsSynthCallback() {
        @Override
        public boolean onSynthFinish(int errorCode, TtsSynthResult result) {
            // errorCode 为当前合成操作返回的错误码,如果返回值为HciErrorCode.HCI_ERR_NONE则表示合成成功
            if (errorCode != HciErrorCode.HCI_ERR_NONE) {
                return false;
            }

            
            // 将本次合成的数据写入文件
            // 每次合成的数据，可能不是需要合成文本的全部，需要多次写入
            if(result != null && result.getVoiceData() != null){
                int length = result.getVoiceData().length;
                if (length > 0) {
                	//////////合成的字节流---将其写成WAV
                	byte[] data = result.getVoiceData();
                	mSpeackContent.add(data);
                }
            }

            if (!result.isHasMoreData()) {
            	///关闭
                //flushOutputStream();
            	mFileOperation.writeFile(mSpeackContent);
            	mSpeackContent.clear();
            	Logger.debug("合成完毕========================================");
            }
            
            //mark 标记语言的回调结果
            if(result.getTtsSynthMark().size() > 0)
            {
            	for(int i=0;i<result.getTtsSynthMark().size();++i)
            	{
            		//ShowMessage(result.getTtsSynthMark().get(i).toStirng());
            		Logger.debug("mark==============" + result.getTtsSynthMark().get(i).toStirng());
            	}
            }
            // 返回true表示处理结果成功,通知引擎可以继续合成并返回下一次的合成结果; 如果不希望引擎继续合成, 则返回false
            // 该方法在引擎中是同步的,即引擎会持续阻塞一直到该方法执行结束
            return true;
        }
    };
}
