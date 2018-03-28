package com.aicyber.speech;
/**
 * 
 * @author zhisheng
 * ���� U3D������Ļص�����
 */

import com.aicyber.hci.HciCloudManager;
import com.aicyber.util.Logger;
import com.aicyber.util.OnTimerCallback;
import com.aicyber.util.TimeUtil;
import com.unity3d.player.UnityPlayer;

public class U3dToAndroid {
	public void reStartListening()
	{
		Logger.debug("1==Unity ˵���ʱ������Ϊ�������ʵ���ʵʱ��==============================");
		TimeUtil.getInstance().startTimeDelay();
		
		Logger.debug("2==Unity ˵�껰����¼������������==============================");
		HciCloudManager.isStart = true;
		HciCloudManager.getInstance().startRecording();	
		
		String emotion = HciCloudManager.getmEmotion() + "";
		Logger.debug("3==Unity ˵�껰֪ͨUNITY���ⲥ�ŵı��鶯��==============================" + emotion);
		UnityPlayer.UnitySendMessage("anny", "sendMessageFromAndroid", emotion);
	}
}
