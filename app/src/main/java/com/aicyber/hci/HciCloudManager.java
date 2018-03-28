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
		 * ***************************************************加载账户相关信息**********************************************
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
		 * *******************************************配置灵云参数**********************************************************
		 */
		InitParam param = getInitParam();
		String strConfig = param.getStringConfig();
		// 初始化
        mErrCode = HciCloudSys.hciInit(strConfig, mContext);
        if (mErrCode != HciErrorCode.HCI_ERR_NONE && mErrCode != HciErrorCode.HCI_ERR_SYS_ALREADY_INIT) {
        	//初始化灵云设置参数失败参数
            return false;
        }
        
        /**
         * *******************************************获取授权期限并验证是否过期********************************************
         */
        // 获取授权/更新授权文件 :
    	mErrCode = checkAuthAndUpdateAuth();
    	// 如果授权不成功则需要反初始化
        if (mErrCode != HciErrorCode.HCI_ERR_NONE) {
            HciCloudSys.hciRelease();
            return false;
        }
        
        /**
         * ******************************************配置语音合成相关参数*************************************************
         */
        if (null != mHciTTS) {
        	mHciTTS.initTTS(mAccountInfo.getTTSCapKey(), mContext);
        	mHciTTS.speackWord("你现在可以和我对话啦");
		}
        else
        {
        	return false;
        }
        
        /**
         * ******************************************初始化语音识别*************************************************
         */
        HciAsr.getInstance().init(mContext, AccountInfo.getInstance().getASRCapKey());
		
        
        /**
         * ******************************************初始化录音机并配置声音解析参数******************************************
         */
        if (null != mHciRecorder) {
        	mHciRecorder.initASRRecorder(mAccountInfo.getASRCapKey(), mContext, asrCallback);
        	//开启录音
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
	 * 识别制定路径的语音数据
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
	 * 识别对应的二进制音频数据
	 * @param data
	 */
	public void Recog(byte[] data) {

		HciAsr.getInstance().Recog(data);
	}
	
	/**
	 * 初始化灵云的相关参数
	 * @return
	 */
	public InitParam getInitParam()
	{
		String authDirPath = mContext.getFilesDir().getAbsolutePath();
        // 前置条件：无
        InitParam initparam = new InitParam();

        // 授权文件所在路径，此项必填
        initparam.addParam(InitParam.AuthParam.PARAM_KEY_AUTH_PATH, authDirPath);

        // 是否自动访问云授权,详见 获取授权/更新授权文件处注释
        initparam.addParam(InitParam.AuthParam.PARAM_KEY_AUTO_CLOUD_AUTH, "no");

        // 灵云云服务的接口地址，此项必填
        initparam.addParam(InitParam.AuthParam.PARAM_KEY_CLOUD_URL, AccountInfo
                .getInstance().getCloudUrl());

        // 开发者Key，此项必填，由捷通华声提供
        initparam.addParam(InitParam.AuthParam.PARAM_KEY_DEVELOPER_KEY, AccountInfo
                .getInstance().getDeveloperKey());

        // 应用Key，此项必填，由捷通华声提供
        initparam.addParam(InitParam.AuthParam.PARAM_KEY_APP_KEY, AccountInfo
                .getInstance().getAppKey());

        return initparam;
	}
	
	/**
	 * 获取授权时间的相关信息
	 * @return
	 */
	private int checkAuthAndUpdateAuth() {

		// 获取系统授权到期时间
		int initResult;
		AuthExpireTime objExpireTime = new AuthExpireTime();
		initResult = HciCloudSys.hciGetAuthExpireTime(objExpireTime);
		if (initResult == HciErrorCode.HCI_ERR_NONE) {
			// 显示授权日期,如用户不需要关注该值,此处代码可忽略
			//Date date = new Date(objExpireTime.getExpireTime() * 1000);
			//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
			if (objExpireTime.getExpireTime() * 1000 > System.currentTimeMillis()) {
				// 已经成功获取了授权,并且距离授权到期有充足的时间(>7天)
				return initResult;
			}

		}

		// 获取过期时间失败或者已经过期
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
