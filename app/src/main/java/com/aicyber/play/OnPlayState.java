package com.aicyber.play;

public interface OnPlayState {
	public void onInit(String url);
	public void onStart();
	public void onPause();
	public void onPlay();
	public void onStop();
	public void onBufferUpdata(int percent);
	public void onCompletion();
}
