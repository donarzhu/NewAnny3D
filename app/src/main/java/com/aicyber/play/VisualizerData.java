package com.aicyber.play;

import android.media.audiofx.Visualizer;

public class VisualizerData {
	
	private Visualizer mVisualizer; // 音频可视化类
	private static final int CAPTURE_SIZE = 256; // 获取这些数据, 用于显示
	// 设置音频线
	private void startVisualiser() {
		mVisualizer = new Visualizer(0); // 初始化
		mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
			@Override
			public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
				/*
				if (mWvWaveform != null) {
					mWvWaveform.setWaveform(waveform);
				}*/
			}

			@Override
			public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
			}
		}, Visualizer.getMaxCaptureRate(), true, false);
		mVisualizer.setCaptureSize(CAPTURE_SIZE);
		mVisualizer.setEnabled(true);
	}

}
