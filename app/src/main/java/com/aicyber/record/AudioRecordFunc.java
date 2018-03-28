package com.aicyber.record;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;

import com.aicyber.hci.HciCloudManager;
import com.aicyber.util.IOUtil;

import android.R.bool;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.Log;
import peri.vad.VadTester;

public class AudioRecordFunc {

	public final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;   //麦克风输入源
	public final static int AUDIO_SAMPLE_RATE = 16000;    //采样率
	private int bufferSizeInBytes = 0;   //缓冲区大小
	private AudioRecord audioRecord = null; //录音机
	private boolean isRecord; //录音状态
	private Thread mThread;  //采集数据线程
	private VadTester mVadTester; //音频数据处理库接口类
	
    public static final String AudioName = "/data/data/" + "com.example.hellojni" + "/old.pcm";
    //public static final String AudioName = "/sdcard/love.pcm";
    public static final String NewAudioName = "/data/data/" + "com.example.hellojni" + "/new.wav";
    //public static final String NewAudioName = "/sdcard/new.wav";


	private static AudioRecordFunc mInstance;
	private AudioRecordFunc() {
		
		
	}
	public synchronized static AudioRecordFunc getInstance()
	{
		if (mInstance == null)
			mInstance = new AudioRecordFunc();
		return mInstance;
	}
	
	public void initAudioRecord() {
		//创建录音机
		bufferSizeInBytes = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE,/*AudioFormat.CHANNEL_IN_STEREO*/1, AudioFormat.ENCODING_PCM_16BIT);
		audioRecord = new AudioRecord(AUDIO_INPUT, AUDIO_SAMPLE_RATE,/*AudioFormat.CHANNEL_IN_STEREO*/1, AudioFormat.ENCODING_PCM_16BIT, bufferSizeInBytes);
		
		//初始化接口
		mVadTester = new VadTester();
		mVadTester.init(AUDIO_SAMPLE_RATE, 1, 16);
	}


	public void startRecord() {
		if (null != audioRecord) {
			mThread = new Thread(new AudioRecordThread());
			audioRecord.startRecording();
			isRecord = true;
			mThread.start();
		}
	}

	public void stopRecord() {
		if (audioRecord != null) {
			isRecord = false;
			audioRecord.stop();
			mThread = null;
		}
	}
	
	public void releaseRecord(){
		if (audioRecord != null) {
			isRecord = false;
			audioRecord.stop();
			audioRecord.release();
			mThread = null;
		}
	}
	
	/**
	 * 采集数据线程
	 * @author zhisheng
	 *
	 */
	class AudioRecordThread implements Runnable {
		@Override
		public void run() {
			writeDataToFile();
			//copyWaveFile(AudioName, NewAudioName);
		}
	}
	
	
	private void writeDataToFile() {
		byte[] audioData = new byte[bufferSizeInBytes];
		int readSize = 0;
		FileOutputStream fos = IOUtil.getOutputStream(AudioName);
		if (null == fos)
			return;
		int state = 0;
		while (isRecord == true) {
			//取得采样数据
			readSize = audioRecord.read(audioData, 0, bufferSizeInBytes);
			//分析采样数据
			mVadTester.appendData(audioData);
			
			if (state == 0) {
				if (mVadTester.isStarted()) {
					state = 1;
					byte[] useData = mVadTester.getData();
					if (AudioRecord.ERROR_INVALID_OPERATION != readSize) {
						IOUtil.WriteData(fos, useData);
					}
				}
			}
			else if(state == 1)
			{
				IOUtil.WriteData(fos, audioData);
				if (mVadTester.isEnded()) {
					state = 2;
					isRecord = false;
					stopRecord();
					mVadTester.clear();
				}
			}
		}
		IOUtil.closed(fos);
		HciCloudManager.getInstance().Recog(AudioName);
	}
	
	private void copyWaveFile(String inFileName, String outFileName) {
		FileInputStream in = null;
		FileOutputStream out = null;
		long totalAudioLen = 0;
		long totalDataLen = totalAudioLen + 36;
		long longSampleRate = AUDIO_SAMPLE_RATE;
		int channels = 1;
		long byteRate = 16 * AUDIO_SAMPLE_RATE * channels / 8;

		byte[] data = new byte[bufferSizeInBytes];
		try {
			in = new FileInputStream(inFileName);
			out = new FileOutputStream(outFileName);

			totalAudioLen = in.getChannel().size();
			totalDataLen = totalAudioLen + 36;
			WriteWaveFileHeader(out, totalAudioLen, totalDataLen, longSampleRate, channels, byteRate);
			while (in.read(data) != -1) {
				out.write(data);
			}

			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen, long totalDataLen, long longSampleRate,
			int channels, long byteRate) throws IOException {
		byte[] header = new byte[44];
		header[0] = 'R'; // RIFF/WAVE header
		header[1] = 'I';
		header[2] = 'F';
		header[3] = 'F';
		header[4] = (byte) (totalDataLen & 0xff);
		header[5] = (byte) ((totalDataLen >> 8) & 0xff);
		header[6] = (byte) ((totalDataLen >> 16) & 0xff);
		header[7] = (byte) ((totalDataLen >> 24) & 0xff);
		header[8] = 'W';
		header[9] = 'A';
		header[10] = 'V';
		header[11] = 'E';
		header[12] = 'f'; // 'fmt ' chunk
		header[13] = 'm';
		header[14] = 't';
		header[15] = ' ';
		header[16] = 16; // 4 bytes: size of 'fmt ' chunk
		header[17] = 0;
		header[18] = 0;
		header[19] = 0;
		header[20] = 1; // format = 1
		header[21] = 0;
		header[22] = (byte) channels;
		header[23] = 0;
		header[24] = (byte) (longSampleRate & 0xff);
		header[25] = (byte) ((longSampleRate >> 8) & 0xff);
		header[26] = (byte) ((longSampleRate >> 16) & 0xff);
		header[27] = (byte) ((longSampleRate >> 24) & 0xff);
		header[28] = (byte) (byteRate & 0xff);
		header[29] = (byte) ((byteRate >> 8) & 0xff);
		header[30] = (byte) ((byteRate >> 16) & 0xff);
		header[31] = (byte) ((byteRate >> 24) & 0xff);
		header[32] = (byte) (2 * 16 / 8); // block align
		header[33] = 0;
		header[34] = 16; // bits per sample
		header[35] = 0;
		header[36] = 'd';
		header[37] = 'a';
		header[38] = 't';
		header[39] = 'a';
		header[40] = (byte) (totalAudioLen & 0xff);
		header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
		header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
		header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
		out.write(header, 0, 44);
	}

	/**
	 * 降低噪音
	 * @param lin
	 * @param off
	 * @param len
	 */
	void calc1(short[] lin, int off, int len) {
		int i, j;

		for (i = 0; i < len; i++) {
			j = lin[i + off];
			lin[i + off] = (short) (j >> 2);
		}
	}
}
