package com.aicyber.file;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import com.aicyber.file.WavWriter;
import com.unity3d.player.UnityPlayer;
import android.annotation.SuppressLint;
import android.content.Context;

public class FileOperation {
	
	private String mAudioPath;
	private static FileOperation mInstance;
	
	@SuppressLint("SdCardPath")
	private FileOperation(Context context)
	{
		mAudioPath = "/data/data/" + context.getPackageName() + "/unders3.wav";
		creatAudioFile(mAudioPath);
	}
	
	public static FileOperation getInstance(Context context)
	{
		if (mInstance == null) {
			mInstance = new FileOperation(context);
		}
		return mInstance;
	}

	/**
	 * ???????????§Ó????
	 */
	private void creatAudioFile(String fileName)
	{
		File f = null;
		try {
			f = new File(fileName);
			if (!f.exists()) {  //??????????????????
				f.createNewFile();
			} 
			else {
				f.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ????????????writeBuff§Õ??????????fileName??????§µ?WAV??????????
	 * @param writeBuff ??????????????
	 */
	public void writeFile(Vector<byte[]> writeBuff){
		try {
			//??????????????????????????????????
			File myfile = new File(mAudioPath);
			WavWriter myWavWriter = new WavWriter(myfile, 16000);
			//§Õ??????
			try {										
				int size = writeBuff.size();
				int len = 0;
				for (int i = 0; i < size; i++) {
					len += writeBuff.elementAt(i).length;
				}
				myWavWriter.writeHeader(len);
				for (int i = 0; i < size; i++) {
					myWavWriter.write(writeBuff.elementAt(i)); 
				}
				//???
				myWavWriter.close();
				myWavWriter = null;
				
				//?????????????????´•??
				UnityPlayer.UnitySendMessage("_GameManager", "AndroidPlayByStream", mAudioPath);
				//UnityPlayer.UnitySendMessage("anny", "sendMessageFromAndroid", "0");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
