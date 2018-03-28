package com.aicyber.view;

import java.util.Timer;
import java.util.TimerTask;

import com.aicyber.alter.R;
import com.aicyber.hci.HciCloudManager;
import com.aicyber.play.NetAudio;
import com.aicyber.play.OnPlayState;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class AudioView extends ViewGroup {
	private ImageView mBackground = null; // ����
	private ImageView mControl = null; // ����
	private ImageView mStop = null; // ֹͣ
	private ImageView mCD = null; // CD
	private SeekBar mSeekBar = null; // ������
	private ImageView mCDNeedle = null; // CDָ��
	private TextView mMusicName = null; // ��������
	private TextView mMusicAllTime = null; // �����ܵ�ʱ��
	private TextView mMusicCurTime = null; // ���ֵ�ǰ���ŵ�ʱ��

	private NetAudio mNetAudio = null; // ������Ƶ������

	private float mScal; // �Կ��Ϊ��׼�����ű���
	private final float backgroundPanelWidth = 800.f; // ����ͼƬ�Ŀ��
	private final float backgroundPanelHeight = 480.f; // ����ͼƬ�ĸ߶�

	private Timer mTimer = null;
	private TimerTask mTask_cd = null;
	private TimerTask mTask_play = null;
	private Handler nHandler = null;
	private int mRotation;

	public AudioView(Context context, String url) {
		super(context);
		// TODO Auto-generated constructor stub
		// ��ʼ�������ӿؼ�
		initChilren(context);

		// ��ʼ����Ƶ������
		mNetAudio = new NetAudio();
		mNetAudio.init(new PlayState(), url);

		// ����CD����
		mTimer = new Timer();
	}

	@SuppressLint("ResourceAsColor")
	public void initChilren(Context context) {
		// ����ͼƬ
		mBackground = new ImageView(context);
		mBackground.setBackgroundResource(R.drawable.audio_background);
		addView(mBackground);

		// ���ư�ť
		mControl = new ImageView(context);
		mControl.setBackgroundResource(R.drawable.pause);
		addView(mControl);

		mControl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mNetAudio.getmPlayState() == NetAudio.STATE_PLAY
						|| mNetAudio.getmPlayState() == NetAudio.STATE_START) {
					mNetAudio.pause();
					mControl.setBackgroundResource(R.drawable.play);
				} else if (mNetAudio.getmPlayState() == NetAudio.STATE_PAUSE) {
					mNetAudio.play();
					mControl.setBackgroundResource(R.drawable.pause);
				}
			}
		});

		// ֹͣ��ť
		mStop = new ImageView(context);
		mStop.setBackgroundResource(R.drawable.cha);
		mStop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mNetAudio.stop();
			}
		});
		addView(mStop);

		// CD
		mCD = new ImageView(context);
		mCD.setBackgroundResource(R.drawable.cd_1);
		addView(mCD);

		// CDָ��
		mCDNeedle = new ImageView(context);
		mCDNeedle.setBackgroundResource(R.drawable.cd_needle);
		addView(mCDNeedle);

		// ������
		mSeekBar = new SeekBar(context);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mSeekBar.setLayoutParams(params);
		Drawable drawableProgress = context.getDrawable(R.drawable.po_seekbar);
		mSeekBar.setProgressDrawable(drawableProgress);
		Drawable drawableThumb = context.getDrawable(R.drawable.seekbar_thumb);
		mSeekBar.setThumb(drawableThumb);
		mSeekBar.setMax(100);
		mSeekBar.setFocusable(true);
		mSeekBar.getProgressDrawable().setBounds(0, 0, 4, 4);
		addView(mSeekBar);

		// ��������
		mMusicName = new TextView(context);
		mMusicName.setText("�����Ľ�ɽ��");
		mMusicName.setTextSize(16);
		mMusicName.setTextColor(Color.WHITE);
		addView(mMusicName);

		// ����������ʱ��
		mMusicAllTime = new TextView(context);
		mMusicAllTime.setText("99:99");
		mMusicAllTime.setTextSize(12);
		mMusicAllTime.setTextColor(Color.WHITE);
		addView(mMusicAllTime);

		// �������ŵ�ǰʱ��
		mMusicCurTime = new TextView(context);
		mMusicCurTime.setText("99:99");
		mMusicCurTime.setTextSize(12);
		mMusicCurTime.setTextColor(Color.WHITE);
		addView(mMusicCurTime);
	}

	private void playCDAnimation() {
		// TODO Auto-generated method stub
		// ����
		mRotation = 0;
		nHandler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					if (mNetAudio.getmPlayState() == NetAudio.STATE_PAUSE) {
						mRotation += 0;
					} else {
						mRotation += 10;
						if (mRotation == 360) {
							mRotation = 0;
						}
					}
					mCD.setRotation(mRotation);
				}
				if (msg.what == 2) {
					int position = mNetAudio.getPositionTime();
					int duration = mNetAudio.getDurationTime();
					if (duration > 0) {
						long pos = mSeekBar.getMax() * position / duration;
						mSeekBar.setProgress((int) pos);
					}
					if (mMusicCurTime != null) {
						setTimeText(mMusicCurTime, position);
					}
				}
			}
		};

		mTask_cd = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg = new Message();
				msg.what = 1;
				nHandler.sendMessage(msg);
			}
		};

		mTask_play = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg = new Message();
				msg.what = 2;
				nHandler.sendMessage(msg);
			}
		};
		mTimer.schedule(mTask_cd, 80, 80);
		mTimer.schedule(mTask_play, 1000, 1000);
	}

	private void setTimeText(TextView view, int time) {
		if (view != null) {
			int min = (time / 1000) / 60;
			int sec = (time / 1000) % 60;
			if (min > 9) {
				if (sec > 9) {
					view.setText("" + min + ":" + sec);
				} else {
					view.setText("" + min + ":0" + sec);
				}
			} else {
				if (sec > 9) {
					view.setText("0" + min + ":" + sec);
				} else {
					view.setText("0" + min + ":0" + sec);
				}
			}
		}
	}

	private void clean() {
		// �Ȱ����е�ʱ��ֹͣ
		mTask_cd.cancel();
		mTask_play.cancel();
		mTimer.cancel();
		// ������ֹͣ
		mNetAudio.stop();
		mNetAudio = null;
		// ɾ�����е�VIEW
		this.removeAllViews();
		((ViewGroup) this.getParent()).removeView(this);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		// ����ÿ���Ӻ��ӵ��Ƽ��ߴ�
		measureChildren(widthMeasureSpec, heightMeasureSpec);

		// ȡ�ñ������Ƽ����ȺͿ��
		int background_width = mBackground.getMeasuredWidth();
		int background_height = mBackground.getMeasuredHeight();
		mScal = (background_width / backgroundPanelWidth);
		background_height = (int) (mScal * backgroundPanelHeight);

		// ����View�ĳߴ�
		setMeasuredDimension(background_width, background_height);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		// MarginLayoutParams cParams =
		// (MarginLayoutParams)mControl.getLayoutParams();
		int background_width = mBackground.getMeasuredWidth();
		int background_height = mBackground.getMeasuredHeight();
		background_height = (int) (mScal * backgroundPanelHeight);
		mBackground.layout(0, 0, background_width, background_height);

		int control_width = mControl.getMeasuredWidth();
		int control_height = mControl.getMeasuredHeight();
		int cl = 100;
		int ct = (background_height - control_height) / 2 + control_height * 2 / 3;
		int cr = cl + control_width;
		int cb = ct + control_height;
		mControl.layout(cl, ct, cr, cb);

		int cd_width = mCD.getMeasuredWidth();
		int cd_height = mCD.getMeasuredHeight();
		int cdl = (background_width - cd_width) / 2;
		int cdt = 0;
		int cdr = cdl + cd_width;
		int cdb = cdt + cd_height;
		mCD.layout(cdl, cdt, cdr, cdb);

		int cd_needle_width = mCDNeedle.getMeasuredWidth();
		int cd_needle_height = mCDNeedle.getMeasuredHeight();
		int cd_needlel = background_width / 2 - cd_needle_width / 6;
		int cd_needlet = cd_height * 3 / 4;
		int cd_needler = cd_needlel + cd_needle_width;
		int cd_needleb = cd_needlet + cd_needle_height;
		mCDNeedle.layout(cd_needlel, cd_needlet, cd_needler, cd_needleb);

		int stop_width = mStop.getMeasuredWidth();
		int stop_height = mStop.getMeasuredHeight();

		int stopl = background_width - stop_width;
		int stopt = cd_height / 2;
		int stopr = stopl + stop_width;
		int stopb = stopt + stop_height;
		mStop.layout(stopl, stopt, stopr, stopb);

		int seekBar_width = mSeekBar.getMeasuredWidth();
		int seekBar_height = mSeekBar.getMeasuredHeight();
		int seekl = 100;
		int seekt = background_height * 5 / 6;
		int seekr = (seekl + background_width) - 200;
		int seekb = seekt + seekBar_height;
		mSeekBar.layout(seekl, seekt, seekr, seekb);

		int music_width = mMusicName.getMeasuredWidth();
		int music_height = mMusicName.getMeasuredHeight();
		int musicl = seekr - music_width;
		int musict = seekt - music_height * 2;
		int musicr = musicl + music_width;
		int musicb = musict + music_height;
		mMusicName.layout(musicl, musict, musicr, musicb);

		int alltime_width = mMusicAllTime.getMeasuredWidth();
		int alltime_height = mMusicAllTime.getMeasuredHeight();
		int alltimel = seekr - 20;
		int alltimet = seekt - 3;
		int alltimer = alltimel + alltime_width;
		int alltimeb = alltimet + alltime_height;
		mMusicAllTime.layout(alltimel, alltimet, alltimer, alltimeb);

		int curTime_width = mMusicCurTime.getMeasuredWidth();
		int curTime_height = mMusicCurTime.getMeasuredHeight();
		int curTimel = seekl - curTime_width + 20;
		int curTimet = seekt - 3;
		int curTimer = curTimel + curTime_width;
		int curTimeb = curTimet + curTime_height;
		mMusicCurTime.layout(curTimel, curTimet, curTimer, curTimeb);
	}

	class PlayState implements OnPlayState {

		@Override
		public void onInit(String url) {
			// TODO Auto-generated method stub
			// mNetAudio.playUrl("http://abv.cn/music/�������.mp3");
			mNetAudio.playUrl(url);
		}

		@Override
		public void onStart() {
			// TODO Auto-generated method stub
			if (mMusicAllTime != null) {
				playCDAnimation();
				setTimeText(mMusicAllTime, mNetAudio.getDurationTime());
			}
		}

		@Override
		public void onPause() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPlay() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStop() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCompletion() {
			// TODO Auto-generated method stub
			clean();
			HciCloudManager.getInstance().startRecording();
		}

		@Override
		public void onBufferUpdata(int percent) {
			// TODO Auto-generated method stub
			mSeekBar.setSecondaryProgress(percent);
		}
	}
}
