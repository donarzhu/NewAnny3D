package com.aicyber.hci;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.sinovoice.hcicloudsdk.api.HciCloudSys;
import com.sinovoice.hcicloudsdk.common.AuthExpireTime;
import com.sinovoice.hcicloudsdk.common.HciErrorCode;
import com.sinovoice.hcicloudsdk.common.InitParam;

import android.content.Context;
public class HciCloudManager {
	
	private static HciCloudManager mHciInstance;
	private Context mContext;
	private AccountInfo mAccountInfo;
	private int mErrCode = 0;
	
	private static HciRecorder mHciRecorder = null;
	private static HciTTS mHciTTS = null;
	
	private static int mEmotion = 0;
	public static boolean isStart = false;

	
	private HciCloudManager()
	{
		
	}
	
	public static HciCloudManager getInstance()
	{
		if (null == mHciInstance) {
			mHciInstance = new HciCloudManager();
		}
		return mHciInstance;
	}
	
	public boolean initParam(Context context, ASRCallback asrCallback)
	{
		mContext = context;
		mAccountInfo = AccountInfo.getInstance();
		
		mHciRecorder = new HciRecorder();
		mHciTTS = new HciTTS();
		/**
		 * ***************************************************�����˻������Ϣ**********************************************
		 */
		if (null != mAccountInfo) {
			boolean b = mAccountInfo.loadAccountInfo(mContext);
	    	if (!b) {
				return false;
			}
		}
		else
		{
			return false;
		}
		
		/**
		 * *******************************************�������Ʋ���**********************************************************
		 */
		InitParam param = getInitParam();
		String strConfig = param.getStringConfig();
		// ��ʼ��
        mErrCode = HciCloudSys.hciInit(strConfig, mContext);
        if (mErrCode != HciErrorCode.HCI_ERR_NONE && mErrCode != HciErrorCode.HCI_ERR_SYS_ALREADY_INIT) {
        	//��ʼ���������ò���ʧ�ܲ���
            return false;
        }
        
        /**
         * *******************************************��ȡ��Ȩ���޲���֤�Ƿ����********************************************
         */
        // ��ȡ��Ȩ/������Ȩ�ļ� :
    	mErrCode = checkAuthAndUpdateAuth();
    	// �����Ȩ���ɹ�����Ҫ����ʼ��
        if (mErrCode != HciErrorCode.HCI_ERR_NONE) {
            HciCloudSys.hciRelease();
            return false;
        }
        
        /**
         * ******************************************���������ϳ���ز���*************************************************
         */
        if (null != mHciTTS) {
        	mHciTTS.initTTS(mAccountInfo.getTTSCapKey(), mContext);
        	mHciTTS.speackWord("�����ڿ��Ժ��ҶԻ���");
		}
        else
        {
        	return false;
        }
        
        /**
         * ******************************************��ʼ������ʶ��*************************************************
         */
        HciAsr.getInstance().init(mContext, AccountInfo.getInstance().getASRCapKey());
		
        
        /**
         * ******************************************��ʼ��¼����������������������******************************************
         */
        if (null != mHciRecorder) {
        	mHciRecorder.initASRRecorder(mAccountInfo.getASRCapKey(), mContext, asrCallback);
        	//����¼��
        	mHciRecorder.startRecording();
		}
        else
        {
        	return false;
        }
        
        return true;
	}
	
	public void startRecording(){
		if (isStart) {
			mHciRecorder.startRecording();
		}
		
	}
	
	public void stopRecording(){
		if (isStart) {
			mHciRecorder.stopRecording();
		}
	}
	
	public void speakWord(String word)
	{
		mHciTTS.speackWord(word);
	}
	
	public void proactiveRobot()
	{
		mHciRecorder.proactiveRobot();
	}
	
	/**
	 * ʶ���ƶ�·������������
	 * @param path
	 */
	public void Recog(String path){
		File file = new File(path);
		try {
			FileInputStream inputStream = new FileInputStream(file);
			try {
				int size = inputStream.available();
				byte[] data = new byte[size];
				int len = inputStream.read(data, 0, size);
				Recog(data);
				inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
	
	/**
	 * ʶ���Ӧ�Ķ�������Ƶ����
	 * @param data
	 */
	public void Recog(byte[] data) {

		HciAsr.getInstance().Recog(data);
	}
	
	/**
	 * ��ʼ�����Ƶ���ز���
	 * @return
	 */
	public InitParam getInitParam()
	{
		String authDirPath = mContext.getFilesDir().getAbsolutePath();
        // ǰ����������
        InitParam initparam = new InitParam();

        // ��Ȩ�ļ�����·�����������
        initparam.addParam(InitParam.AuthParam.PARAM_KEY_AUTH_PATH, authDirPath);

        // �Ƿ��Զ���������Ȩ,��� ��ȡ��Ȩ/������Ȩ�ļ���ע��
        initparam.addParam(InitParam.AuthParam.PARAM_KEY_AUTO_CLOUD_AUTH, "no");

        // �����Ʒ���Ľӿڵ�ַ���������
        initparam.addParam(InitParam.AuthParam.PARAM_KEY_CLOUD_URL, AccountInfo
                .getInstance().getCloudUrl());

        // ������Key���������ɽ�ͨ�����ṩ
        initparam.addParam(InitParam.AuthParam.PARAM_KEY_DEVELOPER_KEY, AccountInfo
                .getInstance().getDeveloperKey());

        // Ӧ��Key���������ɽ�ͨ�����ṩ
        initparam.addParam(InitParam.AuthParam.PARAM_KEY_APP_KEY, AccountInfo
                .getInstance().getAppKey());

        return initparam;
	}
	
	/**
	 * ��ȡ��Ȩʱ��������Ϣ
	 * @return
	 */
	private int checkAuthAndUpdateAuth() {

		// ��ȡϵͳ��Ȩ����ʱ��
		int initResult;
		AuthExpireTime objExpireTime = new AuthExpireTime();
		initResult = HciCloudSys.hciGetAuthExpireTime(objExpireTime);
		if (initResult == HciErrorCode.HCI_ERR_NONE) {
			// ��ʾ��Ȩ����,���û�����Ҫ��ע��ֵ,�˴�����ɺ���
			//Date date = new Date(objExpireTime.getExpireTime() * 1000);
			//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
			if (objExpireTime.getExpireTime() * 1000 > System.currentTimeMillis()) {
				// �Ѿ��ɹ���ȡ����Ȩ,���Ҿ�����Ȩ�����г����ʱ��(>7��)
				return initResult;
			}

		}

		// ��ȡ����ʱ��ʧ�ܻ����Ѿ�����
		initResult = HciCloudSys.hciCheckAuth();
		if (initResult == HciErrorCode.HCI_ERR_NONE) {
			return initResult;
		} else {
			return initResult;
		}
	}

	public static int getmEmotion() {
		return mEmotion;
	}

	public static void setmEmotion(int mEmotion) {
		HciCloudManager.mEmotion = mEmotion;
	}
}
