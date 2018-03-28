package com.aicyber.play;

import android.media.MediaPlayer;

public class MultiMediaBase {
	public MediaPlayer mediaPlayer = null;      // Ã½Ìå²¥·ÅÆ÷
	public OnPlayState mCallback;               // ²¥·Å×´Ì¬»Øµô
	public int mPlayState;                       // ²¥·Å×´Ì¬
	
	public static final int STATE_UNKONOW = 0;    //Î´Öª×´Ì¬
	public static final int STATE_START = 1;      //²¥·Å¿ªÊ¼
	public static final int STATE_PAUSE = 2;      //²¥·ÅÔÝÍ£
	public static final int STATE_PLAY = 3;       //²¥·Å×´Ì¬
	public static final int STATE_STOP= 4;        //²¥·ÅÍ£Ö¹
	public static final int STATE_COMPLISH= 5;    //²¥·ÅÍ£Ö¹
	
	public int getmPlayState() {
		return mPlayState;
	}
	public void setmPlayState(int mPlayState) {
		this.mPlayState = mPlayState;
	}
}
