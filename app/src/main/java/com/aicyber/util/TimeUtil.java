package com.aicyber.util;

import java.util.Timer;
import java.util.TimerTask;

import com.aicyber.hci.HciCloudManager;

import android.R.integer;

public class TimeUtil {
	
	private static TimeUtil mInstance;
	private Timer mTimer;
	private int mICount;
	private long mIStartTime;
	
	private TimeUtil(){
		mTimer = new Timer();
		mICount = 0;
		mIStartTime = System.currentTimeMillis();
	}
	
	public static TimeUtil getInstance()
	{
		if (null == mInstance) {
			mInstance = new TimeUtil();
		}
		return mInstance;
	}
	
	public void setStartTime(long time){
		mIStartTime = time;
	}
	
	public int getmICount() {
		return mICount;
	}
	
	private void setTimeDelay(final OnTimerCallback call)
	{
		if (mICount < 2) {
			final long timeNum = (mICount == 0 ? 12000:20000);
			mTimer.schedule(new TimerTask() {		
				@Override
				public void run() {
					// TODO Auto-generated method stub
					long nowTime = System.currentTimeMillis();
					//Logger.debug("���������ش��߳�================================ϵͳʱ��" + nowTime);
					if (nowTime - mIStartTime <= timeNum + 1000 && nowTime - mIStartTime >= timeNum) {
						mICount++;
						call.OnEnd();
						setStartTime(nowTime);
						this.cancel();
					}
				}
			}, 1000, 1000);
		}
	}
	
	public void startTimeDelay()
	{
		setStartTime(System.currentTimeMillis());
		if (getmICount() < 2) {
			setCancel();
			//����ʱ���ӳ�
			setTimeDelay(new OnTimerCallback() {
				@Override
				public void OnEnd() {
					// TODO Auto-generated method stub
					// �˴�Ӧ�õ��ùر�¼����
					HciCloudManager.getInstance().stopRecording();
					// ���������ӿڵ���
					HciCloudManager.getInstance().proactiveRobot();
				}
			});
		}
	}
	
	public void setCancel()
	{
		mTimer.cancel();
		mTimer = new Timer();
	}
	
	public void reSetTime()
	{
		//ֹͣ���ж�ʱ
		setCancel();
		mICount = 0;
	}
}
