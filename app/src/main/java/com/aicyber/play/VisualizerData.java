package com.aicyber.play;

import android.media.audiofx.Visualizer;

public class VisualizerData {
	
	private Visualizer mVisualizer; // ��Ƶ���ӻ���
	private static final int CAPTURE_SIZE = 256; // ��ȡ��Щ����, ������ʾ
	// ������Ƶ��
	private void startVisualiser() {
		mVisualizer = new Visualizer(0); // ��ʼ��
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
