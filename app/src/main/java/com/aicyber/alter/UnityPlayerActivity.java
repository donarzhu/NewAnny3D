package com.aicyber.alter;

import com.aicyber.hci.ASRCallback;
import com.aicyber.hci.HciCloudManager;
import com.aicyber.loaction.LocationService;
import com.aicyber.util.Logger;
import com.aicyber.view.AudioView;
import com.unity3d.player.UnityPlayer;

import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

public class UnityPlayerActivity extends BaseActivity {
	protected UnityPlayer mUnityPlayer; // don't change the name of this
										// variable; referenced from native code
	private AudioView mAudioView = null; // 音频播放VIEW

	// Setup activity layout
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		getWindow().setFormat(PixelFormat.RGBX_8888); // <--- This makes xperia
														// play happy

		// u3d 部分
		mUnityPlayer = new UnityPlayer(this);
		setContentView(mUnityPlayer);
		mUnityPlayer.requestFocus();

		// 语音识别和合成部分
		HciCloudManager.getInstance().initParam(this, new ASRCallback() {

			@Override
			public void onExit() {
				// TODO Auto-generated method stub
				android.os.Process.killProcess(android.os.Process.myPid());
			}

			@Override
			public void intoVedio(String url) {
				// TODO Auto-generated method stub
				forwordNextActivity(VideoActivity.class);
				VideoActivity.mUrl = url;
			}

			@Override
			public void startAudio(String url) {
				// TODO Auto-generated method stub
				addAudioView(url);
			}

		});

		// 获取位置部分
		LocationService.getInstance().initLocation(this);
	}

	/**
	 * 添加播放器VIEW
	 */
	public void addAudioView(String url) {
		// DisplayMetrics dm = new DisplayMetrics();
		// 取得窗口属性
		// getWindowManager().getDefaultDisplay().getMetrics(dm);
		mAudioView = null;
		if (mAudioView == null) {
			mAudioView = new AudioView(this, url);
		}
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
		addContentView(mAudioView, params);
	}

	/**
	 * 移除播放器View
	 */
	public void removeAudioView() {
		if (mAudioView != null) {
			mAudioView.removeAllViews();
			((ViewGroup) mAudioView.getParent()).removeView(mAudioView);
		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Logger.debug("=============================" + "start");
	}

	// Quit Unity
	@Override
	protected void onDestroy() {
		mUnityPlayer.quit();
		super.onDestroy();
		Logger.debug("=============================" + "destroy");
	}

	// Pause Unity
	@Override
	protected void onPause() {
		Logger.debug("=============================" + "pause");
		HciCloudManager.getInstance().stopRecording();
		// NetAudio.getInstance().pause();
		super.onPause();
		mUnityPlayer.pause();
	}

	// Resume Unity
	@Override
	protected void onResume() {
		super.onResume();
		Logger.debug("=============================" + "resum");
		// NetAudio.getInstance().play();
		// if (NetAudio.getInstance().getmPlayState() != NetAudio.STATE_PLAY)
		// {
		// HciCloudManager.getInstance().startRecording();
		// }
		mUnityPlayer.resume();
	}

	// This ensures the layout will be correct.
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mUnityPlayer.configurationChanged(newConfig);
	}

	// Notify Unity of the focus change.
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		mUnityPlayer.windowFocusChanged(hasFocus);
	}

	// For some reason the multiple keyevent type is not supported by the ndk.
	// Force event injection by overriding dispatchKeyEvent().
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_MULTIPLE)
			return mUnityPlayer.injectEvent(event);
		return super.dispatchKeyEvent(event);
	}

	// Pass any events not handled by (unfocused) views straight to UnityPlayer
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return mUnityPlayer.injectEvent(event);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return mUnityPlayer.injectEvent(event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return mUnityPlayer.injectEvent(event);
	}

	/* API12 */ public boolean onGenericMotionEvent(MotionEvent event) {
		return mUnityPlayer.injectEvent(event);
	}
}
