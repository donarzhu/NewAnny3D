package com.aicyber.play;

import android.media.MediaPlayer;

public class MultiMediaBase {
	public MediaPlayer mediaPlayer = null;      // ý�岥����
	public OnPlayState mCallback;               // ����״̬�ص�
	public int mPlayState;                       // ����״̬
	
	public static final int STATE_UNKONOW = 0;    //δ֪״̬
	public static final int STATE_START = 1;      //���ſ�ʼ
	public static final int STATE_PAUSE = 2;      //������ͣ
	public static final int STATE_PLAY = 3;       //����״̬
	public static final int STATE_STOP= 4;        //����ֹͣ
	public static final int STATE_COMPLISH= 5;    //����ֹͣ
	
	public int getmPlayState() {
		return mPlayState;
	}
	public void setmPlayState(int mPlayState) {
		this.mPlayState = mPlayState;
	}
}
