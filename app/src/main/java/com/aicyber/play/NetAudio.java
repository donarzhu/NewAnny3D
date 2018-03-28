package com.aicyber.play;

import java.io.IOException;
import com.aicyber.util.Logger;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.audiofx.Visualizer;

public class NetAudio extends MultiMediaBase implements OnBufferingUpdateListener, OnCompletionListener, OnPreparedListener {
	private Visualizer mVisualizer;              // 音频可视化类
	private static final int CAPTURE_SIZE = 256; // 获取这些数据, 用于显示

	public NetAudio() {
		// TODO Auto-generated constructor stub
		mPlayState = STATE_UNKONOW;
	}
	
	public void init(OnPlayState callback, String url)
	{
		try {
			mCallback = callback;
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置媒体流类型
			mediaPlayer.setOnBufferingUpdateListener(this);
			mediaPlayer.setOnPreparedListener(this);
			mediaPlayer.setOnCompletionListener(this);
			//initVisualizer();
			mCallback.onInit(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void initVisualizer()
	{
		mVisualizer = new Visualizer(mediaPlayer.getAudioSessionId());
		mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
			@Override
			public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
				for (int i = 0; i < waveform.length; i++) {
					//Logger.debug("频率=================================================" + waveform[i]);
				}
			}
			@Override
			public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
			}
		}, Visualizer.getMaxCaptureRate(), true, false);
		mVisualizer.setCaptureSize(CAPTURE_SIZE);
		mVisualizer.setEnabled(true);
	}
	
	//开始播放
	public void play() {
		if (mediaPlayer != null) {
			mediaPlayer.start(); 
			mPlayState = STATE_PLAY;
			mCallback.onPlay();
		}
	}  
	
	//播放对应网络地址的音频
	public void playUrl(String url) {
		try {
			if (mediaPlayer != null) {
				mediaPlayer.reset();
				mediaPlayer.setDataSource(url); // 设置数据源
				mediaPlayer.prepare(); // prepare自动播放
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace(); 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//暂停
	public void pause() {
		if (mediaPlayer != null) {
			mediaPlayer.pause();
			mPlayState = STATE_PAUSE;
			mCallback.onPause();
		}
	}

	// 停止
	public void stop() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
			mPlayState = STATE_STOP;
			mCallback.onCompletion();
		}
	}
	
	//得到音频时长
	public int getDurationTime()
	{
		return mediaPlayer.getDuration();
	}
	
	//得到播放的位置
	public int getPositionTime()
	{
		return mediaPlayer.getCurrentPosition();
	}
		
	@Override
	public void onPrepared(MediaPlayer mp) {
		// TODO Auto-generated method stub
		Logger.debug("onPrepared");
		mp.start();  
		mPlayState = STATE_START;
		mCallback.onStart();
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		// 结束了直接掉停止
		stop();
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		mCallback.onBufferUpdata(percent);
	}
}
