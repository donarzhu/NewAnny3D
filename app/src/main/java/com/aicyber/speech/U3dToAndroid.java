package com.aicyber.speech;
/**
 * 
 * @author zhisheng
 * 处理 U3D处理完的回掉函数
 */

import com.aicyber.hci.HciCloudManager;
import com.aicyber.util.Logger;
import com.aicyber.util.OnTimerCallback;
import com.aicyber.util.TimeUtil;
import com.unity3d.player.UnityPlayer;

public class U3dToAndroid {
	public void reStartListening()
	{
		Logger.debug("1==Unity 说完的时间设置为主动提问的其实时间==============================");
		TimeUtil.getInstance().startTimeDelay();
		
		Logger.debug("2==Unity 说完话设置录音机重新启动==============================");
		HciCloudManager.isStart = true;
		HciCloudManager.getInstance().startRecording();	
		
		String emotion = HciCloudManager.getmEmotion() + "";
		Logger.debug("3==Unity 说完话通知UNITY另外播放的表情动画==============================" + emotion);
		UnityPlayer.UnitySendMessage("anny", "sendMessageFromAndroid", emotion);
	}
}
