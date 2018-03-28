package com.aicyber.play;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.aicyber.util.Logger;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.SeekBar;

public class NetVideo extends MultiMediaBase implements OnBufferingUpdateListener,  
        OnCompletionListener, OnPreparedListener,  
        SurfaceHolder.Callback 
{
	private int videoWidth;  
	private int videoHeight;   
	private SurfaceHolder surfaceHolder;  
	private SeekBar skbProgress;
	private Timer mTimer=new Timer();
	
	TimerTask mTimerTask = new TimerTask() {
		@Override
		public void run() {
			if (mediaPlayer == null)
				return;
			if (mediaPlayer.isPlaying() && skbProgress.isPressed() == false) {
				handleProgress.sendEmptyMessage(0);
			}
		}
	};

	/**
	 * 构造函数
	 * @param surfaceView  播放控件
	 * @param skbProgress  进度条
	 */
	public NetVideo(SurfaceView surfaceView, SeekBar skbProgress, OnPlayState callback) {
		this.skbProgress = skbProgress;  
		surfaceHolder = surfaceView.getHolder();  
		surfaceHolder.addCallback(this);
		mCallback = callback;
		mTimer.schedule(mTimerTask, 0, 1000);  
	}
	
	Handler handleProgress = new Handler() {
		public void handleMessage(Message msg) {
			int position = mediaPlayer.getCurrentPosition();
			int duration = mediaPlayer.getDuration();

			if (duration > 0) {
				long pos = skbProgress.getMax() * position / duration;
				skbProgress.setProgress((int) pos);
			}
		};
	};
	
	public void play() {
		mediaPlayer.start();
		mPlayState = STATE_START;
	}

	public void playUrl(String videoUrl) {
		try {
			mediaPlayer.reset();
			mediaPlayer.setDataSource(videoUrl);  
			mediaPlayer.prepare();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void pause() {
		mediaPlayer.pause();
		mPlayState = STATE_PAUSE;
	}

	public void stop() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
			mCallback.onStop();
			mPlayState = STATE_STOP;
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		try {
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setDisplay(surfaceHolder);
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnBufferingUpdateListener(this);
			mediaPlayer.setOnPreparedListener(this);
			mediaPlayer.setOnCompletionListener(this);
		} catch (Exception e) {
			Logger.debug("error" + e);
		}
		Logger.debug("surface created");
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		// TODO Auto-generated method stub
		videoWidth = mediaPlayer.getVideoWidth();
		videoHeight = mediaPlayer.getVideoHeight();
		if (videoHeight != 0 && videoWidth != 0) {
			mp.start();
			mPlayState = STATE_START;
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		mCallback.onCompletion();
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		// TODO Auto-generated method stub
		skbProgress.setSecondaryProgress(percent);
		int currentProgress = skbProgress.getMax() * mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();
		Logger.debug(currentProgress + "% play", percent + "% buffer");
	}
}
