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
		// TTS��ʼ��
		TtsInitParam ttsInitParam = new TtsInitParam();
		String dataPath = context.getFilesDir().getPath().replace("files", "lib");
		// ��ȡAppӦ���е�lib��·��,��������������Դ�ļ������ʹ��/data/data/packagename/libĿ¼,��Ҫ���android_so�ı��
		ttsInitParam.addParam(TtsInitParam.PARAM_KEY_DATA_PATH, dataPath);
		ttsInitParam.addParam(TtsInitParam.PARAM_KEY_FILE_FLAG, TtsInitParam.VALUE_OF_PARAM_FILE_FLAG_ANDROID_SO);
		// �˴���ʾ��ʼ��������Ϊtts.cloud.xiaokun, �û����Ը����Լ����õ�������������, ����,�˴����Դ���������ֵ,����;����
		ttsInitParam.addParam(TtsInitParam.PARAM_KEY_INIT_CAP_KEYS, mCapkey);
		// ���ó�ʼ������
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
			// property ���� ˽���� �ƶ����� ���������������ο������ֲ�
			// none: ���б�ǽ��ᱻ��Ϊ�ı�������ȱʡֵ
			synthConfig.addParam(TtsConfig.PrivateCloudConfig.PARAM_KEY_PROPERTY, "cn_xiaokun_common");
			synthConfig.addParam(TtsConfig.BasicConfig.PARAM_KEY_TAG_MODE, "none");
		}
		Synth(mCapkey, synthConfig, word);
		// TTS����ʼ��
		//HciCloudTts.hciTtsRelease();
	}
	
	public static void Synth(String capkey, TtsConfig synthConfig, String synthText) {
		Logger.debug("�����ϳ�============================================");
        //����TTS�Ự
        TtsConfig sessionConfig = new TtsConfig();
        sessionConfig.addParam(TtsConfig.SessionConfig.PARAM_KEY_CAP_KEY, capkey);
        Session session = new Session();
        int errCode = HciCloudTts.hciTtsSessionStart(sessionConfig.getStringConfig(), session);
        if (HciErrorCode.HCI_ERR_NONE != errCode) {
        	Logger.debug("�����ϳɴ���============================================" + HciCloudSys.hciGetErrorInfo(errCode));
			return;
		}

        // ��ʼ�ϳ�
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
            // errorCode Ϊ��ǰ�ϳɲ������صĴ�����,�������ֵΪHciErrorCode.HCI_ERR_NONE���ʾ�ϳɳɹ�
            if (errorCode != HciErrorCode.HCI_ERR_NONE) {
                return false;
            }

            
            // �����κϳɵ�����д���ļ�
            // ÿ�κϳɵ����ݣ����ܲ�����Ҫ�ϳ��ı���ȫ������Ҫ���д��
            if(result != null && result.getVoiceData() != null){
                int length = result.getVoiceData().length;
                if (length > 0) {
                	//////////�ϳɵ��ֽ���---����д��WAV
                	byte[] data = result.getVoiceData();
                	mSpeackContent.add(data);
                }
            }

            if (!result.isHasMoreData()) {
            	///�ر�
                //flushOutputStream();
            	mFileOperation.writeFile(mSpeackContent);
            	mSpeackContent.clear();
            	Logger.debug("�ϳ����========================================");
            }
            
            //mark ������ԵĻص����
            if(result.getTtsSynthMark().size() > 0)
            {
            	for(int i=0;i<result.getTtsSynthMark().size();++i)
            	{
            		//ShowMessage(result.getTtsSynthMark().get(i).toStirng());
            		Logger.debug("mark==============" + result.getTtsSynthMark().get(i).toStirng());
            	}
            }
            // ����true��ʾ�������ɹ�,֪ͨ������Լ����ϳɲ�������һ�εĺϳɽ��; �����ϣ����������ϳ�, �򷵻�false
            // �÷�������������ͬ����,��������������һֱ���÷���ִ�н���
            return true;
        }
    };
}
