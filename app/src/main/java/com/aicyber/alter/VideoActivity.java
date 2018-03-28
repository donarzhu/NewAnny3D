/**
 * 鎾斁瑙嗛鐣岄潰
 * zhangzhisheng
 */
package com.aicyber.alter;

import com.aicyber.play.NetVideo;
import com.aicyber.play.OnPlayState;
import com.aicyber.util.Logger;

import android.os.Bundle;
import android.text.style.TtsSpan.ElectronicBuilder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;

public class VideoActivity extends BaseActivity {

	private SurfaceView mSurfaceView;  //瑙嗛鎵胯浇鎺т欢
	private Button mBtnPlay, mBtnPause, mBtnStop;  //鎾斁 鏆傚仠 鍋滄鎺у埗鎸夐挳
	private SeekBar mSkbProgress; //杩涘害鏉�
	private NetVideo mNetVedio;   //瑙嗛鎾斁
	public static String mUrl = "";  //鎾斁鐨�

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vedio);
		mSurfaceView = (SurfaceView) this.findViewById(R.id.surfaceView);  
		
		
		mBtnPlay = (Button) this.findViewById(R.id.btnPlayUrl);
		mBtnPlay.setOnClickListener(new ClickEvent());

		mBtnStop = (Button) this.findViewById(R.id.btnStop);
		mBtnStop.setOnClickListener(new ClickEvent());

		mSkbProgress = (SeekBar) this.findViewById(R.id.skbProgress);
		mSkbProgress.setOnSeekBarChangeListener(new SeekBarChangeEvent());
		
		mNetVedio = new NetVideo(mSurfaceView, mSkbProgress, new OnPlayState() {
			@Override
			public void onCompletion() {
				// TODO Auto-generated method stub
				forwordNextActivity(UnityPlayerActivity.class);
			}

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPause() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStop() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPlay() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onInit(String url) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onBufferUpdata(int percent) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	class ClickEvent implements OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v == mBtnPlay) {
				if (mNetVedio.mPlayState == 0) {
					if (mUrl != null && !mUrl.equals("")) {
						mNetVedio.playUrl(mUrl);
						mBtnPlay.setBackgroundResource(R.drawable.play);
					}
				}
				else if(mNetVedio.mPlayState == 2)
				{
					mNetVedio.play();
					mBtnPlay.setBackgroundResource(R.drawable.pause);
				}
				else if(mNetVedio.mPlayState == 3)
				{
					mNetVedio.pause();
					mBtnPlay.setBackgroundResource(R.drawable.play);
				}
				
			} else if (v == mBtnStop) {
				forwordNextActivity(UnityPlayerActivity.class);
				mNetVedio.stop();
			}
		}
	}

	class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {
		int progress;

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			// 鍘熸湰鏄�(progress/seekBar.getMax())*player.mediaPlayer.getDuration()
			//this.progress = progress * player.mediaPlayer.getDuration() / seekBar.getMax();
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// seekTo()鐨勫弬鏁版槸鐩稿涓庡奖鐗囨椂闂寸殑鏁板瓧锛岃�屼笉鏄笌seekBar.getMax()鐩稿鐨勬暟瀛�
			//player.mediaPlayer.seekTo(progress);
		}
	}

}
